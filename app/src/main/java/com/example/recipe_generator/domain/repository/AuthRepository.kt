package com.example.recipe_generator.domain.repository

import com.example.recipe_generator.domain.model.User
import kotlinx.coroutines.flow.Flow

/**
 * Repository Interface para autenticación de usuarios
 * - Separación de la lógica de negocio (domain) de la implementación (Firebase)
 * - Permite testing y cambios de backend en el futuro
 */
interface AuthRepository {

    /**
     * Retorna Flow del usuario autenticado actual (null si no está logueado)
     */
    fun getCurrentUser(): Flow<User?>

    /**
     * Registra nuevo usuario con email y contraseña
     */
    suspend fun signUp(email: String, password: String): Result<User>

    /**
     * Inicia sesión con email y contraseña
     */
    suspend fun signIn(email: String, password: String): Result<User>

    /**
     * Cierra sesión del usuario actual
     */
    suspend fun logout(): Result<Unit>

    /**
     * Actualiza el perfil del usuario
     */
    suspend fun updateUserProfile(displayName: String?, photoUrl: String?): Result<User>

    /**
     * Envía correo de recuperación de contraseña (B-09)
     */
    suspend fun sendPasswordReset(email: String): Result<Unit>

    /**
     * Inicia sesión con Google usando el idToken de Credential Manager (B-05)
     */
    suspend fun signInWithGoogle(idToken: String): Result<User>
}

