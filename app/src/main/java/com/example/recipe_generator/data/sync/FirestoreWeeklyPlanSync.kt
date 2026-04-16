package com.example.recipe_generator.data.sync

import android.util.Log
import com.example.recipe_generator.data.local.dao.RecipeDao
import com.example.recipe_generator.data.local.dao.UserRecipeDao
import com.example.recipe_generator.data.local.dao.WeeklyPlanDao
import com.example.recipe_generator.data.local.entity.WeeklyPlanEntity
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * Sincroniza el plan semanal del usuario entre Room y Firestore.
 *
 * Estructura remota:
 * users/{uid}/weeklyPlan/{day}
 *   - breakfast: recipeId | null
 *   - lunch: recipeId | null
 *   - dinner: recipeId | null
 *
 * La fuente remota usa listeners en tiempo real y Room actúa como cache local.
 * Los títulos se reconstruyen localmente usando las tablas recipes y user_recipes.
 *
 * C-13
 * Capa: Data / Sync
 */
class FirestoreWeeklyPlanSync(
    private val firestore: FirebaseFirestore,
    private val weeklyPlanDao: WeeklyPlanDao,
    private val userRecipeDao: UserRecipeDao,
    private val recipeDao: RecipeDao,
    private val syncScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
) {

    private var weeklyPlanListener: ListenerRegistration? = null

    /** Descarga el plan semanal remoto y reemplaza el cache local del usuario. */
    suspend fun syncRemoteToLocal(userId: String) {
        val snapshot = weeklyPlanCollection(userId).get().await()
        applyRemoteSnapshot(userId, snapshot.documents)
    }

    /** Sube el plan semanal local completo a Firestore. */
    suspend fun syncLocalToRemote(userId: String) {
        val localEntries = weeklyPlanDao.getPlanForUser(userId).first()
        val entriesByDay = localEntries.groupBy { it.dayOfWeek }
        val daysToSync = (DAYS_OF_WEEK + entriesByDay.keys).distinct()
        val batch = firestore.batch()

        daysToSync.forEach { day ->
            val dayEntries = entriesByDay[day].orEmpty()
            val document = weeklyPlanCollection(userId).document(day)

            if (dayEntries.isEmpty()) {
                batch.delete(document)
            } else {
                batch.set(document, buildRemoteDayDocument(dayEntries))
            }
        }

        batch.commit().await()
    }

    /** Sube solo un día del plan local a Firestore. */
    suspend fun syncDayToRemote(userId: String, day: String) {
        val dayEntries = weeklyPlanDao.getPlanForDay(userId, day).first()
        val document = weeklyPlanCollection(userId).document(day)

        if (dayEntries.isEmpty()) {
            document.delete().await()
        } else {
            document.set(buildRemoteDayDocument(dayEntries)).await()
        }
    }

    /** Inicia un listener de Firestore para mantener Room sincronizado en tiempo real. */
    fun startRealtimeSync(userId: String) {
        stopRealtimeSync()

        weeklyPlanListener = weeklyPlanCollection(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Error escuchando weeklyPlan para $userId: ${error.message}", error)
                    return@addSnapshotListener
                }

                if (snapshot == null) return@addSnapshotListener

                syncScope.launch {
                    try {
                        applyRemoteSnapshot(userId, snapshot.documents)
                    } catch (e: Exception) {
                        Log.e(TAG, "Error aplicando snapshot weeklyPlan para $userId: ${e.message}", e)
                    }
                }
            }
    }

    /** Detiene el listener activo del plan semanal. */
    fun stopRealtimeSync() {
        weeklyPlanListener?.remove()
        weeklyPlanListener = null
    }

    private suspend fun applyRemoteSnapshot(
        userId: String,
        documents: List<DocumentSnapshot>
    ) {
        val currentLocalEntries = weeklyPlanDao.getPlanForUser(userId).first()
        val currentLocalBySlot = currentLocalEntries.associateBy { slotKey(it.dayOfWeek, it.mealType) }

        val remoteEntries = documents.flatMap { document ->
            documentToEntities(
                userId = userId,
                day = document.id,
                data = document.data.orEmpty(),
                currentLocalBySlot = currentLocalBySlot
            )
        }

        weeklyPlanDao.deleteAllByUser(userId)

        if (remoteEntries.isNotEmpty()) {
            weeklyPlanDao.upsertAll(remoteEntries)
        }
    }

    private suspend fun documentToEntities(
        userId: String,
        day: String,
        data: Map<String, Any>,
        currentLocalBySlot: Map<String, WeeklyPlanEntity>
    ): List<WeeklyPlanEntity> {
        return REMOTE_FIELD_TO_MEAL_TYPE.mapNotNull { (fieldName, mealType) ->
            val recipeId = data[fieldName] as? String
            if (recipeId.isNullOrBlank()) return@mapNotNull null

            val localSlot = currentLocalBySlot[slotKey(day, mealType)]
            val resolvedTitle = when {
                localSlot?.recipeId == recipeId && localSlot.recipeTitle.isNotBlank() ->
                    localSlot.recipeTitle
                else -> resolveRecipeTitle(userId, recipeId)
            }
            val resolvedImage = when {
                localSlot?.recipeId == recipeId && localSlot.imageRes.isNotBlank() ->
                    localSlot.imageRes
                else -> resolveRecipeImage(userId, recipeId)
            }

            WeeklyPlanEntity(
                userId = userId,
                dayOfWeek = day,
                mealType = mealType,
                recipeId = recipeId,
                recipeTitle = resolvedTitle,
                imageRes = resolvedImage,
                notes = localSlot?.notes.orEmpty(),
                updatedAt = System.currentTimeMillis()
            )
        }
    }

    private suspend fun resolveRecipeTitle(userId: String, recipeId: String): String {
        val userRecipeTitle = userRecipeDao.getById(recipeId)
            ?.takeIf { it.userId == userId }
            ?.title

        if (!userRecipeTitle.isNullOrBlank()) {
            return userRecipeTitle
        }

        return recipeDao.getRecipeById(recipeId)?.title.orEmpty()
    }

    private suspend fun resolveRecipeImage(userId: String, recipeId: String): String {
        userRecipeDao.getById(recipeId)
            ?.takeIf { it.userId == userId }
            ?.imageRes
            ?.takeIf { it.isNotBlank() }
            ?.let { return it }

        return recipeDao.getRecipeById(recipeId)?.imageRes.orEmpty()
    }

    private fun buildRemoteDayDocument(entries: List<WeeklyPlanEntity>): Map<String, String?> {
        val entriesByMealType = entries.associateBy { it.mealType }

        return mapOf(
            REMOTE_BREAKFAST to entriesByMealType[LOCAL_BREAKFAST]?.recipeId,
            REMOTE_LUNCH to entriesByMealType[LOCAL_LUNCH]?.recipeId,
            REMOTE_DINNER to entriesByMealType[LOCAL_DINNER]?.recipeId
        )
    }

    private fun weeklyPlanCollection(userId: String) =
        firestore.collection(USERS_COLLECTION)
            .document(userId)
            .collection(WEEKLY_PLAN_COLLECTION)

    private fun slotKey(day: String, mealType: String): String = "$day|$mealType"

    private companion object {
        private const val TAG = "FirestoreWeeklyPlanSync"
        private const val USERS_COLLECTION = "users"
        private const val WEEKLY_PLAN_COLLECTION = "weeklyPlan"
        private const val REMOTE_BREAKFAST = "breakfast"
        private const val REMOTE_LUNCH = "lunch"
        private const val REMOTE_DINNER = "dinner"
        private const val LOCAL_BREAKFAST = "Desayuno"
        private const val LOCAL_LUNCH = "Almuerzo"
        private const val LOCAL_DINNER = "Cena"

        private val DAYS_OF_WEEK = listOf(
            "Lunes",
            "Martes",
            "Miércoles",
            "Jueves",
            "Viernes",
            "Sábado",
            "Domingo"
        )

        private val REMOTE_FIELD_TO_MEAL_TYPE = linkedMapOf(
            REMOTE_BREAKFAST to LOCAL_BREAKFAST,
            REMOTE_LUNCH to LOCAL_LUNCH,
            REMOTE_DINNER to LOCAL_DINNER
        )
    }
}
