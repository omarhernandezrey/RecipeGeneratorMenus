@file:Suppress("SpellCheckingInspection")

package com.example.recipe_generator

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.recipe_generator.data.connectivity.NetworkConnectivityObserver
import com.example.recipe_generator.domain.model.UserPreferences
import com.example.recipe_generator.presentation.auth.AuthScreen
import com.example.recipe_generator.presentation.auth.AuthViewModel
import com.example.recipe_generator.presentation.home.AppShell
import com.example.recipe_generator.presentation.home.AuthWelcomeScreen
import com.example.recipe_generator.presentation.theme.RecipeGeneratorTheme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppContent()
        }
    }

    @Composable
    private fun AppContent() {
        val container = (application as RecipeGeneratorApp).container
        val prefs by container.userPrefsRepository.getUserPreferences()
            .collectAsState(initial = UserPreferences())
        val darkTheme = prefs.theme == "Oscuro"

        RecipeGeneratorTheme(darkTheme = darkTheme) {
            AppContentInner()
        }
    }

    @Composable
    private fun AppContentInner() {
        val container = (application as RecipeGeneratorApp).container
        
        // Permisos de notificaciones
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { }
            LaunchedEffect(Unit) { launcher.launch(Manifest.permission.POST_NOTIFICATIONS) }
        }

        val authViewModel: AuthViewModel = viewModel(
            factory = AuthViewModelFactory(container.authRepository)
        )

        val isCheckingAuth by authViewModel.isCheckingAuth.collectAsState()
        val currentUser by authViewModel.currentUser.collectAsState()

        // Lógica de bienvenida estable
        var wasAlreadyLoggedIn by remember { mutableStateOf<Boolean?>(null) }
        var welcomeSeen by remember { mutableStateOf(false) }
        val coroutineScope = rememberCoroutineScope()

        // Detectar si el usuario ya estaba logueado al arrancar
        LaunchedEffect(currentUser) {
            if (currentUser != null && wasAlreadyLoggedIn == null) {
                wasAlreadyLoggedIn = true
            } else if (currentUser == null) {
                wasAlreadyLoggedIn = null
                welcomeSeen = false
            }
        }

        // Sincronización al entrar
        val userId = currentUser?.uid
        LaunchedEffect(userId) {
            if (userId != null) {
                container.setSyncing(true)
                runCatching { container.firestoreSyncService.syncOnLogin(userId) }
                container.setSyncing(false)
            }
        }

        // Observador de conectividad
        val connectivityObserver = remember { NetworkConnectivityObserver(applicationContext) }
        LaunchedEffect(userId) {
            if (userId == null) return@LaunchedEffect
            var wasOffline = false
            connectivityObserver.observe().collect { isConnected ->
                if (!isConnected) wasOffline = true
                else if (wasOffline) {
                    wasOffline = false
                    container.setSyncing(true)
                    runCatching { container.firestoreSyncService.syncPendingRecipes(userId) }
                    container.setSyncing(false)
                }
            }
        }

        val handleLogout: () -> Unit = {
            val uid = container.authRepository.getCurrentUserId()
            if (uid != null) {
                coroutineScope.launch { runCatching { container.clearLocalUserData(uid) } }
            }
            authViewModel.logout()
        }

        when {
            isCheckingAuth -> {
                LoadingBox()
            }

            currentUser == null -> {
                AuthScreen(
                    viewModel = authViewModel,
                    onAuthSuccess = { wasAlreadyLoggedIn = false }
                )
            }

            wasAlreadyLoggedIn == false && !welcomeSeen -> {
                AuthWelcomeScreen(
                    userEmail = currentUser?.email ?: "",
                    onContinueToApp = { welcomeSeen = true },
                    onLogout = handleLogout
                )
            }

            else -> {
                AppShell(
                    getMenuForDayUseCase = container.getMenuForDayUseCase,
                    getRecipeDetailUseCase = container.getRecipeDetailUseCase,
                    favoritesRepository = container.favoritesRepository,
                    generateMenuUseCase = container.generateMenuUseCase,
                    userPrefsRepository = container.userPrefsRepository,
                    weeklyPlanRepository = container.weeklyPlanRepository,
                    isSyncing = container.isSyncing,
                    userId = container.requireAuthenticatedUserId(),
                    onLogout = handleLogout
                )
            }
        }
    }

    @Composable
    private fun LoadingBox() {
        Box(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}

class AuthViewModelFactory(
    private val authRepository: com.example.recipe_generator.domain.repository.AuthRepository
) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
