package com.example.recipe_generator.presentation.myrecipes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipe_generator.data.sync.FirestoreSyncService
import com.example.recipe_generator.domain.model.AppNotification
import com.example.recipe_generator.domain.model.UserRecipe
import com.example.recipe_generator.domain.repository.AppNotificationRepository
import com.example.recipe_generator.domain.repository.UserRecipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

data class RecipeFormUiState(
    /** UUID siempre disponible — se usa como nombre de archivo de la imagen */
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
    /** file:// URI local o cadena vacía si no tiene imagen */
    val imageRes: String = "",
    val isSaving: Boolean = false,
    val error: String? = null,
    val saveVersion: Int = 0,
    val createdAt: Long? = null
)

class CreateRecipeViewModel(
    private val userId: String,
    private val userRecipeRepository: UserRecipeRepository,
    private val firestoreSyncService: FirestoreSyncService,
    private val appNotificationRepository: AppNotificationRepository? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecipeFormUiState())
    val uiState: StateFlow<RecipeFormUiState> = _uiState.asStateFlow()

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
            createdAt = recipe.createdAt
        )
    }

    fun saveRecipe() {
        val current = _uiState.value
        val validationError = validate(current)
        if (validationError != null) {
            updateState { copy(error = validationError) }
            return
        }

        viewModelScope.launch {
            updateState { copy(isSaving = true, error = null) }

            val recipe = current.toRecipe(userId)

            runCatching {
                if (current.recipeId == null) {
                    userRecipeRepository.addRecipe(recipe)
                } else {
                    userRecipeRepository.updateRecipe(recipe)
                }
                firestoreSyncService.uploadRecipe(recipe)
            }.onSuccess {
                // Guardar notificación in-app
                appNotificationRepository?.insert(AppNotification(
                    id        = UUID.randomUUID().toString(),
                    title     = "Receta creada",
                    body      = "\"${current.title.trim().ifBlank { "Nueva receta" }}\" se guardó en Mis Recetas",
                    type      = "recipe_created",
                    createdAt = System.currentTimeMillis()
                ))
                updateState { copy(isSaving = false, saveVersion = saveVersion + 1) }
            }.onFailure { throwable ->
                updateState {
                    copy(
                        isSaving = false,
                        error = throwable.message ?: "No se pudo guardar la receta"
                    )
                }
            }
        }
    }

    private fun validate(state: RecipeFormUiState): String? {
        if (state.title.isBlank()) return "El título es obligatorio"
        if (state.description.isBlank()) return "La descripción es obligatoria"
        if (state.ingredients.none { it.isNotBlank() }) return "Agrega al menos un ingrediente"
        if (state.steps.none { it.isNotBlank() }) return "Agrega al menos un paso"
        return null
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
    timeInMinutes = timeInMinutes.toIntOrNull() ?: 0,
    calories = calories.toIntOrNull() ?: 0,
    difficulty = sliderValueToDifficulty(difficultyLevel),
    category = category,
    description = description.trim(),
    dayOfWeek = dayOfWeek,
    mealType = mealType,
    ingredients = ingredients.map(String::trim).filter(String::isNotBlank),
    steps = steps.map(String::trim).filter(String::isNotBlank),
    createdAt = createdAt ?: System.currentTimeMillis(),
    updatedAt = System.currentTimeMillis()
)
