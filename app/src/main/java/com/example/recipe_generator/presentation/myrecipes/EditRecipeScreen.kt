package com.example.recipe_generator.presentation.myrecipes

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.recipe_generator.RecipeGeneratorApp
import com.example.recipe_generator.domain.model.UserRecipe
import com.example.recipe_generator.presentation.profile.copyContentUriToInternalStorage
import com.example.recipe_generator.presentation.profile.saveBitmapToInternalStorage
import kotlinx.coroutines.launch

@Composable
fun EditRecipeScreen(
    recipe: UserRecipe,
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onSaved: () -> Unit = {}
) {
    val appContainer = (LocalContext.current.applicationContext as RecipeGeneratorApp).container
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val viewModel: CreateRecipeViewModel = viewModel(
        key = "edit-recipe-${recipe.id}",
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return CreateRecipeViewModel(
                    userId = recipe.userId,
                    userRecipeRepository = appContainer.userRecipeRepository,
                    firestoreSyncService = appContainer.firestoreSyncService,
                    resolveRecipeVideoUseCase = appContainer.resolveRecipeVideoUseCase,
                    geminiDataSource = appContainer.geminiRecipeDataSource, // Inyectado para IA
                    appNotificationRepository = appContainer.appNotificationRepository
                ) as T
            }
        }
    )

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var handledSaveVersion by remember { mutableIntStateOf(uiState.saveVersion) }
    var showImageSearch by remember { mutableStateOf(false) }

    LaunchedEffect(recipe.id) {
        viewModel.populateFromRecipe(recipe)
    }

    LaunchedEffect(uiState.saveVersion) {
        if (uiState.saveVersion > handledSaveVersion) {
            onSaved()
        }
    }

    if (showImageSearch) {
        MealImageSearchDialog(
            initialQuery = uiState.title,
            recipeId = uiState.recipeId,
            onImageSelected = { path ->
                viewModel.updateImageRes(path)
                showImageSearch = false
            },
            onDismiss = { showImageSearch = false }
        )
    }

    val pickImageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            coroutineScope.launch {
                val path = copyContentUriToInternalStorage(context, "recipe_${uiState.recipeId}", uri)
                if (path != null) viewModel.updateImageRes(path)
            }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if (bitmap != null) {
            coroutineScope.launch {
                val path = saveBitmapToInternalStorage(context, "recipe_${uiState.recipeId}", bitmap)
                viewModel.updateImageRes(path)
            }
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
        onPickImageFromGallery = { pickImageLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
        onTakePhoto = { cameraLauncher.launch(null) },
        onSearchImage = { showImageSearch = true },
        onAiGenerate = viewModel::generateWithAI,
        onBack = onBack,
        onSave = viewModel::saveRecipe
    )
}
