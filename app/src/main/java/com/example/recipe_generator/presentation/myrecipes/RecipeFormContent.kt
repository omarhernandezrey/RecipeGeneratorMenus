package com.example.recipe_generator.presentation.myrecipes

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Collections
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.recipe_generator.presentation.components.AppTextField
import com.example.recipe_generator.presentation.components.EditorialCard
import com.example.recipe_generator.presentation.components.PrimaryButton
import com.example.recipe_generator.presentation.profile.rememberProfileImage
import com.example.recipe_generator.presentation.theme.OnSurface
import com.example.recipe_generator.presentation.theme.OnSurfaceVariant
import com.example.recipe_generator.presentation.theme.Primary
import com.example.recipe_generator.presentation.theme.PrimaryContainer
import com.example.recipe_generator.presentation.theme.Surface
import com.example.recipe_generator.presentation.theme.rounded_full
import com.example.recipe_generator.presentation.theme.rounded_lg
import com.example.recipe_generator.presentation.theme.spacing_10
import com.example.recipe_generator.presentation.theme.spacing_2
import com.example.recipe_generator.presentation.theme.spacing_3
import com.example.recipe_generator.presentation.theme.spacing_4
import com.example.recipe_generator.presentation.theme.spacing_6

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
    onBack: () -> Unit,
    onSave: () -> Unit
) {
    Column(
        modifier = Modifier
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
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = OnSurface
                )
                Text(
                    text = "Completa el formulario para guardar la receta del usuario.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurfaceVariant
                )
            }
        }

        RecipeImagePicker(
            imageRes = uiState.imageRes,
            onPickFromGallery = onPickImageFromGallery,
            onTakePhoto = onTakePhoto
        )

        EditorialCard {
            Text("Información básica", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(spacing_4))
            AppTextField(uiState.title, onTitleChange, placeholder = "Título de la receta")
            Spacer(modifier = Modifier.height(spacing_3))
            AppTextField(uiState.description, onDescriptionChange, placeholder = "Descripción")
            Spacer(modifier = Modifier.height(spacing_3))
            Row(horizontalArrangement = Arrangement.spacedBy(spacing_3)) {
                AppTextField(
                    value = uiState.timeInMinutes,
                    onValueChange = onTimeChange,
                    placeholder = "Tiempo (min)",
                    modifier = Modifier.weight(1f)
                )
                AppTextField(
                    value = uiState.calories,
                    onValueChange = onCaloriesChange,
                    placeholder = "Calorías",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        EditorialCard {
            Text("Clasificación", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(spacing_4))
            AppDropdownField(
                label = "Categoría",
                value = uiState.category,
                options = recipeCategories,
                onSelected = onCategoryChange
            )
            Spacer(modifier = Modifier.height(spacing_3))
            Row(horizontalArrangement = Arrangement.spacedBy(spacing_3)) {
                AppDropdownField(
                    label = "Día",
                    value = uiState.dayOfWeek,
                    options = recipeDaysOfWeek,
                    onSelected = onDayChange,
                    modifier = Modifier.weight(1f)
                )
                AppDropdownField(
                    label = "Comida",
                    value = uiState.mealType,
                    options = mealTypeOptions,
                    onSelected = onMealTypeChange,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(spacing_4))
            Text(
                text = "Dificultad: ${sliderValueToDifficulty(uiState.difficultyLevel)}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = Primary
            )
            Slider(
                value = uiState.difficultyLevel,
                onValueChange = onDifficultyChange,
                valueRange = 0f..2f,
                steps = 1
            )
        }

        EditableStringSection(
            title = "Ingredientes",
            items = uiState.ingredients,
            placeholder = "Ej. 2 huevos",
            onValueChange = onIngredientChange,
            onAdd = onAddIngredient,
            onRemove = onRemoveIngredient
        )

        EditableStringSection(
            title = "Pasos",
            items = uiState.steps,
            placeholder = "Ej. Mezclar todos los ingredientes",
            onValueChange = onStepChange,
            onAdd = onAddStep,
            onRemove = onRemoveStep
        )

        if (!uiState.error.isNullOrBlank()) {
            Text(
                text = uiState.error,
                style = MaterialTheme.typography.bodyMedium,
                color = androidx.compose.ui.graphics.Color(0xFFBA1A1A)
            )
        }

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
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Box(
                modifier = Modifier
                    .background(Primary.copy(alpha = 0.08f), RoundedCornerShape(rounded_full))
                    .clickable(onClick = onAdd)
                    .padding(horizontal = spacing_3, vertical = spacing_2)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Add, contentDescription = null, tint = Primary)
                    Spacer(modifier = Modifier.width(spacing_2))
                    Text("Agregar", color = Primary, fontWeight = FontWeight.SemiBold)
                }
            }
        }
        Spacer(modifier = Modifier.height(spacing_4))
        Column(verticalArrangement = Arrangement.spacedBy(spacing_3)) {
            items.forEachIndexed { index, item ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(spacing_3)
                ) {
                    AppTextField(
                        value = item,
                        onValueChange = { onValueChange(index, it) },
                        placeholder = placeholder,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { onRemove(index) }) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "Eliminar",
                            tint = OnSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RecipeImagePicker(
    imageRes: String,
    onPickFromGallery: () -> Unit,
    onTakePhoto: () -> Unit
) {
    val image = rememberProfileImage(imageRes.takeIf { it.isNotBlank() })

    EditorialCard {
        Text(
            text = "Foto de la receta",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(spacing_4))

        // Preview o placeholder
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(rounded_lg))
                .background(PrimaryContainer.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            if (image != null) {
                Image(
                    bitmap = image,
                    contentDescription = "Imagen de la receta",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(spacing_2)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Image,
                        contentDescription = null,
                        tint = Primary.copy(alpha = 0.4f),
                        modifier = Modifier.size(40.dp)
                    )
                    Text(
                        text = "Sin imagen",
                        style = MaterialTheme.typography.bodyMedium,
                        color = OnSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(spacing_4))

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
                Icon(Icons.Outlined.Collections, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(spacing_2))
                Text("Galería")
            }
            OutlinedButton(
                onClick = onTakePhoto,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(rounded_full),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Primary)
            ) {
                Icon(Icons.Outlined.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(spacing_2))
                Text("Cámara")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppDropdownField(
    label: String,
    value: String,
    options: List<String>,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .menuAnchor(
                    type = ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                    enabled = true
                )
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
