package com.example.recipe_generator.ui.screens

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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.recipe_generator.ui.components.EditorialBottomNavBar
import com.example.recipe_generator.ui.components.HomeEditorialTopAppBar
import com.example.recipe_generator.ui.components.editorialBottomBarContentPadding
import com.example.recipe_generator.ui.components.editorialTopBarContentPadding
import com.example.recipe_generator.ui.theme.OnPrimary
import com.example.recipe_generator.ui.theme.OnPrimaryFixed
import com.example.recipe_generator.ui.theme.OnSecondaryContainer
import com.example.recipe_generator.ui.theme.OnSurface
import com.example.recipe_generator.ui.theme.OnSurfaceVariant
import com.example.recipe_generator.ui.theme.Outline
import com.example.recipe_generator.ui.theme.Primary
import com.example.recipe_generator.ui.theme.PrimaryContainer
import com.example.recipe_generator.ui.theme.PrimaryFixed
import com.example.recipe_generator.ui.theme.Secondary
import com.example.recipe_generator.ui.theme.SecondaryContainer
import com.example.recipe_generator.ui.theme.SurfaceContainer
import com.example.recipe_generator.ui.theme.SurfaceContainerHigh
import com.example.recipe_generator.ui.theme.SurfaceContainerHighest
import com.example.recipe_generator.ui.theme.SurfaceContainerLow
import com.example.recipe_generator.ui.theme.SurfaceContainerLowest
import com.example.recipe_generator.ui.theme.rounded_full
import com.example.recipe_generator.ui.theme.rounded_lg
import com.example.recipe_generator.ui.theme.rounded_md
import com.example.recipe_generator.ui.theme.spacing_10
import com.example.recipe_generator.ui.theme.spacing_12
import com.example.recipe_generator.ui.theme.spacing_2
import com.example.recipe_generator.ui.theme.spacing_4
import com.example.recipe_generator.ui.theme.spacing_6

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
fun MenuGeneratorScreen(onNavigate: (Int) -> Unit = {}) {
    var selectedNavItem by remember { mutableIntStateOf(2) }
    var selectedDiets by remember { mutableStateOf(setOf<String>()) }
    var difficultyLevel by remember { mutableFloatStateOf(2f) }
    var portions by remember { mutableIntStateOf(4) }
    var selectedRecipeTypes by remember {
        mutableStateOf(setOf("Desayunos", "Almuerzos Ejecutivos"))
    }

    val surfaceBg = com.example.recipe_generator.ui.theme.Surface

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(surfaceBg)
    ) {
        val topContentPadding = editorialTopBarContentPadding()

        // Scrollable Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = topContentPadding, bottom = editorialBottomBarContentPadding() + 40.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Hero / Title
            Column(modifier = Modifier.padding(horizontal = spacing_6, vertical = spacing_6)) {
                Text(
                    text = "Generador de Menú",
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = OnSurface,
                    lineHeight = 44.sp
                )
                Spacer(modifier = Modifier.height(spacing_2))
                Text(
                    text = "Personaliza tu experiencia culinaria semanal con precisión editorial.",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = OnSurfaceVariant.copy(alpha = 0.70f)
                )
            }

            Spacer(modifier = Modifier.height(spacing_10))

            // Preferencias Dietéticas Section
            Column(modifier = Modifier.padding(horizontal = spacing_6)) {
                Text(
                    text = "Preferencias Dietéticas",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = OnSurface,
                    modifier = Modifier.padding(bottom = spacing_4)
                )
                
                // Diet options in grid
                Column(verticalArrangement = Arrangement.spacedBy(spacing_4)) {
                    for (row in 0..2) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(spacing_4)
                        ) {
                            for (col in 0..1) {
                                val i = row * 2 + col
                                if (i < dietOptions.size) {
                                    val opt = dietOptions[i]
                                    val selected = opt.label in selectedDiets
                                    DietOptionChip(
                                        option = opt,
                                        selected = selected,
                                        onClick = {
                                            selectedDiets = if (selected)
                                                selectedDiets - opt.label
                                            else
                                                selectedDiets + opt.label
                                        },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(spacing_12))

            // Dificultad Card
            Box(modifier = Modifier.padding(horizontal = spacing_6)) {
                DifficultyCard(
                    level = difficultyLevel,
                    onLevelChange = { difficultyLevel = it },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(spacing_6))

            // Porciones Card
            Box(modifier = Modifier.padding(horizontal = spacing_6)) {
                PortionsCard(
                    count = portions,
                    onChange = { portions = it },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(spacing_12))

            // Tipos de Recetas Section
            Column(modifier = Modifier.padding(horizontal = spacing_6)) {
                Text(
                    text = "Tipos de Recetas Preferidas",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = OnSurface,
                    modifier = Modifier.padding(bottom = spacing_4)
                )
                RecipeTypeChips(
                    selected = selectedRecipeTypes,
                    onToggle = { label ->
                        selectedRecipeTypes = if (label in selectedRecipeTypes)
                            selectedRecipeTypes - label
                        else
                            selectedRecipeTypes + label
                    }
                )
            }

            Spacer(modifier = Modifier.height(spacing_12))

            // Generate Button
            Box(modifier = Modifier.padding(horizontal = spacing_6)) {
                Button(
                    onClick = { },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .shadow(
                            elevation = 12.dp,
                            shape = RoundedCornerShape(rounded_full),
                            ambientColor = Primary.copy(alpha = 0.20f)
                        ),
                    shape = RoundedCornerShape(rounded_full),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(listOf(Primary, PrimaryContainer)),
                                shape = RoundedCornerShape(rounded_full)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "GENERAR MENÚ SEMANAL",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(spacing_12))
        }

        // Bottom Nav Bar
        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
            EditorialBottomNavBar(
                selectedItem = selectedNavItem,
                onItemSelected = {
                    selectedNavItem = it
                    onNavigate(it)
                }
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
                        .padding(8.dp),
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
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        // Build rows manually to wrap
        val rows = listOf(
            recipeTypeLabels.take(2),
            recipeTypeLabels.drop(2).take(2),
            recipeTypeLabels.drop(4)
        ).filter { it.isNotEmpty() }

        rows.forEach { rowLabels ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
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
                            .padding(horizontal = 18.dp, vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(7.dp)
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
