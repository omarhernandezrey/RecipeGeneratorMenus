@file:Suppress("SpellCheckingInspection")

package com.example.recipe_generator.di

import android.content.Context
import com.example.recipe_generator.data.local.AppDatabase
import com.example.recipe_generator.data.repository.FirebaseAuthRepository
import com.example.recipe_generator.data.repository.MockAuthRepository
import com.example.recipe_generator.data.repository.RecipeRepositoryImpl
import com.example.recipe_generator.data.repository.RoomFavoritesRepositoryImpl
import com.example.recipe_generator.data.repository.UserProfileRepositoryImpl
import com.example.recipe_generator.data.repository.UserPrefsRepositoryImpl
import com.example.recipe_generator.data.repository.UserRecipeRepositoryImpl
import com.example.recipe_generator.data.repository.WeeklyPlanRepositoryImpl
import com.example.recipe_generator.data.sync.FirestoreWeeklyPlanSync
import com.example.recipe_generator.domain.repository.AuthRepository
import com.example.recipe_generator.domain.repository.FavoritesRepository
import com.example.recipe_generator.domain.repository.RecipeRepository
import com.example.recipe_generator.domain.repository.UserProfileRepository
import com.example.recipe_generator.domain.repository.UserPrefsRepository
import com.example.recipe_generator.domain.repository.UserRecipeRepository
import com.example.recipe_generator.domain.repository.WeeklyPlanRepository
import com.example.recipe_generator.domain.usecase.GenerateMenuUseCase
import com.example.recipe_generator.domain.usecase.GetMenuForDayUseCase
import com.example.recipe_generator.domain.usecase.GetRecipeDetailUseCase
import com.example.recipe_generator.domain.usecase.ToggleFavoriteUseCase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Contenedor de dependencias manual — sin Hilt ni Dagger.
 *
 * Actúa como Service Locator para toda la aplicación.
 * Instanciado una sola vez en RecipeGeneratorApp.onCreate().
 *
 * FLUJO DE DEPENDENCIAS (Dependency Rule — Clean Architecture):
 *
 *   Presentation → Domain ← Data
 *
 * La capa de Dominio NO conoce a Presentation ni a Data.
 * AppContainer construye las implementaciones concretas de Data
 * e inyecta las interfaces de Dominio hacia arriba (Presentation).
 *
 * Capa: DI (infraestructura transversal)
 *
 * NOTA: Las implementaciones concretas (RecipeRepositoryImpl, etc.)
 * se instanciarán aquí en F2-21 y F3-29/F3-30 cuando se cree Room
 * y DataStore. Por ahora las propiedades están declaradas como lateinit
 * para dejar explícita la arquitectura.
 */
class AppContainer(private val context: Context) {
    private val appContext = context.applicationContext

    // ── Base de datos Room (F3-29) ────────────────────────────────────
    private val database: AppDatabase by lazy {
        AppDatabase.getInstance(appContext)
    }

    // ── Firebase Authentication ────────────────────────────────────────
    private val firebaseAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val firebaseFirestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    // ── Repositorios (interfaces de Dominio, impls de Datos) ──────────
    // En F3-29 FavoritesRepository migra a Room.

    val authRepository: AuthRepository by lazy {
        try {
            // Intenta usar Firebase, si falla usa Mock para desarrollo
            FirebaseAuthRepository(firebaseAuth)
        } catch (e: Exception) {
            android.util.Log.w("AppContainer", "Firebase no disponible, usando MockAuthRepository: ${e.message}")
            MockAuthRepository()
        }
    }

    val recipeRepository: RecipeRepository by lazy {
        RecipeRepositoryImpl(database.recipeDao())
    }

    val favoritesRepository: FavoritesRepository by lazy {
        RoomFavoritesRepositoryImpl(
            favoriteDao = database.favoriteDao(),
            recipeRepository = recipeRepository
        )
    }

    val userPrefsRepository: UserPrefsRepository by lazy {
        UserPrefsRepositoryImpl(appContext)
    }

    val userRecipeRepository: UserRecipeRepository by lazy {
        UserRecipeRepositoryImpl(
            userRecipeDao = database.userRecipeDao(),
            weeklyPlanDao = database.weeklyPlanDao()
        )
    }

    val weeklyPlanRepository: WeeklyPlanRepository by lazy {
        WeeklyPlanRepositoryImpl(
            weeklyPlanDao = database.weeklyPlanDao(),
            userRecipeDao = database.userRecipeDao(),
            recipeDao = database.recipeDao()
        )
    }

    val userProfileRepository: UserProfileRepository by lazy {
        UserProfileRepositoryImpl(database.userProfileDao())
    }

    val firestoreWeeklyPlanSync: FirestoreWeeklyPlanSync by lazy {
        FirestoreWeeklyPlanSync(
            firestore = firebaseFirestore,
            weeklyPlanDao = database.weeklyPlanDao(),
            userRecipeDao = database.userRecipeDao(),
            recipeDao = database.recipeDao()
        )
    }

    /**
     * UID actual que debe pasarse a los repositorios de datos por usuario.
     * Lanza excepción si la capa de presentación intenta usar estos repos
     * sin tener una sesión autenticada activa.
     */
    fun requireAuthenticatedUserId(): String =
        authRepository.getCurrentUserId()
            ?: throw IllegalStateException("No hay usuario autenticado")

    // ── Casos de Uso ─────────────────────────────────────────────────

    val getMenuForDayUseCase: GetMenuForDayUseCase
        get() = GetMenuForDayUseCase(recipeRepository)

    val getRecipeDetailUseCase: GetRecipeDetailUseCase
        get() = GetRecipeDetailUseCase(recipeRepository)

    val toggleFavoriteUseCase: ToggleFavoriteUseCase
        get() = ToggleFavoriteUseCase(favoritesRepository)

    val generateMenuUseCase: GenerateMenuUseCase
        get() = GenerateMenuUseCase(recipeRepository)
}
