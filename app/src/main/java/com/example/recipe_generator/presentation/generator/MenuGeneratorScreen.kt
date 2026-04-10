package com.example.recipe_generator.presentation.generator

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.recipe_generator.data.notification.NotificationHelper
import com.example.recipe_generator.presentation.theme.OnSecondaryContainer
import com.example.recipe_generator.presentation.theme.OnSurface
import com.example.recipe_generator.presentation.theme.OnSurfaceVariant
import com.example.recipe_generator.presentation.theme.Primary
import com.example.recipe_generator.presentation.theme.SecondaryContainer
import com.example.recipe_generator.presentation.theme.Surface
import com.example.recipe_generator.presentation.theme.SurfaceContainerHigh
import com.example.recipe_generator.presentation.theme.SurfaceContainerLow
import com.example.recipe_generator.presentation.theme.rounded_full
import com.example.recipe_generator.presentation.theme.rounded_lg
import com.example.recipe_generator.presentation.theme.rounded_md
import com.example.recipe_generator.presentation.theme.spacing_2
import com.example.recipe_generator.presentation.theme.spacing_3
import com.example.recipe_generator.presentation.theme.spacing_4
import com.example.recipe_generator.presentation.theme.spacing_6
import com.example.recipe_generator.presentation.theme.spacing_8

// ── Data ─────────────────────────────────────────────────────────────

internal data class DietOption(val emoji: String, val label: String)

internal val dietOptions = listOf(
    DietOption("🥗", "Vegetariano"),
    DietOption("🌿", "Vegano"),
    DietOption("🌾", "Sin gluten"),
    DietOption("🥛", "Sin lácteos"),
    DietOption("🥑", "Keto"),
    DietOption("🍖", "Paleo")
)

internal data class MealTypeOption(val emoji: String, val label: String, val accent: Color)

internal val mealTypeOptions = listOf(
    MealTypeOption("🌅", "Desayuno", Color(0xFFD84315)),
    MealTypeOption("☀️", "Almuerzo", Color(0xFF2E7D32)),
    MealTypeOption("🌙", "Cena",     Color(0xFF4527A0))
)

internal data class DifficultyOption(val emoji: String, val label: String, val color: Color)

internal val difficultyOptions = listOf(
    DifficultyOption("⚡", "Fácil",   Color(0xFF2E7D32)),
    DifficultyOption("🔥", "Medio",   Color(0xFFE65100)),
    DifficultyOption("💀", "Difícil", Color(0xFFC62828))
)

// ── Embeddable content (sin nav bar ni top bar) ──────────────────────

/**
 * Contenido del Generador sin nav bar — para usar dentro de UnifiedRecipesScreen.
 */
@Composable
fun GeneratorTabContent(
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
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Modal state — se abre cuando el plan se guarda
    var showModal by remember { mutableStateOf(false) }

    // Trigger: mostrar modal + notificación cuando planSaved cambia a true
    LaunchedEffect(uiState.planSaved) {
        if (uiState.planSaved) {
            showModal = true
            NotificationHelper.showPlanGeneratedNotification(
                context       = context,
                hasBreakfasts = uiState.hasBreakfasts,
                hasLunches    = uiState.hasLunches,
                hasDinners    = uiState.hasDinners
            )
        }
    }

    Box(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = spacing_6),
            verticalArrangement = Arrangement.spacedBy(spacing_8)
        ) {
            Spacer(modifier = Modifier.height(spacing_4))

            HeroBanner()

            Column {
                SectionLabel("Preferencias Dietéticas")
                Column(verticalArrangement = Arrangement.spacedBy(spacing_3)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(spacing_3)) {
                        dietOptions.take(3).forEach { opt ->
                            DietPill(opt, opt.label in selectedDiets, { onToggleDiet(opt.label) }, Modifier.weight(1f))
                        }
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(spacing_3)) {
                        dietOptions.drop(3).forEach { opt ->
                            DietPill(opt, opt.label in selectedDiets, { onToggleDiet(opt.label) }, Modifier.weight(1f))
                        }
                    }
                }
            }

            Column {
                SectionLabel("Tipo de Comida")
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(spacing_3)) {
                    mealTypeOptions.forEach { opt ->
                        MealTypeCard(opt, opt.label in selectedRecipeTypes, { onToggleRecipeType(opt.label) }, Modifier.weight(1f))
                    }
                }
            }

            Column {
                SectionLabel("Dificultad Máxima")
                DifficultySelector(selectedDifficulty, onDifficultySelected)
            }

            PortionsStepper(portions, onPortionsChange)

            // Solo muestra el botón de generar — el resultado va al modal
            GenerateCtaSection(
                uiState         = uiState.copy(planSaved = false, error = uiState.error),
                onGenerateClick = onGenerateClick,
                onGoToPlan      = onGoToPlan
            )

            Spacer(modifier = Modifier.height(spacing_6))
        }

        // ── Modal de éxito ───────────────────────────────────────
        if (showModal) {
            GeneratedPlanModal(
                uiState    = uiState,
                onGoToPlan = {
                    showModal = false
                    onGoToPlan()
                },
                onDismiss  = { showModal = false }
            )
        }
    }
}

// ── CTA section ──────────────────────────────────────────────────────

@Composable
internal fun GenerateCtaSection(
    uiState: GeneratorUiState,
    onGenerateClick: () -> Unit,
    onGoToPlan: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(spacing_4)) {
        Button(
            onClick = { if (!uiState.isGenerating) onGenerateClick() },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .shadow(12.dp, RoundedCornerShape(rounded_full), ambientColor = Primary.copy(alpha = 0.3f)),
            shape = RoundedCornerShape(rounded_full),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            contentPadding = PaddingValues(0.dp),
            enabled = !uiState.isGenerating
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = if (uiState.isGenerating)
                            Brush.horizontalGradient(listOf(SurfaceContainerLow, SurfaceContainerHigh))
                        else
                            Brush.horizontalGradient(listOf(Primary, Color(0xFF7B3FC4))),
                        shape = RoundedCornerShape(rounded_full)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (uiState.isGenerating) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(spacing_3)) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Primary, strokeWidth = 2.dp)
                        Text("Generando...", color = OnSurfaceVariant, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                    }
                } else {
                    Text("✨  GENERAR MENÚ SEMANAL", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 0.5.sp)
                }
            }
        }

        when {
            uiState.planSaved -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(rounded_lg))
                        .background(Color(0xFF1B5E20).copy(alpha = 0.08f))
                        .border(1.dp, Color(0xFF2E7D32).copy(alpha = 0.35f), RoundedCornerShape(rounded_lg))
                        .padding(spacing_6)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(spacing_4)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(spacing_3)) {
                            Box(Modifier.size(32.dp).background(Color(0xFF2E7D32), CircleShape), contentAlignment = Alignment.Center) {
                                Text("✓", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                            }
                            Column {
                                Text("¡Plan generado!", fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = Color(0xFF1B5E20))
                                Text("${uiState.recipesFound} recetas asignadas para la semana", fontSize = 12.sp, color = OnSurfaceVariant)
                            }
                        }
                        Button(
                            onClick = onGoToPlan,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(rounded_full),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                        ) {
                            Text("Ver Mi Plan →", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            uiState.error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(rounded_lg))
                        .background(Color(0xFFBA1A1A).copy(alpha = 0.07f))
                        .border(1.dp, Color(0xFFBA1A1A).copy(alpha = 0.3f), RoundedCornerShape(rounded_lg))
                        .padding(spacing_6)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(spacing_3)) {
                        Text("⚠️", fontSize = 18.sp)
                        Text(uiState.error, fontSize = 13.sp, color = Color(0xFFBA1A1A), fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}

// ── Hero Banner ──────────────────────────────────────────────────────

@Composable
internal fun HeroBanner() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .clip(RoundedCornerShape(rounded_lg))
            .background(Brush.linearGradient(listOf(Color(0xFF12082A), Color(0xFF2D1B69), Color(0xFF4C27A8))))
            .padding(horizontal = spacing_6, vertical = spacing_4)
    ) {
        Row(modifier = Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text("MENÚ SEMANAL", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.5f), letterSpacing = 2.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Tu plan de\ncomidas perfecto", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = Color.White, lineHeight = 28.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Text("7 días · 21 comidas", fontSize = 12.sp, color = Color.White.copy(alpha = 0.55f))
            }
            Text("🍽️", fontSize = 64.sp)
        }
    }
}

// ── Section Label ────────────────────────────────────────────────────

@Composable
internal fun SectionLabel(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = spacing_4)) {
        Box(Modifier.width(3.dp).height(16.dp).background(Primary, RoundedCornerShape(2.dp)))
        Spacer(modifier = Modifier.width(spacing_3))
        Text(text, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = OnSurface)
    }
}

// ── Diet Pill ────────────────────────────────────────────────────────

@Composable
private fun DietPill(option: DietOption, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val bgColor by animateColorAsState(if (selected) SecondaryContainer else SurfaceContainerLow, tween(250), label = "pillBg")
    val textColor by animateColorAsState(if (selected) OnSecondaryContainer else OnSurfaceVariant, tween(250), label = "pillText")

    Box(
        modifier = modifier.height(56.dp).clip(RoundedCornerShape(rounded_md)).background(bgColor).clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text(option.emoji, fontSize = 20.sp, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(2.dp))
            Text(option.label, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = textColor, textAlign = TextAlign.Center, maxLines = 1)
        }
    }
}

// ── Meal Type Card ───────────────────────────────────────────────────

@Composable
private fun MealTypeCard(option: MealTypeOption, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val bgColor by animateColorAsState(
        if (isSelected) option.accent.copy(alpha = 0.1f) else SurfaceContainerLow, tween(300), label = "mealBg"
    )

    Box(
        modifier = modifier
            .height(90.dp)
            .clip(RoundedCornerShape(rounded_lg))
            .background(bgColor)
            .then(if (isSelected) Modifier.border(2.dp, option.accent, RoundedCornerShape(rounded_lg)) else Modifier)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center, modifier = Modifier.padding(spacing_2)) {
            Text(option.emoji, fontSize = 28.sp, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                option.label,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
                color = if (isSelected) option.accent else OnSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

// ── Difficulty Selector ──────────────────────────────────────────────

@Composable
private fun DifficultySelector(selected: String, onSelect: (String) -> Unit) {
    Box(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(rounded_lg)).background(SurfaceContainerLow).padding(spacing_2)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            difficultyOptions.forEach { opt ->
                val isSelected = opt.label == selected
                val bg by animateColorAsState(if (isSelected) opt.color else Color.Transparent, tween(250), label = "diffBg")
                val fg by animateColorAsState(if (isSelected) Color.White else OnSurfaceVariant, tween(250), label = "diffFg")

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(rounded_md))
                        .background(bg)
                        .clickable { onSelect(opt.label) }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(opt.emoji, fontSize = 20.sp)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(opt.label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = fg)
                    }
                }
            }
        }
    }
}

// ── Portions Stepper ─────────────────────────────────────────────────

@Composable
private fun PortionsStepper(count: Int, onChange: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(rounded_lg))
            .background(SurfaceContainerLow)
            .padding(horizontal = spacing_6, vertical = spacing_4),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text("Porciones", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = OnSurface)
            Text("personas por comida", fontSize = 11.sp, color = OnSurfaceVariant)
        }

        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(spacing_4)) {
            Box(
                modifier = Modifier.size(36.dp).clip(CircleShape).background(SurfaceContainerHigh).clickable { if (count > 1) onChange(count - 1) },
                contentAlignment = Alignment.Center
            ) {
                Text("−", fontSize = 20.sp, fontWeight = FontWeight.Medium, color = OnSurface)
            }

            Text("$count", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Primary, textAlign = TextAlign.Center, modifier = Modifier.widthIn(min = 28.dp))

            Box(
                modifier = Modifier.size(36.dp).clip(CircleShape).background(Primary).clickable { onChange(count + 1) },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Add, "Aumentar", tint = Color.White, modifier = Modifier.size(18.dp))
            }
        }
    }
}

// ── Standalone screen wrapper (no longer used as tab) ────────────────

@Composable
fun MenuGeneratorScreen(
    selectedNavItem: Int = 3,
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
    Box(modifier = Modifier.fillMaxSize().background(Surface)) {
        GeneratorTabContent(
            selectedDiets, onToggleDiet, selectedDifficulty, onDifficultySelected,
            portions, onPortionsChange, selectedRecipeTypes, onToggleRecipeType,
            uiState, onGenerateClick, onGoToPlan,
            modifier = Modifier.fillMaxSize()
        )
    }
}
