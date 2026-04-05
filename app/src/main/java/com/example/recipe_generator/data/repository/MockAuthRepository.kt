package com.example.recipe_generator.data.repository

import com.example.recipe_generator.domain.model.User
import com.example.recipe_generator.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Mock de AuthRepository para desarrollo sin Firebase configurado
 * - Simula registro y login
 * - Almacena usuarios en memoria (se pierden al reiniciar la app)
 * - Útil para testing y desarrollo sin Google Cloud Console
 */
class MockAuthRepository : AuthRepository {

    private val currentUserFlow = MutableStateFlow<User?>(null)

    override fun getCurrentUser(): Flow<User?> = currentUserFlow.asStateFlow()

    override suspend fun signUp(email: String, password: String): Result<User> = try {
        if (email.isEmpty() || password.isEmpty()) {
            throw IllegalArgumentException("Email y contraseña son requeridos")
        }
        if (password.length < 6) {
            throw IllegalArgumentException("La contraseña debe tener al menos 6 caracteres")
        }

        val user = User(
            uid = "mock_${System.currentTimeMillis()}",
            email = email,
            displayName = email.split("@")[0]
        )
        currentUserFlow.value = user
        Result.success(user)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun signIn(email: String, password: String): Result<User> = try {
        if (email.isEmpty() || password.isEmpty()) {
            throw IllegalArgumentException("Email y contraseña son requeridos")
        }

        val user = User(
            uid = "mock_${email.hashCode()}",
            email = email,
            displayName = email.split("@")[0]
        )
        currentUserFlow.value = user
        Result.success(user)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun logout(): Result<Unit> = try {
        currentUserFlow.value = null
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun updateUserProfile(displayName: String?, photoUrl: String?): Result<User> = try {
        val user = currentUserFlow.value ?: throw Exception("No hay usuario autenticado")
        val updatedUser = user.copy(
            displayName = displayName ?: user.displayName,
            photoUrl = photoUrl ?: user.photoUrl
        )
        currentUserFlow.value = updatedUser
        Result.success(updatedUser)
    } catch (e: Exception) {
        Result.failure(e)
    }

    // B-09: Mock — simula envío de correo de recuperación
    override suspend fun sendPasswordReset(email: String): Result<Unit> = try {
        if (email.isEmpty()) throw IllegalArgumentException("El correo no puede estar vacío")
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    // B-08: Mock — simula login con Google
    override suspend fun signInWithGoogle(idToken: String): Result<User> = try {
        val user = User(
            uid = "mock_google_${System.currentTimeMillis()}",
            email = "usuario@gmail.com",
            displayName = "Usuario Google"
        )
        currentUserFlow.value = user
        Result.success(user)
    } catch (e: Exception) {
        Result.failure(e)
    }
}

