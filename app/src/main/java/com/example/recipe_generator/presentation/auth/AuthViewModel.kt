@file:Suppress("SpellCheckingInspection")

package com.example.recipe_generator.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipe_generator.domain.model.User
import com.example.recipe_generator.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onEach
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

    // true mientras Firebase no ha respondido aún (evita flash de AuthScreen al abrir la app)
    private val _isCheckingAuth = MutableStateFlow(true)
    val isCheckingAuth: StateFlow<Boolean> = _isCheckingAuth.asStateFlow()

    // Estado del usuario autenticado actual.
    // SharingStarted.Eagerly inicia el listener de Firebase inmediatamente al crear el ViewModel,
    // onEach apaga el flag de "verificando" tras la primera respuesta real de Firebase.
    val currentUser: StateFlow<User?> = authRepository.getCurrentUser()
        .onEach { _isCheckingAuth.value = false }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = null
        )

    // Indicador de loading
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Mensaje de error
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Mensaje de éxito (recuperación de contraseña, etc.)
    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    /**
     * Registra nuevo usuario con nombre de display (B-02)
     */
    fun signUp(email: String, password: String, displayName: String = "") {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val result = authRepository.signUp(email, password)

            result.onSuccess { user ->
                if (displayName.isNotBlank()) {
                    authRepository.updateUserProfile(displayName, null)
                }
                _isLoading.value = false
            }.onFailure { error ->
                _errorMessage.value = error.message ?: "Error al registrar"
                _isLoading.value = false
            }
        }
    }

    /**
     * Inicia sesión con Google usando idToken de Credential Manager (B-07)
     * El idToken lo obtiene AuthScreen con Credential Manager y lo pasa aquí
     */
    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            authRepository.signInWithGoogle(idToken)
                .onFailure { error ->
                    _errorMessage.value = error.message ?: "Error al iniciar sesión con Google"
                }
            _isLoading.value = false
        }
    }

    /**
     * Envía correo de recuperación de contraseña (B-09)
     */
    fun sendPasswordReset(email: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _successMessage.value = null
            val result = authRepository.sendPasswordReset(email)
            result.onSuccess {
                _successMessage.value = "Enlace enviado a $email.\nRevisa tu bandeja de entrada (y spam)."
            }.onFailure { error ->
                _errorMessage.value = error.message ?: "Error al enviar correo"
            }
            _isLoading.value = false
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

    /**
     * Limpia el mensaje de éxito
     */
    fun clearSuccess() {
        _successMessage.value = null
    }

    /**
     * Establece un mensaje de error desde la UI (ej. Credential Manager)
     */
    fun setError(message: String) {
        _errorMessage.value = message
        _isLoading.value = false
    }
}
