package com.example.recipe_generator.presentation.myrecipes

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.recipe_generator.presentation.components.AppTextField
import com.example.recipe_generator.presentation.components.EditorialCard
import com.example.recipe_generator.presentation.components.PrimaryButton
import com.example.recipe_generator.presentation.profile.rememberProfileImage
import com.example.recipe_generator.presentation.theme.*

@Composable
internal fun RecipeFormContent(
    title: String,
    buttonLabel: String,
    uiState: RecipeFormUiState,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onTimeChange: (String) -> Unit,
    onCaloriesChange: (String) -> Unit,
    onDifficultyChange: (Float) -> Unit,
    onCategoryChange: (String) -> Unit,
    onDayChange: (String) -> Unit,
    onMealTypeChange: (String) -> Unit,
    onIngredientChange: (Int, String) -> Unit,
    onAddIngredient: () -> Unit,
    onRemoveIngredient: (Int) -> Unit,
    onStepChange: (Int, String) -> Unit,
    onAddStep: () -> Unit,
    onRemoveStep: (Int) -> Unit,
    onPickImageFromGallery: () -> Unit,
    onTakePhoto: () -> Unit,
    onSearchImage: () -> Unit,
    onAiGenerate: (String) -> Unit, // Función para el asistente IA
    onBack: () -> Unit,
    onSave: () -> Unit
) {
    var aiPrompt by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Surface)
            .verticalScroll(rememberScrollState())
            .padding(spacing_6),
        verticalArrangement = Arrangement.spacedBy(spacing_6)
    ) {
        // Cabecera
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Volver", tint = OnSurface)
            }
            Text(title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold, color = OnSurface)
        }

        // SECCIÓN IA: ASISTENTE INTELIGENTE
        val keyboardController = LocalSoftwareKeyboardController.current
        val onGenerate = {
            if (aiPrompt.isNotBlank() && !uiState.isGenerating) {
                keyboardController?.hide()
                onAiGenerate(aiPrompt)
            }
        }
        EditorialCard {
            Text("Asistente de Creación IA", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Primary)
            Text("Escribe el nombre de un plato y la IA completará los campos por ti.", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
            Spacer(modifier = Modifier.height(spacing_4))

            AppTextField(
                value = aiPrompt,
                onValueChange = { aiPrompt = it },
                placeholder = "Ej: Ajiaco Colombiano",
                modifier = Modifier.fillMaxWidth(),
                imeAction = ImeAction.Done,
                onImeAction = { onGenerate() }
            )
            Spacer(modifier = Modifier.height(spacing_3))

            Button(
                onClick = { onGenerate() },
                enabled = !uiState.isGenerating && aiPrompt.isNotBlank(),
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                if (uiState.isGenerating) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = Color.White)
                    Spacer(modifier = Modifier.width(spacing_2))
                    Text("Generando...", color = Color.White)
                } else {
                    Icon(Icons.Outlined.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(spacing_2))
                    Text("Generar con IA")
                }
            }

            if (!uiState.error.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(spacing_2))
                Text(
                    text = uiState.error,
                    color = Color(0xFFBA1A1A),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        HorizontalDivider(color = OnSurface.copy(alpha = 0.05f))

        // SECCIÓN MANUAL: LOS MISMOS CAMPOS DE SIEMPRE
        RecipeImagePicker(
            imageRes = uiState.imageRes,
            imageUrl = uiState.imageUrl,
            onPickFromGallery = onPickImageFromGallery,
            onTakePhoto = onTakePhoto,
            onSearchImage = onSearchImage
        )

        EditorialCard {
            Text("Información de la receta", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(spacing_4))
            AppTextField(uiState.title, onTitleChange, placeholder = "Nombre del plato")
            Spacer(modifier = Modifier.height(spacing_3))
            AppTextField(uiState.description, onDescriptionChange, placeholder = "Descripción o historia del plato")
            Spacer(modifier = Modifier.height(spacing_3))
            Row(horizontalArrangement = Arrangement.spacedBy(spacing_3)) {
                AppTextField(uiState.timeInMinutes, onTimeChange, Modifier.weight(1f), "Mins")
                AppTextField(uiState.calories, onCaloriesChange, Modifier.weight(1f), "Kcals")
            }
        }

        EditorialCard {
            Text("Clasificación", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(spacing_4))
            AppDropdownField("Categoría", uiState.category, recipeCategories, onCategoryChange)
            Spacer(modifier = Modifier.height(spacing_3))
            Text("Dificultad: ${sliderValueToDifficulty(uiState.difficultyLevel)}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = Primary)
            Slider(value = uiState.difficultyLevel, onValueChange = onDifficultyChange, valueRange = 0f..2f, steps = 1)
        }

        EditableStringSection("Ingredientes", uiState.ingredients, "Añadir ingrediente", onIngredientChange, onAddIngredient, onRemoveIngredient)
        EditableStringSection("Pasos de preparación", uiState.steps, "Añadir paso", onStepChange, onAddStep, onRemoveStep)

        PrimaryButton(
            text = if (uiState.isSaving) "Guardando..." else buttonLabel,
            onClick = onSave,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(spacing_10))
    }
}

@Composable
private fun EditableStringSection(
    title: String,
    items: List<String>,
    placeholder: String,
    onValueChange: (Int, String) -> Unit,
    onAdd: () -> Unit,
    onRemove: (Int) -> Unit
) {
    EditorialCard {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            TextButton(onClick = onAdd) {
                Icon(Icons.Outlined.Add, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(4.dp))
                Text("Añadir")
            }
        }
        Spacer(modifier = Modifier.height(spacing_2))
        items.forEachIndexed { index, item ->
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                AppTextField(item, { onValueChange(index, it) }, Modifier.weight(1f), placeholder)
                IconButton(onClick = { onRemove(index) }) {
                    Icon(Icons.Outlined.RemoveCircleOutline, "Quitar", tint = OnSurfaceVariant.copy(alpha = 0.5f))
                }
            }
        }
    }
}

@Composable
private fun RecipeImagePicker(
    imageRes: String,
    imageUrl: String?,
    onPickFromGallery: () -> Unit,
    onTakePhoto: () -> Unit,
    onSearchImage: () -> Unit
) {
    EditorialCard {
        Text("Imagen de la receta", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(spacing_4))
        
        Box(modifier = Modifier.fillMaxWidth().height(200.dp).clip(RoundedCornerShape(rounded_lg)).background(PrimaryContainer.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) {
            if (!imageUrl.isNullOrBlank()) {
                com.example.recipe_generator.presentation.components.RecipeImage(
                    recipeTitle = "Preview",
                    imageRes = "",
                    imageUrl = imageUrl,
                    modifier = Modifier.fillMaxSize()
                )
            } else if (imageRes.isNotBlank()) {
                val bitmap = rememberProfileImage(imageRes)
                if (bitmap != null) Image(bitmap, null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            } else {
                Icon(Icons.Outlined.Image, null, modifier = Modifier.size(48.dp), tint = Primary.copy(alpha = 0.3f))
            }
        }
        
        Spacer(modifier = Modifier.height(spacing_4))
        Row(horizontalArrangement = Arrangement.spacedBy(spacing_2)) {
            OutlinedButton(onClick = onSearchImage, modifier = Modifier.weight(1f)) { Text("Buscar", style = MaterialTheme.typography.labelSmall) }
            OutlinedButton(onClick = onPickFromGallery, modifier = Modifier.weight(1f)) { Text("Galería", style = MaterialTheme.typography.labelSmall) }
            OutlinedButton(onClick = onTakePhoto, modifier = Modifier.weight(1f)) { Text("Cámara", style = MaterialTheme.typography.labelSmall) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppDropdownField(label: String, value: String, options: List<String>, onSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = value, onValueChange = {}, readOnly = true, label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryNotEditable).fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { DropdownMenuItem(text = { Text(it) }, onClick = { onSelected(it); expanded = false }) }
        }
    }
}
