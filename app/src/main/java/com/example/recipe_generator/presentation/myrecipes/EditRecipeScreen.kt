package com.example.recipe_generator.presentation.myrecipes

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.recipe_generator.RecipeGeneratorApp
import com.example.recipe_generator.domain.model.UserRecipe

@Composable
fun EditRecipeScreen(
    recipe: UserRecipe,
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onSaved: () -> Unit = {}
) {
    val appContainer = (LocalContext.current.applicationContext as RecipeGeneratorApp).container

    val viewModel: CreateRecipeViewModel = viewModel(
        key = "edit-recipe-${recipe.id}",
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return CreateRecipeViewModel(
                    userId = recipe.userId,
                    userRecipeRepository = appContainer.userRecipeRepository,
                    firestoreSyncService = appContainer.firestoreSyncService
                ) as T
            }
        }
    )

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var handledSaveVersion by remember { mutableStateOf(uiState.saveVersion) }

    LaunchedEffect(recipe.id, recipe.updatedAt) {
        viewModel.populateFromRecipe(recipe)
        handledSaveVersion = viewModel.uiState.value.saveVersion
    }

    LaunchedEffect(uiState.saveVersion) {
        if (uiState.saveVersion > handledSaveVersion) {
            handledSaveVersion = uiState.saveVersion
            onSaved()
        }
    }

    RecipeFormContent(
        title = "Editar receta",
        buttonLabel = "Actualizar receta",
        uiState = uiState,
        onTitleChange = viewModel::updateTitle,
        onDescriptionChange = viewModel::updateDescription,
        onTimeChange = viewModel::updateTimeInMinutes,
        onCaloriesChange = viewModel::updateCalories,
        onDifficultyChange = viewModel::updateDifficulty,
        onCategoryChange = viewModel::updateCategory,
        onDayChange = viewModel::updateDayOfWeek,
        onMealTypeChange = viewModel::updateMealType,
        onIngredientChange = viewModel::updateIngredient,
        onAddIngredient = viewModel::addIngredient,
        onRemoveIngredient = viewModel::removeIngredient,
        onStepChange = viewModel::updateStep,
        onAddStep = viewModel::addStep,
        onRemoveStep = viewModel::removeStep,
        onBack = onBack,
        onSave = viewModel::saveRecipe
    )
}
