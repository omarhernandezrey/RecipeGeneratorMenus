package com.example.recipe_generator.presentation.weeklyplan

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.recipe_generator.RecipeGeneratorApp
import com.example.recipe_generator.domain.model.WeeklyPlan
import com.example.recipe_generator.presentation.components.EditorialCard
import com.example.recipe_generator.presentation.theme.OnSurface
import com.example.recipe_generator.presentation.theme.OnSurfaceVariant
import com.example.recipe_generator.presentation.theme.Primary
import com.example.recipe_generator.presentation.theme.PrimaryContainer
import com.example.recipe_generator.presentation.theme.Surface
import com.example.recipe_generator.presentation.theme.SurfaceContainerLow
import com.example.recipe_generator.presentation.theme.rounded_full
import com.example.recipe_generator.presentation.theme.rounded_lg
import com.example.recipe_generator.presentation.theme.rounded_md
import com.example.recipe_generator.presentation.theme.spacing_2
import com.example.recipe_generator.presentation.theme.spacing_3
import com.example.recipe_generator.presentation.theme.spacing_4
import com.example.recipe_generator.presentation.theme.spacing_6
import java.io.File

private data class PlanSlot(val day: String, val mealType: String)

private val weeklyPlanDays = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")
private val weeklyPlanMealTypes = listOf("Desayuno", "Almuerzo", "Cena")

@Composable
fun MyWeeklyPlanScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {}
) {
    val appContainer = (LocalContext.current.applicationContext as RecipeGeneratorApp).container
    val userId = remember(appContainer) { appContainer.requireAuthenticatedUserId() }

    val viewModel: MyWeeklyPlanViewModel = viewModel(
        key = "weekly-plan-$userId",
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return MyWeeklyPlanViewModel(
                    userId = userId,
                    weeklyPlanRepository = appContainer.weeklyPlanRepository,
                    firestoreWeeklyPlanSync = appContainer.firestoreWeeklyPlanSync
                ) as T
            }
        }
    )

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedSlot by remember { mutableStateOf<PlanSlot?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Surface)
            .padding(horizontal = spacing_4, vertical = spacing_4),
        verticalArrangement = Arrangement.spacedBy(spacing_4)
    ) {
        // Header
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Volver", tint = OnSurface)
            }
            Column(modifier = Modifier.padding(start = spacing_2)) {
                Text(
                    text = "Mi plan semanal",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = OnSurface
                )
                Text(
                    text = "Desayuno, almuerzo y cena para los 7 días.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurfaceVariant
                )
            }
        }

        if (!uiState.error.isNullOrBlank()) {
            EditorialCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(uiState.error!!, style = MaterialTheme.typography.bodyMedium,
                        color = OnSurfaceVariant, modifier = Modifier.weight(1f))
                    TextButton(onClick = viewModel::clearError) { Text("Cerrar") }
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .horizontalScroll(rememberScrollState())
        ) {
            WeeklyPlanTable(
                plan          = uiState.plan,
                onSelectSlot  = { day, mealType -> selectedSlot = PlanSlot(day, mealType) },
                onRemoveMeal  = viewModel::removeMeal
            )
        }
    }

    if (selectedSlot != null) {
        SelectRecipeDialog(
            mealType = selectedSlot!!.mealType,
            onDismiss = { selectedSlot = null },
            onRecipeSelected = { recipeId ->
                val slot = selectedSlot ?: return@SelectRecipeDialog
                viewModel.assignRecipe(slot.day, slot.mealType, recipeId)
                selectedSlot = null
            }
        )
    }
}

@Composable
private fun WeeklyPlanTable(
    plan: List<WeeklyPlan>,
    onSelectSlot: (String, String) -> Unit,
    onRemoveMeal: (String, String) -> Unit
) {
    val colW   = 152.dp
    val labelW = 104.dp

    Column(verticalArrangement = Arrangement.spacedBy(spacing_3)) {
        // Cabecera días
        Row(horizontalArrangement = Arrangement.spacedBy(spacing_3)) {
            HeaderCell("", labelW)
            weeklyPlanDays.forEach { day -> HeaderCell(day, colW) }
        }

        // Filas por tipo de comida
        weeklyPlanMealTypes.forEach { mealType ->
            Row(horizontalArrangement = Arrangement.spacedBy(spacing_3)) {
                MealLabelCell(mealType, labelW)
                weeklyPlanDays.forEach { day ->
                    val entry = plan.firstOrNull { it.dayOfWeek == day && it.mealType == mealType }
                    WeeklyPlanCell(
                        entry         = entry,
                        width         = colW,
                        onAddOrReplace = { onSelectSlot(day, mealType) },
                        onRemove      = { onRemoveMeal(day, mealType) }
                    )
                }
            }
        }
    }
}

@Composable
private fun HeaderCell(text: String, width: Dp) {
    Box(
        modifier = Modifier
            .width(width)
            .background(PrimaryContainer.copy(alpha = 0.32f), RoundedCornerShape(rounded_md))
            .padding(horizontal = spacing_3, vertical = spacing_3),
        contentAlignment = Alignment.Center
    ) {
        Text(text, style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.ExtraBold, color = Primary)
    }
}

@Composable
private fun MealLabelCell(text: String, width: Dp) {
    Box(
        modifier = Modifier
            .width(width)
            .background(SurfaceContainerLow, RoundedCornerShape(rounded_md))
            .padding(horizontal = spacing_3, vertical = spacing_6),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(text, style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold, color = OnSurface)
    }
}

@Composable
private fun WeeklyPlanCell(
    entry: WeeklyPlan?,
    width: Dp,
    onAddOrReplace: () -> Unit,
    onRemove: () -> Unit
) {
    Box(modifier = Modifier.width(width)) {
        EditorialCard(modifier = Modifier.fillMaxWidth()) {
            if (entry == null) {
                // Celda vacía — botón agregar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Primary.copy(alpha = 0.06f), RoundedCornerShape(rounded_full))
                        .padding(vertical = spacing_3),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(onClick = onAddOrReplace) {
                        Icon(Icons.Outlined.Add, contentDescription = "Agregar", tint = Primary)
                    }
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(spacing_2)) {
                    // Imagen de la receta si existe
                    if (entry.imageRes.isNotBlank()) {
                        val model: Any = if (entry.imageRes.startsWith("http"))
                            entry.imageRes else File(entry.imageRes)
                        AsyncImage(
                            model          = model,
                            contentDescription = entry.recipeTitle,
                            contentScale   = ContentScale.Crop,
                            modifier       = Modifier
                                .fillMaxWidth()
                                .height(80.dp)
                                .clip(RoundedCornerShape(rounded_lg))
                                .background(PrimaryContainer.copy(alpha = 0.15f))
                        )
                    } else {
                        // Placeholder letra
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .clip(RoundedCornerShape(rounded_lg))
                                .background(PrimaryContainer.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = entry.recipeTitle.take(2).uppercase(),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = Primary
                            )
                        }
                    }

                    Text(
                        text = entry.recipeTitle.ifBlank { "Receta" },
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = OnSurface,
                        maxLines = 2
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = onAddOrReplace,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cambiar", style = MaterialTheme.typography.labelSmall)
                        }
                        IconButton(onClick = onRemove, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Outlined.Close, contentDescription = "Quitar",
                                tint = OnSurfaceVariant, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}
