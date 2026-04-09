package com.example.recipe_generator.presentation.generator

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.recipe_generator.presentation.components.EditorialBottomNavBar
import com.example.recipe_generator.presentation.components.HomeEditorialTopAppBar
import com.example.recipe_generator.presentation.components.editorialBottomBarContentPadding
import com.example.recipe_generator.presentation.components.editorialTopBarContentPadding
import com.example.recipe_generator.presentation.theme.OnPrimary
import com.example.recipe_generator.presentation.theme.OnPrimaryFixed
import com.example.recipe_generator.presentation.theme.OnSecondaryContainer
import com.example.recipe_generator.presentation.theme.OnSurface
import com.example.recipe_generator.presentation.theme.OnSurfaceVariant
import com.example.recipe_generator.presentation.theme.Outline
import com.example.recipe_generator.presentation.theme.Primary
import com.example.recipe_generator.presentation.theme.PrimaryContainer
import com.example.recipe_generator.presentation.theme.PrimaryFixed
import com.example.recipe_generator.presentation.theme.Secondary
import com.example.recipe_generator.presentation.theme.SecondaryContainer
import com.example.recipe_generator.presentation.theme.SurfaceContainer
import com.example.recipe_generator.presentation.theme.SurfaceContainerHigh
import com.example.recipe_generator.presentation.theme.SurfaceContainerHighest
import com.example.recipe_generator.presentation.theme.SurfaceContainerLow
import com.example.recipe_generator.presentation.theme.SurfaceContainerLowest
import com.example.recipe_generator.presentation.theme.rounded_full
import com.example.recipe_generator.presentation.theme.rounded_lg
import com.example.recipe_generator.presentation.theme.rounded_md
import com.example.recipe_generator.presentation.theme.spacing_10
import com.example.recipe_generator.presentation.theme.spacing_12
import com.example.recipe_generator.presentation.theme.spacing_2
import com.example.recipe_generator.presentation.theme.spacing_3
import com.example.recipe_generator.presentation.theme.spacing_4
import com.example.recipe_generator.presentation.theme.spacing_6

// ── Data ────────────────────────────────────────────────────────────

private data class DietOption(val iconName: String, val label: String)

private val dietOptions = listOf(
    DietOption("eco", "Vegetariano"),
    DietOption("energy_savings_leaf", "Vegano"),
    DietOption("grain", "Sin gluten"),
    DietOption("opacity", "Sin lácteos"),
    DietOption("nutrition", "Keto"),
    DietOption("egg", "Paleo")
)

private val recipeTypeLabels = listOf(
    "Desayunos",
    "Cenas Ligeras",
    "Almuerzos Ejecutivos",
    "Postres Healthy",
    "Snacks"
)

// ── Main Screen ─────────────────────────────────────────────────────

@Composable
fun MenuGeneratorScreen(
    selectedNavItem: Int = 4,
    selectedDiets: Set<String> = emptySet(),
    onToggleDiet: (String) -> Unit = {},
    selectedDifficulty: String = "Difícil",
    onDifficultySelected: (String) -> Unit = {},
    portions: Int = 4,
    onPortionsChange: (Int) -> Unit = {},
    selectedRecipeTypes: Set<String> = emptySet(),
    onToggleRecipeType: (String) -> Unit = {},
    uiState: GeneratorUiState = GeneratorUiState(),
    onGenerateClick: () -> Unit = {},
    onGoToPlan: () -> Unit = {},
    onNavigate: (Int) -> Unit = {}
) {
    val surfaceBg = com.example.recipe_generator.presentation.theme.Surface
    val difficultyLevel = difficultyLabelToSliderValue(selectedDifficulty)
    val isGenerating = uiState.isGenerating

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(surfaceBg)
    ) {
        val topContentPadding = editorialTopBarContentPadding()
        val bottomContentPadding = editorialBottomBarContentPadding()

        // Scrollable Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = topContentPadding,
                    bottom = bottomContentPadding + spacing_6
                )
                .verticalScroll(rememberScrollState())
                .padding(horizontal = spacing_6)
        ) {
            Spacer(modifier = Modifier.height(spacing_10))

            // ═══════════════════════════════════════════════════════════
            // SECTION 1: HERO + TITLE
            // ═══════════════════════════════════════════════════════════
            Text(
                text = "Generador de Menú",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.ExtraBold,
                color = OnSurface,
                fontSize = 32.sp
            )
            Text(
                text = "Personaliza tu experiencia culinaria semanal con precisión editorial.",
                style = MaterialTheme.typography.bodyMedium,
                color = OnSurfaceVariant.copy(alpha = 0.7f),
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = spacing_2)
            )

            Spacer(modifier = Modifier.height(spacing_10))

            // ═══════════════════════════════════════════════════════════
            // SECTION 2: PREFERENCIAS DIETÉTICAS (BENTO GRID)
            // ═══════════════════════════════════════════════════════════
            Text(
                text = "Preferencias Dietéticas",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = OnSurface,
                modifier = Modifier.padding(bottom = spacing_3, start = spacing_2)
            )

            // Diet options in grid (3 rows × 2 cols)
            Column(verticalArrangement = Arrangement.spacedBy(spacing_3)) {
                for (row in 0..2) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(spacing_3)
                    ) {
                        for (col in 0..1) {
                            val i = row * 2 + col
                            if (i < dietOptions.size) {
                                val opt = dietOptions[i]
                                val selected = opt.label in selectedDiets
                                DietOptionChip(
                                    option = opt,
                                    selected = selected,
                                    onClick = { onToggleDiet(opt.label) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(spacing_10))

            // ═══════════════════════════════════════════════════════════
            // SECTION 3: DIFICULTAD Y PORCIONES (ASYMMETRIC LAYOUT)
            // ═══════════════════════════════════════════════════════════
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing_6)
            ) {
                // LEFT: Difficulty Slider (less wide)
                Box(modifier = Modifier.weight(0.6f)) {
                    DifficultyCard(
                        level = difficultyLevel,
                        onLevelChange = { onDifficultySelected(sliderValueToDifficultyLabel(it)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // RIGHT: Portions Stepper (more wide)
                Box(modifier = Modifier.weight(0.4f)) {
                    PortionsCard(
                        count = portions,
                        onChange = onPortionsChange,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(spacing_10))

            // ═══════════════════════════════════════════════════════════
            // SECTION 4: TIPOS DE RECETAS (MULTISELECT)
            // ═══════════════════════════════════════════════════════════
            Text(
                text = "Tipos de Recetas Preferidas",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = OnSurface,
                modifier = Modifier.padding(bottom = spacing_2, start = spacing_2)
            )
            RecipeTypeChips(
                selected = selectedRecipeTypes,
                onToggle = onToggleRecipeType
            )

            Spacer(modifier = Modifier.height(spacing_10))

            // ═══════════════════════════════════════════════════════════
            // SECTION 5: GENERATION CONTROL
            // ═══════════════════════════════════════════════════════════
            // Progress Bar (inactive)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(SurfaceContainer.copy(alpha = 0.4f))
            )

            Spacer(modifier = Modifier.height(spacing_6))

            // Generate Button (Gradient + Shadow)
            Button(
                onClick = onGenerateClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .shadow(
                        elevation = 16.dp,
                        shape = RoundedCornerShape(rounded_full),
                        ambientColor = Primary.copy(alpha = 0.25f)
                    ),
                shape = RoundedCornerShape(rounded_full),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                listOf(Primary, PrimaryContainer)
                            ),
                            shape = RoundedCornerShape(rounded_full)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "GENERAR MENÚ SEMANAL",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.8.sp
                    )
                }
            }

            // Estado del generador
            when {
                uiState.isGenerating -> {
                    Text(
                        text = "Generando plan semanal...",
                        modifier = Modifier.padding(vertical = spacing_4),
                        style = MaterialTheme.typography.bodyMedium,
                        color = OnSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                }
                uiState.planSaved -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(rounded_lg))
                            .background(Secondary.copy(alpha = 0.12f))
                            .padding(spacing_6)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(spacing_4)) {
                            Text(
                                text = "✓ Plan generado y guardado",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = Secondary
                            )
                            Text(
                                text = "${uiState.recipesFound} receta(s) asignadas. Ve a Mi Plan para verlo.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = OnSurfaceVariant
                            )
                            Button(
                                onClick = { onGoToPlan() },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(rounded_full),
                                colors = ButtonDefaults.buttonColors(containerColor = Secondary)
                            ) {
                                Text("Ver Mi Plan Semanal", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
                uiState.error != null -> {
                    Text(
                        text = uiState.error,
                        modifier = Modifier.padding(vertical = spacing_4),
                        style = MaterialTheme.typography.bodyMedium,
                        color = androidx.compose.ui.graphics.Color(0xFFBA1A1A),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(spacing_6))
        }

        // Bottom Nav Bar
        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
            EditorialBottomNavBar(
                selectedItem = selectedNavItem,
                onItemSelected = onNavigate
            )
        }

        // Top App Bar - Fixed at top
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .background(surfaceBg)
        ) {
            HomeEditorialTopAppBar(title = "Generador de Menú")
        }
    }
}

private fun difficultyLabelToSliderValue(difficulty: String): Float {
    return when (difficulty) {
        "Fácil" -> 1f
        "Medio" -> 2f
        else -> 3f
    }
}

private fun sliderValueToDifficultyLabel(value: Float): String {
    return when (value.toInt()) {
        1 -> "Fácil"
        2 -> "Medio"
        else -> "Difícil"
    }
}

// ── Diet Option Chip ────────────────────────────────────────────────

@Composable
private fun DietOptionChip(
    option: DietOption,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor by animateColorAsState(
        targetValue = if (selected) SecondaryContainer else SurfaceContainerLow,
        animationSpec = tween(300),
        label = "chipBg"
    )
    val textColor by animateColorAsState(
        targetValue = if (selected) OnSecondaryContainer else OnSurface,
        animationSpec = tween(300),
        label = "chipText"
    )
    val iconColor by animateColorAsState(
        targetValue = if (selected) OnSecondaryContainer else Secondary,
        animationSpec = tween(300),
        label = "chipIcon"
    )

    Box(
        modifier = modifier
            .height(88.dp)
            .clip(RoundedCornerShape(rounded_md))
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(spacing_4)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = when(option.iconName) {
                    "eco" -> Icons.Filled.Favorite
                    "energy_savings_leaf" -> Icons.Filled.FavoriteBorder
                    "grain" -> Icons.Filled.Home
                    "opacity" -> Icons.Filled.Search
                    "nutrition" -> Icons.Filled.Settings
                    "egg" -> Icons.Filled.Favorite
                    else -> Icons.Filled.Favorite
                },
                contentDescription = option.label,
                modifier = Modifier.size(24.dp),
                tint = iconColor
            )
            Text(
                text = option.label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = textColor,
                textAlign = TextAlign.Center,
                fontSize = 11.sp
            )
        }
    }
}

// ── Difficulty Slider Card ──────────────────────────────────────────

@Composable
private fun DifficultyCard(
    level: Float,
    onLevelChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val label = when (level.toInt()) {
        1 -> "Fácil"; 2 -> "Medio"; else -> "Difícil"
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(rounded_lg))
            .background(SurfaceContainerLow)
    ) {
        Column(
            modifier = Modifier.padding(spacing_6),
            verticalArrangement = Arrangement.spacedBy(spacing_4)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "DIFICULTAD MÁXIMA",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = OnSurfaceVariant,
                    letterSpacing = 1.2.sp
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = Primary,
                    fontSize = 14.sp
                )
            }

            Slider(
                value = level,
                onValueChange = onLevelChange,
                valueRange = 1f..3f,
                steps = 1,
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = Primary,
                    activeTrackColor = Primary,
                    inactiveTrackColor = SurfaceContainer
                )
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf("FÁCIL", "MEDIO", "DIFÍCIL").forEach { t ->
                    Text(
                        text = t,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Outline,
                        letterSpacing = (-0.3).sp
                    )
                }
            }
        }
    }
}

// ── Portions Stepper Card ───────────────────────────────────────────

@Composable
private fun PortionsCard(
    count: Int,
    onChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(rounded_lg))
            .background(SurfaceContainerHigh)
    ) {
        Column(modifier = Modifier.padding(spacing_6)) {
            Text(
                text = "NÚMERO DE PORCIONES",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = OnSurfaceVariant,
                letterSpacing = 1.2.sp,
                modifier = Modifier.padding(bottom = spacing_4)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(rounded_full))
                    .background(SurfaceContainerLowest)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(spacing_2),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // minus
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .clickable { if (count > 1) onChange(count - 1) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "−",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Light,
                            color = OnSurface
                        )
                    }

                    Text(
                        text = "$count",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = OnSurface,
                        textAlign = TextAlign.Center
                    )

                    // plus
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Primary)
                            .clickable { onChange(count + 1) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Aumentar",
                            tint = OnPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

// ── Recipe Type Chips ───────────────────────────────────────────────

@Composable
private fun RecipeTypeChips(
    selected: Set<String>,
    onToggle: (String) -> Unit
) {
    // Row 1: first 2 chips, Row 2: next 2, Row 3: last one
    Column(verticalArrangement = Arrangement.spacedBy(spacing_3)) {
        // Build rows manually to wrap
        val rows = listOf(
            recipeTypeLabels.take(2),
            recipeTypeLabels.drop(2).take(2),
            recipeTypeLabels.drop(4)
        ).filter { it.isNotEmpty() }

        rows.forEach { rowLabels ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing_3)
            ) {
                rowLabels.forEach { label ->
                    val isSelected = label in selected
                    val bg by animateColorAsState(
                        if (isSelected) PrimaryFixed else SurfaceContainerHighest,
                        tween(300), label = "chipBg"
                    )
                    val fg by animateColorAsState(
                        if (isSelected) OnPrimaryFixed else OnSurfaceVariant,
                        tween(300), label = "chipFg"
                    )

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(rounded_full))
                            .background(bg)
                            .clickable { onToggle(label) }
                            .padding(horizontal = spacing_4, vertical = spacing_3),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(spacing_2)
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = fg,
                                fontSize = 12.sp
                            )
                            Icon(
                                imageVector = if (isSelected) Icons.Filled.Check else Icons.Filled.Add,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = fg
                            )
                        }
                    }
                }
            }
        }
    }
}
