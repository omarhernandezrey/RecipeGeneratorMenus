package com.example.recipe_generator.presentation.generator

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.recipe_generator.presentation.theme.OnSurface
import com.example.recipe_generator.presentation.theme.OnSurfaceVariant
import com.example.recipe_generator.presentation.theme.Primary
import com.example.recipe_generator.presentation.theme.Surface
import com.example.recipe_generator.presentation.theme.SurfaceContainerLow
import com.example.recipe_generator.presentation.theme.rounded_full
import com.example.recipe_generator.presentation.theme.rounded_lg
import com.example.recipe_generator.presentation.theme.rounded_xl
import com.example.recipe_generator.presentation.theme.spacing_2
import com.example.recipe_generator.presentation.theme.spacing_3
import com.example.recipe_generator.presentation.theme.spacing_4
import com.example.recipe_generator.presentation.theme.spacing_6
import com.example.recipe_generator.presentation.theme.spacing_8
import kotlinx.coroutines.delay

/**
 * Modal animado que celebra la generación exitosa del plan semanal.
 *
 * Animaciones:
 *  - Scrim: fadeIn tween(300)
 *  - Card: slideInVertically spring(MediumBouncy) desde abajo
 *  - Checkmark: scale 0→1 spring(MediumBouncy) con delay 350ms
 *  - Emojis: float infinito (sin/cos offset)
 *  - Desglose de comidas: slideIn + fadeIn con delay 600ms
 */
@Composable
fun GeneratedPlanModal(
    uiState: GeneratorUiState,
    onGoToPlan: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false)
    ) {
        // ── Animation states ─────────────────────────────────────
        var scrimVisible    by remember { mutableStateOf(false) }
        var cardVisible     by remember { mutableStateOf(false) }
        var checkVisible    by remember { mutableStateOf(false) }
        var detailsVisible  by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            scrimVisible = true
            delay(80)
            cardVisible = true
            delay(350)
            checkVisible = true
            delay(250)
            detailsVisible = true
        }

        // Checkmark scale
        val checkScale by animateFloatAsState(
            targetValue = if (checkVisible) 1f else 0f,
            animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMediumLow),
            label = "checkScale"
        )

        // Floating emoji infinite loop
        val infiniteTransition = rememberInfiniteTransition(label = "confetti")
        val float1 by infiniteTransition.animateFloat(
            initialValue = 0f, targetValue = -14f,
            animationSpec = infiniteRepeatable(tween(1600, easing = FastOutSlowInEasing), RepeatMode.Reverse),
            label = "f1"
        )
        val float2 by infiniteTransition.animateFloat(
            initialValue = -10f, targetValue = 6f,
            animationSpec = infiniteRepeatable(tween(2000, easing = LinearEasing), RepeatMode.Reverse),
            label = "f2"
        )
        val float3 by infiniteTransition.animateFloat(
            initialValue = 0f, targetValue = -18f,
            animationSpec = infiniteRepeatable(tween(1300, easing = FastOutSlowInEasing), RepeatMode.Reverse),
            label = "f3"
        )

        Box(modifier = Modifier.fillMaxSize()) {

            // ── Scrim ────────────────────────────────────────────
            AnimatedVisibility(
                visible = scrimVisible,
                enter   = fadeIn(animationSpec = tween(300))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.55f))
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) { onDismiss() }
                )
            }

            // ── Bottom sheet card ────────────────────────────────
            AnimatedVisibility(
                visible  = cardVisible,
                enter    = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec  = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow)
                ) + fadeIn(tween(200)),
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(topStart = rounded_xl, topEnd = rounded_xl))
                        .background(Surface)
                        .padding(horizontal = spacing_6)
                        .padding(bottom = spacing_8)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) { /* block clicks through */ },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Handle
                    Spacer(modifier = Modifier.height(spacing_3))
                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(4.dp)
                            .background(OnSurface.copy(alpha = 0.12f), RoundedCornerShape(2.dp))
                    )
                    Spacer(modifier = Modifier.height(spacing_6))

                    // ── Floating emojis ──────────────────────────
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(spacing_4),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "🎉", fontSize = 28.sp,
                            modifier = Modifier.offset { IntOffset(0, float1.dp.roundToPx()) }
                        )
                        Text(
                            "✨", fontSize = 22.sp,
                            modifier = Modifier.offset { IntOffset(0, float2.dp.roundToPx()) }
                        )
                        // Big animated checkmark in the center
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .scale(checkScale)
                                .background(
                                    brush = Brush.radialGradient(
                                        listOf(Color(0xFF66BB6A), Color(0xFF2E7D32))
                                    ),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "✓",
                                color      = Color.White,
                                fontSize   = 36.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                        Text(
                            "✨", fontSize = 22.sp,
                            modifier = Modifier.offset { IntOffset(0, float2.dp.roundToPx()) }
                        )
                        Text(
                            "🎊", fontSize = 28.sp,
                            modifier = Modifier.offset { IntOffset(0, float3.dp.roundToPx()) }
                        )
                    }

                    Spacer(modifier = Modifier.height(spacing_6))

                    // ── Title ────────────────────────────────────
                    Text(
                        text       = "¡Plan Semanal\nGenerado!",
                        fontSize   = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color      = OnSurface,
                        textAlign  = TextAlign.Center,
                        lineHeight = 34.sp
                    )
                    Spacer(modifier = Modifier.height(spacing_2))
                    Text(
                        text      = "Tu menú para los próximos 7 días está listo 🍽️",
                        fontSize  = 14.sp,
                        color     = OnSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(spacing_6))

                    HorizontalDivider(color = OnSurface.copy(alpha = 0.07f))

                    Spacer(modifier = Modifier.height(spacing_4))

                    // ── Meal breakdown (staggered) ────────────────
                    AnimatedVisibility(
                        visible = detailsVisible,
                        enter   = fadeIn(tween(400)) +
                                slideInVertically(initialOffsetY = { 60 }, animationSpec = tween(400))
                    ) {
                        Column(
                            modifier  = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(rounded_lg))
                                .background(SurfaceContainerLow)
                                .padding(spacing_4),
                            verticalArrangement = Arrangement.spacedBy(spacing_3)
                        ) {
                            Text(
                                "RESUMEN DEL PLAN",
                                fontSize      = 10.sp,
                                fontWeight    = FontWeight.Bold,
                                color         = OnSurfaceVariant.copy(alpha = 0.6f),
                                letterSpacing = 1.5.sp
                            )
                            if (uiState.hasBreakfasts) MealRow("🌅", "Desayuno", "7 días asignados")
                            if (uiState.hasLunches)    MealRow("☀️", "Almuerzo", "7 días asignados")
                            if (uiState.hasDinners)    MealRow("🌙", "Cena",     "7 días asignados")

                            val missing = buildList {
                                if (!uiState.hasBreakfasts) add("Desayuno")
                                if (!uiState.hasLunches)    add("Almuerzo")
                                if (!uiState.hasDinners)    add("Cena")
                            }
                            if (missing.isNotEmpty()) {
                                Text(
                                    text  = "⚠️ Sin recetas para: ${missing.joinToString(", ")}",
                                    fontSize = 12.sp,
                                    color = Color(0xFFBA1A1A).copy(alpha = 0.8f)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(spacing_6))

                    // ── CTA buttons ──────────────────────────────
                    Button(
                        onClick  = onGoToPlan,
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape    = RoundedCornerShape(rounded_full),
                        colors   = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.horizontalGradient(listOf(Primary, Color(0xFF7B3FC4))),
                                    RoundedCornerShape(rounded_full)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Ver Mi Plan Semanal →",
                                color      = Color.White,
                                fontSize   = 15.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }

                    TextButton(
                        onClick  = onDismiss,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "Listo, ¡gracias!",
                            color      = OnSurfaceVariant,
                            fontSize   = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

// ── Meal row inside breakdown card ───────────────────────────────────

@Composable
private fun MealRow(emoji: String, label: String, detail: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing_3)
        ) {
            Text(emoji, fontSize = 20.sp)
            Text(label, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = OnSurface)
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing_2)
        ) {
            Text(detail, fontSize = 12.sp, color = OnSurfaceVariant)
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .background(Color(0xFF2E7D32).copy(alpha = 0.12f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("✓", fontSize = 10.sp, color = Color(0xFF2E7D32), fontWeight = FontWeight.ExtraBold)
            }
        }
    }
}
