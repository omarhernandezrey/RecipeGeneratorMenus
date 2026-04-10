package com.example.recipe_generator.presentation.weeklyplan

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.recipe_generator.RecipeGeneratorApp
import com.example.recipe_generator.domain.model.WeeklyPlan
import com.example.recipe_generator.presentation.components.EditorialCard
import com.example.recipe_generator.presentation.components.RecipeImage
import com.example.recipe_generator.presentation.components.editorialBottomBarContentPadding
import com.example.recipe_generator.presentation.theme.Background
import com.example.recipe_generator.presentation.theme.OnPrimary
import com.example.recipe_generator.presentation.theme.OnSurface
import com.example.recipe_generator.presentation.theme.OnSurfaceVariant
import com.example.recipe_generator.presentation.theme.Primary
import com.example.recipe_generator.presentation.theme.PrimaryContainer
import com.example.recipe_generator.presentation.theme.PrimaryFixedDim
import com.example.recipe_generator.presentation.theme.Surface
import com.example.recipe_generator.presentation.theme.SurfaceContainerHigh
import com.example.recipe_generator.presentation.theme.SurfaceContainerLow
import com.example.recipe_generator.presentation.theme.SurfaceContainerLowest
import com.example.recipe_generator.presentation.theme.Tertiary
import com.example.recipe_generator.presentation.theme.rounded_full
import com.example.recipe_generator.presentation.theme.rounded_lg
import com.example.recipe_generator.presentation.theme.rounded_md
import com.example.recipe_generator.presentation.theme.rounded_sm
import com.example.recipe_generator.presentation.theme.spacing_1
import com.example.recipe_generator.presentation.theme.spacing_2
import com.example.recipe_generator.presentation.theme.spacing_3
import com.example.recipe_generator.presentation.theme.spacing_4
import com.example.recipe_generator.presentation.theme.spacing_5
import com.example.recipe_generator.presentation.theme.spacing_6
import com.example.recipe_generator.presentation.theme.spacing_8

private data class PlanSlot(val day: String, val mealType: String)

private val weeklyPlanDays      = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")
private val weeklyPlanMealTypes = listOf("Desayuno", "Almuerzo", "Cena")

private val dayAbbr = mapOf(
    "Lunes" to "Lun", "Martes" to "Mar", "Miércoles" to "Mié",
    "Jueves" to "Jue", "Viernes" to "Vie", "Sábado" to "Sáb", "Domingo" to "Dom"
)

private val mealColor = mapOf(
    "Desayuno" to Color(0xFFFFF3E0),
    "Almuerzo" to Color(0xFFE8F5E9),
    "Cena"     to Color(0xFFEDE7F6)
)
private val mealAccent = mapOf(
    "Desayuno" to Color(0xFFE65100),
    "Almuerzo" to Color(0xFF2E7D32),
    "Cena"     to Color(0xFF4527A0)
)

// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun MyWeeklyPlanScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onRecipeClick: (recipeId: String) -> Unit = {}
) {
    val appContainer = (LocalContext.current.applicationContext as RecipeGeneratorApp).container
    val userId = remember(appContainer) { appContainer.requireAuthenticatedUserId() }

    val viewModel: MyWeeklyPlanViewModel = viewModel(
        key = "weekly-plan-$userId",
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return MyWeeklyPlanViewModel(
                    userId                 = userId,
                    weeklyPlanRepository   = appContainer.weeklyPlanRepository,
                    firestoreWeeklyPlanSync = appContainer.firestoreWeeklyPlanSync
                ) as T
            }
        }
    )

    val uiState     by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedSlot by remember { mutableStateOf<PlanSlot?>(null) }

    val filledCount   = uiState.plan.size
    val totalSlots    = weeklyPlanDays.size * weeklyPlanMealTypes.size   // 21
    val progress      = filledCount.toFloat() / totalSlots
    val statusBarTop  = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val bottomNavPad  = editorialBottomBarContentPadding()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Surface)
    ) {
        // ── Header ─────────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Background)
                .padding(top = statusBarTop + spacing_3)
                .padding(horizontal = spacing_4)
                .padding(bottom = spacing_4)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Botón volver
                Surface(
                    modifier  = Modifier.size(40.dp),
                    shape     = CircleShape,
                    color     = SurfaceContainerHigh
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        IconButton(onClick = onBack) {
                            Icon(
                                Icons.AutoMirrored.Outlined.ArrowBack,
                                contentDescription = "Volver",
                                tint = OnSurface,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                Spacer(Modifier.width(spacing_4))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Mi Plan Semanal",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = OnSurface
                    )
                    Text(
                        text = "7 días · Desayuno, almuerzo y cena",
                        style = MaterialTheme.typography.bodyMedium,
                        color = OnSurfaceVariant
                    )
                }

                // Badge progreso
                Surface(
                    shape = RoundedCornerShape(rounded_full),
                    color = if (filledCount > 0) Primary else SurfaceContainerHigh
                ) {
                    Text(
                        text  = "$filledCount/$totalSlots",
                        modifier = Modifier.padding(horizontal = spacing_4, vertical = spacing_2),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (filledCount > 0) OnPrimary else OnSurfaceVariant
                    )
                }
            }

            // Barra de progreso
            if (filledCount > 0) {
                Spacer(Modifier.height(spacing_4))
                LinearProgressIndicator(
                    progress          = { progress },
                    modifier          = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(rounded_full)),
                    color             = Primary,
                    trackColor        = PrimaryFixedDim.copy(alpha = 0.25f),
                    strokeCap         = StrokeCap.Round
                )
                Spacer(Modifier.height(spacing_1))
                Text(
                    text  = if (filledCount == totalSlots) "¡Plan completo!" else "$filledCount de $totalSlots comidas planificadas",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (filledCount == totalSlots) Primary else OnSurfaceVariant,
                    fontWeight = if (filledCount == totalSlots) FontWeight.Bold else FontWeight.Normal
                )
            }
        }

        // ── Banner de error ─────────────────────────────────────────────
        if (!uiState.error.isNullOrBlank()) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing_4, vertical = spacing_2),
                shape = RoundedCornerShape(rounded_md),
                color = MaterialTheme.colorScheme.errorContainer
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = spacing_4, vertical = spacing_3),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = uiState.error!!,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(onClick = viewModel::clearError) {
                        Text("Cerrar", color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }

        // ── Tabla semanal (scroll horizontal) ──────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .horizontalScroll(rememberScrollState())
        ) {
            WeeklyPlanTable(
                plan          = uiState.plan,
                onSelectSlot  = { day, mealType -> selectedSlot = PlanSlot(day, mealType) },
                onRemoveMeal  = viewModel::removeMeal,
                onRecipeClick = onRecipeClick,
                bottomPadding = bottomNavPad
            )
        }
    }

    // ── Diálogo selección de receta ─────────────────────────────────────
    if (selectedSlot != null) {
        SelectRecipeDialog(
            mealType  = selectedSlot!!.mealType,
            onDismiss = { selectedSlot = null },
            onRecipeSelected = { recipeId ->
                val slot = selectedSlot ?: return@SelectRecipeDialog
                viewModel.assignRecipe(slot.day, slot.mealType, recipeId)
                selectedSlot = null
            }
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun WeeklyPlanTable(
    plan: List<WeeklyPlan>,
    onSelectSlot: (String, String) -> Unit,
    onRemoveMeal: (String, String) -> Unit,
    onRecipeClick: (String) -> Unit,
    bottomPadding: Dp = 0.dp
) {
    val colW   = 164.dp
    val labelW = 100.dp

    Column(
        modifier = Modifier.padding(
            start  = spacing_4,
            end    = spacing_4,
            top    = spacing_4,
            bottom = spacing_4 + bottomPadding
        ),
        verticalArrangement = Arrangement.spacedBy(spacing_3)
    ) {
        // Cabecera — días
        Row(horizontalArrangement = Arrangement.spacedBy(spacing_3)) {
            Box(modifier = Modifier.width(labelW))   // celda vacía esquina
            weeklyPlanDays.forEach { day ->
                DayHeaderCell(day = day, width = colW)
            }
        }

        // Filas por tipo de comida
        weeklyPlanMealTypes.forEach { mealType ->
            Row(horizontalArrangement = Arrangement.spacedBy(spacing_3)) {
                MealLabelCell(mealType = mealType, width = labelW)
                weeklyPlanDays.forEach { day ->
                    val entry = plan.firstOrNull { it.dayOfWeek == day && it.mealType == mealType }
                    WeeklyPlanCell(
                        entry          = entry,
                        mealType       = mealType,
                        width          = colW,
                        onAddOrReplace = { onSelectSlot(day, mealType) },
                        onRemove       = { onRemoveMeal(day, mealType) },
                        onRecipeClick  = onRecipeClick
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun DayHeaderCell(day: String, width: Dp) {
    Surface(
        modifier = Modifier.width(width),
        shape    = RoundedCornerShape(rounded_md),
        color    = PrimaryContainer.copy(alpha = 0.35f)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = spacing_3),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text       = dayAbbr[day] ?: day,
                style      = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.ExtraBold,
                color      = Primary
            )
        }
    }
}

@Composable
private fun MealLabelCell(mealType: String, width: Dp) {
    val bg     = mealColor[mealType]  ?: SurfaceContainerLow
    val accent = mealAccent[mealType] ?: Primary

    Surface(
        modifier = Modifier.width(width),
        shape    = RoundedCornerShape(rounded_md),
        color    = bg
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing_3, vertical = spacing_5),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing_2)
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(32.dp)
                    .clip(RoundedCornerShape(rounded_full))
                    .background(accent)
            )
            Text(
                text       = mealType,
                style      = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.ExtraBold,
                color      = accent
            )
        }
    }
}

@Composable
private fun WeeklyPlanCell(
    entry: WeeklyPlan?,
    mealType: String,
    width: Dp,
    onAddOrReplace: () -> Unit,
    onRemove: () -> Unit,
    onRecipeClick: (String) -> Unit
) {
    val accent = mealAccent[mealType] ?: Primary

    Surface(
        modifier  = Modifier.width(width),
        shape     = RoundedCornerShape(rounded_md),
        color     = SurfaceContainerLowest,
        shadowElevation = 1.dp
    ) {
        if (entry == null) {
            // ── Celda vacía ─────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(136.dp)
                    .clickable(onClick = onAddOrReplace),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(spacing_2)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = accent.copy(alpha = 0.1f)
                    ) {
                        Icon(
                            Icons.Outlined.Add,
                            contentDescription = "Agregar",
                            tint     = accent,
                            modifier = Modifier
                                .padding(spacing_3)
                                .size(20.dp)
                        )
                    }
                    Text(
                        text  = "Agregar",
                        style = MaterialTheme.typography.labelSmall,
                        color = accent,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        } else {
            // ── Celda con receta ────────────────────────────────────────
            Column {
                // Imagen — clickable abre el modal de detalle
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .clip(RoundedCornerShape(topStart = rounded_md, topEnd = rounded_md))
                        .clickable { onRecipeClick(entry.recipeId) }
                ) {
                    RecipeImage(
                        recipeTitle = entry.recipeTitle,
                        imageRes    = entry.imageRes,
                        modifier    = Modifier.fillMaxSize()
                    )
                    // Gradiente inferior sobre la imagen
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.35f))
                                )
                            )
                    )
                }

                // Título y acciones
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = spacing_3)
                        .padding(top = spacing_2, bottom = spacing_2)
                ) {
                    Text(
                        text     = entry.recipeTitle.ifBlank { "Receta" },
                        style    = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color    = OnSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 16.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onRecipeClick(entry.recipeId) }
                    )

                    Spacer(Modifier.height(spacing_2))

                    // Fila de acciones: Editar + Eliminar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment     = Alignment.CenterVertically
                    ) {
                        // Botón cambiar
                        Surface(
                            modifier  = Modifier
                                .size(28.dp)
                                .clickable(onClick = onAddOrReplace),
                            shape = CircleShape,
                            color = accent.copy(alpha = 0.12f)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Outlined.Edit,
                                    contentDescription = "Cambiar receta",
                                    tint     = accent,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }

                        Spacer(Modifier.width(spacing_2))

                        // Botón quitar
                        Surface(
                            modifier  = Modifier
                                .size(28.dp)
                                .clickable(onClick = onRemove),
                            shape = CircleShape,
                            color = SurfaceContainerHigh
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Outlined.Close,
                                    contentDescription = "Quitar",
                                    tint     = OnSurfaceVariant,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
