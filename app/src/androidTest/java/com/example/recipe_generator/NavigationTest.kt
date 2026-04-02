package com.example.recipe_generator

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * F3-38: Test instrumentación Compose — navegación entre pantallas.
 *
 * Verifica que la navegación entre destinos del bottom nav funciona correctamente.
 * Usa ComposeTestRule + AndroidJUnit4.
 *
 * Nota: Los tests de instrumentación requieren un emulador o dispositivo físico.
 */
@RunWith(AndroidJUnit4::class)
class NavigationTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun app_launches_showing_home_screen() {
        // La pantalla de inicio debe mostrar "Menú Semanal"
        composeTestRule.onNodeWithText("Menú Semanal").assertIsDisplayed()
    }

    @Test
    fun bottom_nav_navigates_to_favorites() {
        // Navegar a Favoritos
        composeTestRule.onNodeWithText("Favoritos").performClick()
        // La pantalla de Favoritos debe mostrarse (buscar en favoritos)
        composeTestRule.onNodeWithText("Buscar en favoritos...").assertIsDisplayed()
    }

    @Test
    fun bottom_nav_navigates_to_generator() {
        // Navegar al Generador
        composeTestRule.onNodeWithText("Generador").performClick()
        // El generador muestra "Preferencias Dietéticas"
        composeTestRule.onNodeWithText("Preferencias Dietéticas").assertIsDisplayed()
    }

    @Test
    fun bottom_nav_navigates_to_settings() {
        // Navegar a Ajustes
        composeTestRule.onNodeWithText("Ajustes").performClick()
        // Los ajustes muestran la sección de preferencias
        composeTestRule.onNodeWithText("Preferencias de Cocina").assertIsDisplayed()
    }

    @Test
    fun day_tabs_work_on_home_screen() {
        // La pantalla de inicio debe mostrar los tabs de días
        composeTestRule.onNodeWithText("Lunes").assertIsDisplayed()
    }
}
