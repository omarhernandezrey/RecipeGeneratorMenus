package com.example.recipe_generator

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.recipe_generator.presentation.auth.AuthScreen
import com.example.recipe_generator.presentation.auth.AuthViewModel
import com.example.recipe_generator.presentation.home.AppShell
import com.example.recipe_generator.presentation.home.AuthWelcomeScreen
import com.example.recipe_generator.presentation.theme.RecipeGeneratorTheme

/**
 * Single host activity for the app shell.
 *
 * ACTUALIZADO: Ahora muestra AuthScreen si el usuario no está autenticado
 * - Firebase Authentication
 * - Jetpack Compose como UI principal
 * - Navigation Component con fragment destinations
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
        val hasSkippedWelcome = androidx.compose.runtime.remember { mutableStateOf(false) }

        when {
            currentUser == null -> {
                // Usuario no autenticado - mostrar login
                AuthScreen(
                    viewModel = authViewModel,
                    onAuthSuccess = {
                        hasSkippedWelcome.value = false
                    }
                )
            }
            !hasSkippedWelcome.value -> {
                // Usuario autenticado pero no ha visto pantalla de bienvenida
                AuthWelcomeScreen(
                    userEmail = currentUser?.email ?: "Usuario",
                    onContinueToApp = {
                        hasSkippedWelcome.value = true
                    },
                    onLogout = {
                        authViewModel.logout()
                        hasSkippedWelcome.value = false
                    }
                )
            }
            else -> {
                // Usuario autenticado - mostrar app completa con NavigationBar
                AppShell(
                    onLogout = {
                        authViewModel.logout()
                        hasSkippedWelcome.value = false
                    }
                )
            }
        }
    }
}

// Factory para crear AuthViewModel con dependencias
class AuthViewModelFactory(private val authRepository: com.example.recipe_generator.domain.repository.AuthRepository) :
    androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
