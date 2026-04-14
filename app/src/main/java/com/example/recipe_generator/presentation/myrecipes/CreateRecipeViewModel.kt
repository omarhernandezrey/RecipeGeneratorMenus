package com.example.recipe_generator.presentation.myrecipes

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipe_generator.data.remote.GeminiRecipeDataSource
import com.example.recipe_generator.data.remote.RecipeImageResolver
import com.example.recipe_generator.data.sync.FirestoreSyncService
import com.example.recipe_generator.domain.model.AppNotification
import com.example.recipe_generator.domain.model.UserRecipe
import com.example.recipe_generator.domain.repository.AppNotificationRepository
import com.example.recipe_generator.domain.repository.UserRecipeRepository
import com.example.recipe_generator.domain.usecase.ResolveRecipeVideoUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

data class RecipeFormUiState(
    val recipeId: String = UUID.randomUUID().toString(),
    val title: String = "",
    val description: String = "",
    val timeInMinutes: String = "",
    val calories: String = "",
    val difficultyLevel: Float = 0f,
    val category: String = recipeCategories.first(),
    val dayOfWeek: String = recipeDaysOfWeek.first(),
    val mealType: String = mealTypeOptions.first(),
    val ingredients: List<String> = listOf(""),
    val steps: List<String> = listOf(""),
    val imageRes: String = "",
    val imageUrl: String? = null,
    val videoYoutube: String? = null,
    val isEditMode: Boolean = false,
    val isSaving: Boolean = false,
    val isGenerating: Boolean = false,
    val error: String? = null,
    val saveVersion: Int = 0,
    val createdAt: Long? = null
)

class CreateRecipeViewModel(
    private val userId: String,
    private val userRecipeRepository: UserRecipeRepository,
    private val firestoreSyncService: FirestoreSyncService,
    private val resolveRecipeVideoUseCase: ResolveRecipeVideoUseCase,
    private val geminiDataSource: GeminiRecipeDataSource? = null,
    private val appNotificationRepository: AppNotificationRepository? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecipeFormUiState())
    val uiState: StateFlow<RecipeFormUiState> = _uiState.asStateFlow()

    fun generateWithAI(prompt: String) {
        if (prompt.isBlank()) return
        if (geminiDataSource == null) {
            updateState { copy(error = "Error: El motor de IA no está configurado.") }
            return
        }
        
        viewModelScope.launch {
            updateState { copy(isGenerating = true, error = null) }
            Log.d("GEMINI_VM", "Iniciando generación para: $prompt")
            
            try {
                val json = geminiDataSource.generateRecipe(prompt, _uiState.value.category)
                
                if (json != null) {
                    Log.d("GEMINI_VM", "JSON recibido con éxito")
                    val searchKeywords = json.optString("imageSearchKeywords", prompt)
                    val foundImageUrl = RecipeImageResolver.resolveWithKeywords(searchKeywords)
                    
                    updateState {
                        copy(
                            title = json.optString("title", prompt),
                            description = json.optString("description", ""),
                            timeInMinutes = json.optInt("timeInMinutes", 30).toString(),
                            calories = json.optInt("calories", 400).toString(),
                            ingredients = List(json.optJSONArray("ingredients")?.length() ?: 0) { i -> 
                                json.getJSONArray("ingredients").getString(i) 
                            }.ifEmpty { listOf("") },
                            steps = List(json.optJSONArray("steps")?.length() ?: 0) { i -> 
                                json.getJSONArray("steps").getString(i) 
                            }.ifEmpty { listOf("") },
                            imageUrl = if (foundImageUrl.isNotBlank()) foundImageUrl else null,
                            isGenerating = false
                        )
                    }
                } else {
                    Log.w("GEMINI_VM", "El servidor de IA devolvió nulo o formato incorrecto")
                    updateState { copy(isGenerating = false, error = "La IA no pudo procesar la receta. Intenta con otro nombre.") }
                }
            } catch (e: Exception) {
                Log.e("GEMINI_VM", "Error fatal en ViewModel: ${e.message}")
                updateState { copy(isGenerating = false, error = "Error de conexión con la IA: ${e.message}") }
            }
        }
    }

    fun updateImageRes(value: String) = updateState { copy(imageRes = value) }
    fun updateTitle(value: String) = updateState { copy(title = value) }
    fun updateDescription(value: String) = updateState { copy(description = value) }
    fun updateTimeInMinutes(value: String) = updateState { copy(timeInMinutes = value.filter(Char::isDigit)) }
    fun updateCalories(value: String) = updateState { copy(calories = value.filter(Char::isDigit)) }
    fun updateDifficulty(value: Float) = updateState { copy(difficultyLevel = value) }
    fun updateCategory(value: String) = updateState { copy(category = value) }
    fun updateDayOfWeek(value: String) = updateState { copy(dayOfWeek = value) }
    fun updateMealType(value: String) = updateState { copy(mealType = value) }
    fun clearError() = updateState { copy(error = null) }

    fun addIngredient() = updateState { copy(ingredients = ingredients + "") }
    fun updateIngredient(index: Int, value: String) = updateState {
        copy(ingredients = ingredients.toMutableList().also { it[index] = value })
    }
    fun removeIngredient(index: Int) = updateState {
        if (ingredients.size == 1) copy(ingredients = listOf(""))
        else copy(ingredients = ingredients.toMutableList().also { it.removeAt(index) })
    }

    fun addStep() = updateState { copy(steps = steps + "") }
    fun updateStep(index: Int, value: String) = updateState {
        copy(steps = steps.toMutableList().also { it[index] = value })
    }
    fun removeStep(index: Int) = updateState {
        if (steps.size == 1) copy(steps = listOf(""))
        else copy(steps = steps.toMutableList().also { it.removeAt(index) })
    }

    fun populateFromRecipe(recipe: UserRecipe) {
        _uiState.value = RecipeFormUiState(
            recipeId = recipe.id,
            title = recipe.title,
            description = recipe.description,
            timeInMinutes = recipe.timeInMinutes.takeIf { it > 0 }?.toString().orEmpty(),
            calories = recipe.calories.takeIf { it > 0 }?.toString().orEmpty(),
            difficultyLevel = difficultyToSliderValue(recipe.difficulty),
            category = recipe.category.ifBlank { recipeCategories.first() },
            dayOfWeek = recipe.dayOfWeek.ifBlank { recipeDaysOfWeek.first() },
            mealType = recipe.mealType.ifBlank { mealTypeOptions.first() },
            ingredients = recipe.ingredients.ifEmpty { listOf("") },
            steps = recipe.steps.ifEmpty { listOf("") },
            imageRes = recipe.imageRes,
            imageUrl = recipe.imageUrl,
            videoYoutube = recipe.videoYoutube,
            isEditMode = true,
            createdAt = recipe.createdAt
        )
    }

    fun saveRecipe() {
        val current = _uiState.value
        if (current.title.isBlank()) {
            updateState { copy(error = "El título es obligatorio") }
            return
        }

        viewModelScope.launch {
            updateState { copy(isSaving = true, error = null) }

            val resolvedVideo = runCatching {
                resolveRecipeVideoUseCase(
                    currentVideoUrl = current.videoYoutube,
                    recipeTitle = current.title.trim()
                )
            }.getOrElse {
                current.videoYoutube ?: ""
            }

            val recipe = current.copy(videoYoutube = resolvedVideo).toRecipe(userId)

            runCatching {
                if (current.isEditMode) userRecipeRepository.updateRecipe(recipe)
                else userRecipeRepository.addRecipe(recipe)
                firestoreSyncService.uploadRecipe(recipe)
            }.onSuccess {
                appNotificationRepository?.insert(AppNotification(
                    id        = UUID.randomUUID().toString(),
                    title     = "Receta guardada",
                    body      = "\"${current.title.trim()}\" se añadió a tu colección",
                    type      = "recipe_created",
                    createdAt = System.currentTimeMillis()
                ))
                updateState { copy(isSaving = false, saveVersion = saveVersion + 1) }
            }.onFailure { throwable ->
                updateState { copy(isSaving = false, error = throwable.message ?: "Error al guardar") }
            }
        }
    }

    private fun updateState(transform: RecipeFormUiState.() -> RecipeFormUiState) {
        _uiState.value = _uiState.value.transform()
    }
}

internal val recipeCategories = listOf("Desayuno", "Almuerzo", "Cena", "Snack", "Postre")
internal val recipeDaysOfWeek = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")
internal val mealTypeOptions = listOf("Desayuno", "Almuerzo", "Cena")
internal val difficultyLabels = listOf("Fácil", "Medio", "Difícil")

internal fun difficultyToSliderValue(difficulty: String): Float =
    when (difficulty) {
        "Medio" -> 1f
        "Difícil" -> 2f
        else -> 0f
    }

internal fun sliderValueToDifficulty(value: Float): String =
    difficultyLabels[value.toInt().coerceIn(0, difficultyLabels.lastIndex)]

private fun RecipeFormUiState.toRecipe(userId: String): UserRecipe = UserRecipe(
    id = recipeId,
    userId = userId,
    title = title.trim(),
    imageRes = imageRes,
    imageUrl = imageUrl,
    timeInMinutes = timeInMinutes.toIntOrNull() ?: 0,
    calories = calories.toIntOrNull() ?: 0,
    difficulty = sliderValueToDifficulty(difficultyLevel),
    category = category,
    description = description.trim(),
    dayOfWeek = dayOfWeek,
    mealType = mealType,
    videoYoutube = videoYoutube,
    ingredients = ingredients.map(String::trim).filter(String::isNotBlank),
    steps = steps.map(String::trim).filter(String::isNotBlank),
    createdAt = createdAt ?: System.currentTimeMillis(),
    updatedAt = System.currentTimeMillis()
)
