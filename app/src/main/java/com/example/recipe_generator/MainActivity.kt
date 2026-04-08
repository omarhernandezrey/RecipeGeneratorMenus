@file:Suppress("SpellCheckingInspection")

package com.example.recipe_generator

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import android.util.Log
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

        val isCheckingAuth by authViewModel.isCheckingAuth.collectAsState()
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

        // E-04: sincronizar Firestore → Room al detectar usuario autenticado
        val userId = currentUser?.uid
        LaunchedEffect(userId) {
            if (userId != null) {
                val container = (application as RecipeGeneratorApp).container
                runCatching {
                    container.firestoreSyncService.syncOnLogin(userId)
                }.onFailure { e ->
                    Log.w("MainActivity", "syncOnLogin falló: ${e.message}")
                }
            }
        }

        val handleLogout: () -> Unit = {
            authViewModel.logout()
        }

        when {
            // ── Verificando sesión → pantalla de carga ───────────────────
            // Evita el "flash" de AuthScreen cuando el usuario ya tiene sesión activa
            isCheckingAuth -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }

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
                    weeklyPlanRepository = container.weeklyPlanRepository,
                    userId = container.requireAuthenticatedUserId(),
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
