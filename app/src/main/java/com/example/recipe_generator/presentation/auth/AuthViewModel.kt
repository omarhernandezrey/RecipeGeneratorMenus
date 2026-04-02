package com.example.recipe_generator.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipe_generator.domain.model.User
import com.example.recipe_generator.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel para pantalla de autenticación (Login/Registro)
 * - Maneja el estado del usuario actual
 * - Maneja errores de autenticación
 * - Realiza operaciones de signUp, signIn, logout
 */
class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    // Estado del usuario autenticado actual
    val currentUser: StateFlow<User?> = authRepository.getCurrentUser()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = null
        )

    // Indicador de loading
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Mensaje de error
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    /**
     * Registra nuevo usuario
     */
    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val result = authRepository.signUp(email, password)

            result.onSuccess {
                _isLoading.value = false
            }.onFailure { error ->
                _errorMessage.value = error.message ?: "Error al registrar"
                _isLoading.value = false
            }
        }
    }

    /**
     * Inicia sesión
     */
    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val result = authRepository.signIn(email, password)

            result.onSuccess {
                _isLoading.value = false
            }.onFailure { error ->
                _errorMessage.value = error.message ?: "Error al iniciar sesión"
                _isLoading.value = false
            }
        }
    }

    /**
     * Cierra sesión
     */
    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }

    /**
     * Limpia el mensaje de error
     */
    fun clearError() {
        _errorMessage.value = null
    }
}

