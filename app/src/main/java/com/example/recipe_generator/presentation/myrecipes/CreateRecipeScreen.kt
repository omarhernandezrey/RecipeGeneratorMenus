package com.example.recipe_generator.presentation.myrecipes

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.recipe_generator.RecipeGeneratorApp
import com.example.recipe_generator.data.notification.NotificationHelper
import com.example.recipe_generator.presentation.profile.copyContentUriToInternalStorage
import com.example.recipe_generator.presentation.profile.saveBitmapToInternalStorage
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun CreateRecipeScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onSaved: () -> Unit = {}
) {
    val appContainer = (LocalContext.current.applicationContext as RecipeGeneratorApp).container
    val userId = remember(appContainer) { appContainer.requireAuthenticatedUserId() }
    val viewModelKey = remember { "create-recipe-${UUID.randomUUID()}" }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val viewModel: CreateRecipeViewModel = viewModel(
        key = viewModelKey,
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return CreateRecipeViewModel(
                    userId = userId,
                    userRecipeRepository = appContainer.userRecipeRepository,
                    firestoreSyncService = appContainer.firestoreSyncService,
                    resolveRecipeVideoUseCase = appContainer.resolveRecipeVideoUseCase,
                    geminiDataSource = appContainer.geminiRecipeDataSource,
                    appNotificationRepository = appContainer.appNotificationRepository
                ) as T
            }
        }
    )

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var handledSaveVersion by remember { mutableStateOf(uiState.saveVersion) }
    var showImageSearch by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(uiState.saveVersion) {
        if (uiState.saveVersion > handledSaveVersion) {
            handledSaveVersion = uiState.saveVersion
            NotificationHelper.showRecipeCreatedNotification(context, uiState.title.ifBlank { "Receta nueva" })
            onSaved()
        }
    }

    if (showImageSearch) {
        MealImageSearchDialog(
            initialQuery = uiState.title.ifBlank { "food" },
            recipeId = uiState.recipeId,
            onImageSelected = { path ->
                viewModel.updateImageRes(path)
                showImageSearch = false
            },
            onDismiss = { showImageSearch = false }
        )
    }

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            val fileName = "recipe_image_${uiState.recipeId}"
            coroutineScope.launch {
                val path = copyContentUriToInternalStorage(context, fileName, uri)
                if (path != null) viewModel.updateImageRes(path)
            }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            val fileName = "recipe_image_${uiState.recipeId}"
            coroutineScope.launch {
                val path = saveBitmapToInternalStorage(context, fileName, bitmap)
                viewModel.updateImageRes(path)
            }
        }
    }

    RecipeFormContent(
        title = if (uiState.isEditMode) "Editar receta" else "Nueva receta",
        buttonLabel = if (uiState.isEditMode) "Actualizar receta" else "Guardar receta",
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
        onPickImageFromGallery = {
            pickImageLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        },
        onTakePhoto = { cameraLauncher.launch(null) },
        onSearchImage = { showImageSearch = true },
        onAiGenerate = viewModel::generateWithAI,
        onBack = onBack,
        onSave = viewModel::saveRecipe
    )
}
