@file:Suppress("SpellCheckingInspection")

package com.example.recipe_generator.di

import android.content.Context
import com.example.recipe_generator.BuildConfig
import com.example.recipe_generator.data.local.AppDatabase
import com.example.recipe_generator.data.remote.GeminiRecipeDataSource
import com.example.recipe_generator.data.remote.GlobalRecipeFirestoreDataSource
import com.example.recipe_generator.data.remote.RecipeVideoResolver
import com.example.recipe_generator.data.remote.TheMealDbServiceFactory
import com.example.recipe_generator.data.repository.AppNotificationRepositoryImpl
import com.example.recipe_generator.data.repository.FirebaseAuthRepository
import com.example.recipe_generator.data.repository.GlobalRecipeSearchRepositoryImpl
import com.example.recipe_generator.data.repository.MockAuthRepository
import com.example.recipe_generator.data.repository.RecipeVideoRepositoryImpl
import com.example.recipe_generator.data.repository.RecipeRepositoryImpl
import com.example.recipe_generator.data.repository.RoomFavoritesRepositoryImpl
import com.example.recipe_generator.data.repository.UserProfileRepositoryImpl
import com.example.recipe_generator.data.repository.UserPrefsRepositoryImpl
import com.example.recipe_generator.data.repository.UserRecipeRepositoryImpl
import com.example.recipe_generator.data.repository.WeeklyPlanRepositoryImpl
import com.example.recipe_generator.data.sync.FirestoreSyncService
import com.example.recipe_generator.data.sync.FirestoreWeeklyPlanSync
import com.example.recipe_generator.data.sync.RecipeVideoStorageService
import com.example.recipe_generator.domain.repository.AppNotificationRepository
import com.example.recipe_generator.domain.repository.AuthRepository
import com.example.recipe_generator.domain.repository.FavoritesRepository
import com.example.recipe_generator.domain.repository.GlobalRecipeSearchRepository
import com.example.recipe_generator.domain.repository.RecipeRepository
import com.example.recipe_generator.domain.repository.RecipeVideoRepository
import com.example.recipe_generator.domain.repository.UserProfileRepository
import com.example.recipe_generator.domain.repository.UserPrefsRepository
import com.example.recipe_generator.domain.repository.UserRecipeRepository
import com.example.recipe_generator.domain.repository.WeeklyPlanRepository
import com.example.recipe_generator.domain.usecase.EnsureRecipeVideoUseCase
import com.example.recipe_generator.domain.usecase.GenerateMenuUseCase
import com.example.recipe_generator.domain.usecase.GetMenuForDayUseCase
import com.example.recipe_generator.domain.usecase.GetRecipeDetailUseCase
import com.example.recipe_generator.domain.usecase.ResolveRecipeVideoUseCase
import com.example.recipe_generator.domain.usecase.SearchGlobalRecipeUseCase
import com.example.recipe_generator.domain.usecase.ToggleFavoriteUseCase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Contenedor de dependencias manual — sin Hilt ni Dagger.
 */
class AppContainer(private val context: Context) {
    private val appContext = context.applicationContext

    // ── Clave Gemini (A-AI) ──────────────────────────────────────────
    private val GEMINI_API_KEY = "AIzaSyB3kfS_PTXewRw1kPZHaAaOjwsHSKuN1nU"

    // ── Base de datos Room ────────────────────────────────────────────
    private val database: AppDatabase by lazy {
        AppDatabase.getInstance(appContext)
    }

    // ── Firebase ──────────────────────────────────────────────────────
    private val firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val firebaseFirestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val firebaseStorage: FirebaseStorage by lazy { FirebaseStorage.getInstance() }

    // ── Data Sources Remotos ──────────────────────────────────────────
    val geminiRecipeDataSource: GeminiRecipeDataSource by lazy {
        GeminiRecipeDataSource(GEMINI_API_KEY)
    }

    private val theMealDbService by lazy { TheMealDbServiceFactory.create() }

    private val globalRecipeFirestoreDataSource: GlobalRecipeFirestoreDataSource by lazy {
        GlobalRecipeFirestoreDataSource(firebaseFirestore)
    }

    // ── Repositorios ──────────────────────────────────────────────────
    val authRepository: AuthRepository by lazy {
        try {
            FirebaseAuthRepository(firebaseAuth)
        } catch (e: Exception) {
            MockAuthRepository()
        }
    }

    val recipeRepository: RecipeRepository by lazy {
        RecipeRepositoryImpl(database.recipeDao())
    }

    val favoritesRepository: FavoritesRepository by lazy {
        RoomFavoritesRepositoryImpl(database.favoriteDao(), recipeRepository)
    }

    val userPrefsRepository: UserPrefsRepository by lazy {
        UserPrefsRepositoryImpl(appContext)
    }

    val userRecipeRepository: UserRecipeRepository by lazy {
        UserRecipeRepositoryImpl(database.userRecipeDao(), database.weeklyPlanDao())
    }

    val weeklyPlanRepository: WeeklyPlanRepository by lazy {
        WeeklyPlanRepositoryImpl(database.weeklyPlanDao(), database.userRecipeDao(), database.recipeDao())
    }

    val userProfileRepository: UserProfileRepository by lazy {
        UserProfileRepositoryImpl(database.userProfileDao())
    }

    val appNotificationRepository: AppNotificationRepository by lazy {
        AppNotificationRepositoryImpl(database.appNotificationDao())
    }

    val recipeVideoStorageService: RecipeVideoStorageService by lazy {
        RecipeVideoStorageService(firebaseStorage)
    }

    val recipeVideoRepository: RecipeVideoRepository by lazy {
        RecipeVideoRepositoryImpl(firebaseFirestore, recipeVideoStorageService, BuildConfig.YOUTUBE_API_KEY)
    }

    val globalRecipeSearchRepository: GlobalRecipeSearchRepository by lazy {
        GlobalRecipeSearchRepositoryImpl(globalRecipeFirestoreDataSource, theMealDbService)
    }

    // ── Servicios de Sincronización ───────────────────────────────────
    val firestoreSyncService: FirestoreSyncService by lazy {
        FirestoreSyncService(firebaseFirestore, database.userRecipeDao(), database.userProfileDao(), recipeVideoStorageService)
    }

    val firestoreWeeklyPlanSync: FirestoreWeeklyPlanSync by lazy {
        FirestoreWeeklyPlanSync(firebaseFirestore, database.weeklyPlanDao(), database.userRecipeDao(), database.recipeDao())
    }

    // ── Estado de Sincronización ──────────────────────────────────────
    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()
    fun setSyncing(value: Boolean) { _isSyncing.value = value }

    fun requireAuthenticatedUserId(): String =
        authRepository.getCurrentUserId() ?: throw IllegalStateException("No hay usuario autenticado")

    suspend fun clearLocalUserData(userId: String) {
        database.favoriteDao().deleteAllByUser(userId)
        database.weeklyPlanDao().deleteAllByUser(userId)
        firestoreSyncService.clearLocalData(userId)
    }

    // ── Casos de Uso ─────────────────────────────────────────────────
    val getMenuForDayUseCase: GetMenuForDayUseCase get() = GetMenuForDayUseCase(recipeRepository)
    val getRecipeDetailUseCase: GetRecipeDetailUseCase get() = GetRecipeDetailUseCase(recipeRepository)
    val toggleFavoriteUseCase: ToggleFavoriteUseCase get() = ToggleFavoriteUseCase(favoritesRepository)
    
    // Aquí es donde la IA toma el control
    val generateMenuUseCase: GenerateMenuUseCase 
        get() = GenerateMenuUseCase(recipeRepository, geminiRecipeDataSource)

    val ensureRecipeVideoUseCase: EnsureRecipeVideoUseCase get() = EnsureRecipeVideoUseCase(recipeVideoRepository)
    val resolveRecipeVideoUseCase: ResolveRecipeVideoUseCase get() = ResolveRecipeVideoUseCase(recipeVideoRepository)
    val searchGlobalRecipeUseCase: SearchGlobalRecipeUseCase get() = SearchGlobalRecipeUseCase(globalRecipeSearchRepository)
}
