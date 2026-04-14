package com.example.recipe_generator.presentation.unified

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.recipe_generator.presentation.components.EditorialBottomNavBar
import com.example.recipe_generator.presentation.components.editorialBottomBarContentPadding
import com.example.recipe_generator.presentation.generator.GeneratorUiState
import com.example.recipe_generator.presentation.myrecipes.MyRecipesScreen
import com.example.recipe_generator.presentation.theme.*

/**
 * Pantalla de Cocina Personal — LIMPIA.
 * Se eliminó el generador aleatorio por petición del usuario.
 * Ahora muestra directamente la biblioteca de recetas personalizadas.
 */
@Composable
fun UnifiedRecipesScreen(
    selectedNavItem: Int = 3,
    onNavItemSelected: (Int) -> Unit = {},
    // Parámetros obsoletos que se mantienen para no romper AppShell por ahora
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
    val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val bottomPad = editorialBottomBarContentPadding()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Surface)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // ── Header Editorial ──────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Surface)
                    .padding(top = statusBarPadding + spacing_3)
                    .padding(horizontal = spacing_6)
                    .padding(bottom = spacing_4)
            ) {
                Text(
                    text = "Mis Creaciones",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = OnSurface
                )
                Text(
                    text = "Gestiona tus recetas y usa el asistente IA",
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurfaceVariant.copy(alpha = 0.7f),
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            // ── Contenido: Directo a Mis Recetas ──────────────────
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = bottomPad)
            ) {
                MyRecipesScreen(
                    modifier = Modifier.fillMaxSize(),
                    embeddedMode = true
                )
            }
        }

        // ── Navegación ────────────────────────────────────────────
        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
            EditorialBottomNavBar(
                selectedItem = selectedNavItem,
                onItemSelected = onNavItemSelected
            )
        }
    }
}
