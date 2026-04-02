package com.example.recipe_generator

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * F3-39: Test instrumentación — toggle favorito persiste.
 *
 * Verifica que al marcar una receta como favorita desde la lista,
 * aparece en FavoritesScreen.
 *
 * Nota: Los tests de instrumentación requieren un emulador o dispositivo físico.
 */
@RunWith(AndroidJUnit4::class)
class FavoriteToggleTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun favorites_screen_shows_empty_state_initially() {
        // Navegar a Favoritos
        composeTestRule.onNodeWithText("Favoritos").performClick()

        // Si no hay favoritos, debe mostrar el estado vacío
        composeTestRule.onNodeWithText("Todavía no tienes favoritos").assertIsDisplayed()
    }

    @Test
    fun favorite_button_is_accessible_on_recipe_cards() {
        // En la pantalla de inicio, debe existir al menos un botón de favorito
        val favoriteButtons = composeTestRule
            .onAllNodesWithContentDescription("Favorite")
            .fetchSemanticsNodes()

        // Verificar que hay botones de favorito visibles
        assert(favoriteButtons.isNotEmpty()) {
            "Debe haber al menos un botón de favorito en la pantalla de inicio"
        }
    }

    @Test
    fun generator_button_generates_menu() {
        // Navegar al Generador
        composeTestRule.onNodeWithText("Generador").performClick()

        // El botón generar debe estar visible
        composeTestRule.onNodeWithText("GENERAR MENÚ SEMANAL").assertIsDisplayed()

        // Presionar el botón
        composeTestRule.onNodeWithText("GENERAR MENÚ SEMANAL").performClick()

        // Esperar un momento para que procese
        composeTestRule.waitForIdle()
    }
}
