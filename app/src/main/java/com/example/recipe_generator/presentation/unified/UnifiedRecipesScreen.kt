package com.example.recipe_generator.presentation.unified

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.recipe_generator.presentation.components.EditorialBottomNavBar
import com.example.recipe_generator.presentation.components.editorialBottomBarContentPadding
import com.example.recipe_generator.presentation.generator.GeneratorTabContent
import com.example.recipe_generator.presentation.generator.GeneratorUiState
import com.example.recipe_generator.presentation.myrecipes.MyRecipesScreen
import com.example.recipe_generator.presentation.theme.OnSurface
import com.example.recipe_generator.presentation.theme.OnSurfaceVariant
import com.example.recipe_generator.presentation.theme.Primary
import com.example.recipe_generator.presentation.theme.Surface
import com.example.recipe_generator.presentation.theme.SurfaceContainerLow
import com.example.recipe_generator.presentation.theme.rounded_full
import com.example.recipe_generator.presentation.theme.rounded_lg
import com.example.recipe_generator.presentation.theme.spacing_2
import com.example.recipe_generator.presentation.theme.spacing_3
import com.example.recipe_generator.presentation.theme.spacing_4
import com.example.recipe_generator.presentation.theme.spacing_6

// ── Tab indices ──────────────────────────────────────────────────────
private const val TAB_GENERATOR = 0
private const val TAB_MY_RECIPES = 1

/**
 * Pantalla unificada: Generador de Menú + Mis Recetas en un solo tab.
 *
 * Contiene un selector de tabs interno (Generador | Mis Recetas).
 * El bottom nav bar externo pertenece al AppShell (tab 3).
 */
@Composable
fun UnifiedRecipesScreen(
    selectedNavItem: Int = 3,
    onNavItemSelected: (Int) -> Unit = {},
    // Generator state
    selectedDiets: Set<String> = emptySet(),
    onToggleDiet: (String) -> Unit = {},
    selectedDifficulty: String = "Difícil",
    onDifficultySelected: (String) -> Unit = {},
    portions: Int = 4,
    onPortionsChange: (Int) -> Unit = {},
    selectedRecipeTypes: Set<String> = emptySet(),
    onToggleRecipeType: (String) -> Unit = {},
    generatorUiState: GeneratorUiState = GeneratorUiState(),
    onGenerateClick: () -> Unit = {},
    onGoToPlan: () -> Unit = {}
) {
    var activeTab by remember { mutableIntStateOf(TAB_GENERATOR) }

    val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val bottomPad = editorialBottomBarContentPadding()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Surface)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // ── Header ────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Surface)
                    .padding(top = statusBarPadding + spacing_3)
                    .padding(horizontal = spacing_6)
                    .padding(bottom = spacing_4)
            ) {
                Text(
                    text = "Cocina Personal",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = OnSurface
                )
                Text(
                    text = "Genera tu menú · Gestiona tus recetas",
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurfaceVariant.copy(alpha = 0.7f),
                    modifier = Modifier.padding(top = 2.dp)
                )

                Spacer(modifier = Modifier.height(spacing_4))

                // ── Tab switcher ─────────────────────────────────
                InnerTabSwitcher(
                    activeTab = activeTab,
                    onTabSelected = { activeTab = it }
                )
            }

            // ── Content ──────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = bottomPad)
            ) {
                when (activeTab) {
                    TAB_GENERATOR -> {
                        GeneratorTabContent(
                            selectedDiets = selectedDiets,
                            onToggleDiet = onToggleDiet,
                            selectedDifficulty = selectedDifficulty,
                            onDifficultySelected = onDifficultySelected,
                            portions = portions,
                            onPortionsChange = onPortionsChange,
                            selectedRecipeTypes = selectedRecipeTypes,
                            onToggleRecipeType = onToggleRecipeType,
                            uiState = generatorUiState,
                            onGenerateClick = onGenerateClick,
                            onGoToPlan = {
                                onGoToPlan()
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    TAB_MY_RECIPES -> {
                        MyRecipesScreen(
                            modifier = Modifier.fillMaxSize(),
                            embeddedMode = true
                        )
                    }
                }
            }
        }

        // ── Bottom Nav Bar ────────────────────────────────────────
        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
            EditorialBottomNavBar(
                selectedItem = selectedNavItem,
                onItemSelected = onNavItemSelected
            )
        }
    }
}

// ── Inner tab switcher ───────────────────────────────────────────────

@Composable
private fun InnerTabSwitcher(activeTab: Int, onTabSelected: (Int) -> Unit) {
    val tabs = listOf(
        Pair("✨ Generador", TAB_GENERATOR),
        Pair("📖 Mis Recetas", TAB_MY_RECIPES)
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(rounded_full))
            .background(SurfaceContainerLow)
            .padding(spacing_2)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing_2)
        ) {
            tabs.forEach { (label, index) ->
                val isActive = activeTab == index
                val bgColor by animateColorAsState(
                    targetValue = if (isActive) Primary else Color.Transparent,
                    animationSpec = tween(250),
                    label = "tabBg_$index"
                )
                val textColor by animateColorAsState(
                    targetValue = if (isActive) Color.White else OnSurfaceVariant,
                    animationSpec = tween(250),
                    label = "tabText_$index"
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(rounded_lg))
                        .background(bgColor)
                        .clickable { onTabSelected(index) }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        fontSize = 13.sp,
                        fontWeight = if (isActive) FontWeight.ExtraBold else FontWeight.Medium,
                        color = textColor
                    )
                }
            }
        }
    }
}
