package com.example.recipe_generator.presentation.profile

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Collections
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.recipe_generator.RecipeGeneratorApp
import com.example.recipe_generator.presentation.components.AppTextField
import com.example.recipe_generator.presentation.components.EditorialCard
import com.example.recipe_generator.presentation.components.PrimaryButton
import com.example.recipe_generator.presentation.theme.OnSurface
import com.example.recipe_generator.presentation.theme.OnSurfaceVariant
import com.example.recipe_generator.presentation.theme.Primary
import com.example.recipe_generator.presentation.theme.PrimaryContainer
import com.example.recipe_generator.presentation.theme.SecondaryContainer
import com.example.recipe_generator.presentation.theme.Surface
import com.example.recipe_generator.presentation.theme.SurfaceContainerLow
import com.example.recipe_generator.presentation.theme.rounded_full
import com.example.recipe_generator.presentation.theme.rounded_lg
import com.example.recipe_generator.presentation.theme.spacing_10
import com.example.recipe_generator.presentation.theme.spacing_2
import com.example.recipe_generator.presentation.theme.spacing_3
import com.example.recipe_generator.presentation.theme.spacing_4
import com.example.recipe_generator.presentation.theme.spacing_6
import com.example.recipe_generator.presentation.theme.spacing_8
import kotlinx.coroutines.launch

private val profileDietOptions = listOf(
    "Vegetariano",
    "Vegano",
    "Sin Gluten",
    "Keto",
    "Paleo"
)

@Composable
fun EditProfileScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onSaved: () -> Unit = {}
) {
    val appContainer = (LocalContext.current.applicationContext as RecipeGeneratorApp).container
    val viewModel: ProfileViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return ProfileViewModel(
                    authRepository = appContainer.authRepository,
                    userProfileRepository = appContainer.userProfileRepository,
                    userRecipeRepository = appContainer.userRecipeRepository,
                    favoritesRepository = appContainer.favoritesRepository,
                    weeklyPlanRepository = appContainer.weeklyPlanRepository,
                    userPrefsRepository = appContainer.userPrefsRepository,
                    firestoreSyncService = appContainer.firestoreSyncService
                ) as T
            }
        }
    )

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val profile by viewModel.profile.collectAsStateWithLifecycle()
    val isSaving by viewModel.isSaving.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    val saveVersion by viewModel.saveVersion.collectAsStateWithLifecycle()
    var handledSaveVersion by remember { mutableStateOf(saveVersion) }

    var displayName by remember { mutableStateOf("") }
    var selectedPhotoRef by remember { mutableStateOf<String?>(null) }
    var selectedDiets by remember { mutableStateOf(listOf<String>()) }
    var defaultPortions by remember { mutableStateOf(2f) }
    var hydratedUserId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(profile, currentUser) {
        val userId = currentUser?.uid ?: return@LaunchedEffect
        if (hydratedUserId != userId) {
            hydratedUserId = userId
            displayName = profile?.displayName?.ifBlank { null }
                ?: currentUser?.displayName.orEmpty()
            selectedPhotoRef = profile?.photoUrl ?: currentUser?.photoUrl
            selectedDiets = profile?.preferredDiets.orEmpty()
            defaultPortions = (profile?.defaultPortions ?: 2).toFloat()
        }
    }

    LaunchedEffect(saveVersion) {
        if (saveVersion > handledSaveVersion) {
            handledSaveVersion = saveVersion
            onSaved()
        }
    }

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            val uid = viewModel.currentUser.value?.uid ?: return@rememberLauncherForActivityResult
            coroutineScope.launch {
                val path = copyContentUriToInternalStorage(context, "profile_photo_$uid", uri)
                if (path != null) selectedPhotoRef = path
            }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            val uid = viewModel.currentUser.value?.uid ?: return@rememberLauncherForActivityResult
            coroutineScope.launch {
                selectedPhotoRef = saveBitmapToInternalStorage(context, "profile_photo_$uid", bitmap)
            }
        }
    }

    EditProfileContent(
        modifier = modifier,
        displayName = displayName,
        onDisplayNameChange = { displayName = it },
        photoRef = selectedPhotoRef,
        selectedDiets = selectedDiets,
        onToggleDiet = { diet ->
            selectedDiets = if (diet in selectedDiets) {
                selectedDiets - diet
            } else {
                selectedDiets + diet
            }
        },
        defaultPortions = defaultPortions,
        onDefaultPortionsChange = { defaultPortions = it },
        isSaving = isSaving,
        error = error,
        onPickFromGallery = {
            pickImageLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        },
        onTakePhoto = {
            cameraLauncher.launch(null)
        },
        onBack = onBack,
        onSave = {
            viewModel.saveProfileChanges(
                displayName = displayName.trim(),
                photoUrl = selectedPhotoRef,
                preferredDiets = selectedDiets,
                defaultPortions = defaultPortions.toInt()
            )
        }
    )
}

@Composable
private fun EditProfileContent(
    modifier: Modifier,
    displayName: String,
    onDisplayNameChange: (String) -> Unit,
    photoRef: String?,
    selectedDiets: List<String>,
    onToggleDiet: (String) -> Unit,
    defaultPortions: Float,
    onDefaultPortionsChange: (Float) -> Unit,
    isSaving: Boolean,
    error: String?,
    onPickFromGallery: () -> Unit,
    onTakePhoto: () -> Unit,
    onBack: () -> Unit,
    onSave: () -> Unit
) {
    val avatar = rememberProfileImage(photoRef)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Surface)
            .verticalScroll(rememberScrollState())
            .padding(spacing_6),
        verticalArrangement = Arrangement.spacedBy(spacing_6)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Volver",
                    tint = OnSurface
                )
            }

            Column(
                modifier = Modifier.padding(start = spacing_2)
            ) {
                Text(
                    text = "Editar perfil",
                    style = MaterialTheme.typography.headlineSmall,
                    color = OnSurface,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "Actualiza tu nombre, foto y preferencias personales.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurfaceVariant
                )
            }
        }

        EditorialCard {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(spacing_4)
            ) {
                Box(
                    modifier = Modifier
                        .size(128.dp)
                        .clip(CircleShape)
                        .background(PrimaryContainer.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center
                ) {
                    if (avatar != null) {
                        androidx.compose.foundation.Image(
                            bitmap = avatar,
                            contentDescription = "Vista previa de foto",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop
                        )
                    } else {
                        Text(
                            text = displayName.trim().ifBlank { "U" }.take(1).uppercase(),
                            style = MaterialTheme.typography.displaySmall,
                            color = Primary,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(spacing_3)
                ) {
                    OutlinedButton(
                        onClick = onPickFromGallery,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(rounded_full),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Primary)
                    ) {
                        Icon(Icons.Outlined.Collections, contentDescription = null)
                        Spacer(modifier = Modifier.width(spacing_2))
                        Text("Galería")
                    }

                    OutlinedButton(
                        onClick = onTakePhoto,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(rounded_full),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Primary)
                    ) {
                        Icon(Icons.Outlined.CameraAlt, contentDescription = null)
                        Spacer(modifier = Modifier.width(spacing_2))
                        Text("Cámara")
                    }
                }
            }
        }

        EditorialCard {
            Text(
                text = "Identidad",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = OnSurface
            )
            Spacer(modifier = Modifier.height(spacing_4))
            AppTextField(
                value = displayName,
                onValueChange = onDisplayNameChange,
                placeholder = "Tu nombre"
            )
        }

        EditorialCard {
            Text(
                text = "Preferencias dietéticas",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = OnSurface
            )
            Spacer(modifier = Modifier.height(spacing_4))
            Column(
                verticalArrangement = Arrangement.spacedBy(spacing_3)
            ) {
                profileDietOptions.chunked(2).forEach { rowDiets ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(spacing_3)
                    ) {
                        rowDiets.forEach { diet ->
                            val isSelected = diet in selectedDiets
                            AssistChip(
                                onClick = { onToggleDiet(diet) },
                                label = { Text(diet) },
                                leadingIcon = if (isSelected) {
                                    {
                                        Icon(
                                            imageVector = Icons.Outlined.Check,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                } else {
                                    null
                                },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = if (isSelected) SecondaryContainer else SurfaceContainerLow,
                                    labelColor = OnSurface
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (rowDiets.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }

        EditorialCard {
            Text(
                text = "Porciones por defecto",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = OnSurface
            )
            Spacer(modifier = Modifier.height(spacing_4))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${defaultPortions.toInt()} porciones",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = Primary
                )
                Icon(
                    imageVector = Icons.Outlined.Tune,
                    contentDescription = null,
                    tint = Primary
                )
            }
            Slider(
                value = defaultPortions,
                onValueChange = onDefaultPortionsChange,
                valueRange = 1f..10f,
                steps = 7
            )
        }

        if (!error.isNullOrBlank()) {
            Text(
                text = error,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFFBA1A1A),
                modifier = Modifier.padding(horizontal = spacing_2)
            )
        }

        PrimaryButton(
            text = if (isSaving) "Guardando..." else "Guardar cambios",
            onClick = onSave,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = spacing_2)
        )

        Spacer(modifier = Modifier.height(spacing_10))
    }
}
