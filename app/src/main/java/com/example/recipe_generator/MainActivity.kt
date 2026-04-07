@file:Suppress("SpellCheckingInspection")

package com.example.recipe_generator

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.recipe_generator.presentation.auth.AuthScreen
import com.example.recipe_generator.presentation.auth.AuthViewModel
import com.example.recipe_generator.presentation.home.AppShell
import com.example.recipe_generator.presentation.home.AuthWelcomeScreen
import com.example.recipe_generator.presentation.theme.RecipeGeneratorTheme

/**
 * Single host Activity — gestiona el flujo de autenticación y la app principal.
 *
 * Flujo de navegación:
 *
 *   Al abrir la app:
 *     • Sesión activa (Firebase ya tenía usuario) → AppShell directo
 *     • Sin sesión → AuthScreen
 *
 *   Al hacer login/registro/Google en AuthScreen:
 *     → AuthWelcomeScreen (bienvenida única por sesión)
 *     → AppShell
 *
 *   Al cerrar sesión desde AppShell:
 *     → AuthScreen
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RecipeGeneratorTheme {
                AppContent()
            }
        }
    }

    @Composable
    private fun AppContent() {
        val authViewModel: AuthViewModel = viewModel(
            factory = AuthViewModelFactory(
                authRepository = (application as RecipeGeneratorApp).container.authRepository
            )
        )

        val currentUser by authViewModel.currentUser.collectAsState()

        // true  → el usuario ya tenía sesión activa al abrir la app (no mostrar welcome)
        // false → el usuario acaba de hacer login en esta sesión (mostrar welcome)
        var wasAlreadyLoggedIn by remember { mutableStateOf<Boolean?>(null) }

        // Controla si ya pasó por AuthWelcomeScreen en esta sesión
        var welcomeSeen by remember { mutableStateOf(false) }

        LaunchedEffect(currentUser) {
            when {
                currentUser == null -> {
                    wasAlreadyLoggedIn = null
                    welcomeSeen = false
                }
                wasAlreadyLoggedIn == null -> {
                    // Sesión pre-existente: saltar AuthWelcomeScreen
                    wasAlreadyLoggedIn = true
                }
            }
        }

        val handleLogout: () -> Unit = {
            authViewModel.logout()
        }

        when {
            // ── Sin usuario autenticado → pantalla de login ──────────────
            currentUser == null -> {
                AuthScreen(
                    viewModel = authViewModel,
                    onAuthSuccess = {
                        // Login fresco: marcar que NO tenía sesión previa → mostrar welcome
                        wasAlreadyLoggedIn = false
                    }
                )
            }

            // ── Usuario recién autenticado en esta sesión → bienvenida ───
            wasAlreadyLoggedIn == false && !welcomeSeen -> {
                AuthWelcomeScreen(
                    userEmail = currentUser?.email ?: "",
                    onContinueToApp = { },
                    onLogout = handleLogout
                )
            }

            // ── Usuario autenticado (sesión activa o ya vio welcome) → App ──
            else -> {
                val container = (application as RecipeGeneratorApp).container
                AppShell(
                    getMenuForDayUseCase = container.getMenuForDayUseCase,
                    favoritesRepository = container.favoritesRepository,
                    generateMenuUseCase = container.generateMenuUseCase,
                    userPrefsRepository = container.userPrefsRepository,
                    onLogout = handleLogout
                )
            }
        }
    }
}

// Factory para crear AuthViewModel con sus dependencias
class AuthViewModelFactory(
    private val authRepository: com.example.recipe_generator.domain.repository.AuthRepository
) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
