package com.example.recipe_generator.data.repository

import com.example.recipe_generator.domain.model.User
import com.example.recipe_generator.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.userProfileChangeRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Implementación de AuthRepository usando Firebase Authentication
 * - Maneja registro, login, logout y gestión de perfil
 * - Emite cambios de autenticación como Flow
 */
class FirebaseAuthRepository(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {

    override fun getCurrentUserId(): String? = firebaseAuth.currentUser?.uid

    /**
     * Emite el usuario autenticado actual. Observa cambios en tiempo real
     */
    override fun getCurrentUser(): Flow<User?> = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { auth ->
            val user = runCatching {
                auth.currentUser?.let { currentUser ->
                    User(
                        uid = currentUser.uid,
                        email = currentUser.email ?: "",
                        displayName = currentUser.displayName,
                        photoUrl = currentUser.photoUrl?.toString()
                    )
                }
            }.getOrNull()

            trySend(user)
        }

        val registrationResult = runCatching {
            firebaseAuth.addAuthStateListener(authStateListener)
        }

        if (registrationResult.isFailure) {
            trySend(null)
            close(registrationResult.exceptionOrNull())
            return@callbackFlow
        }

        awaitClose {
            runCatching {
                firebaseAuth.removeAuthStateListener(authStateListener)
            }
        }
    }

    /**
     * Registra nuevo usuario en Firebase
     */
    override suspend fun signUp(email: String, password: String): Result<User> = try {
        val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        val user = result.user ?: throw Exception("Usuario no creado")
        // Enviar verificación de correo automáticamente (no bloquea el registro si falla)
        try { user.sendEmailVerification().await() } catch (e: Exception) {
            android.util.Log.w("FirebaseAuth", "No se pudo enviar verificación: ${e.message}")
        }
        Result.success(
            User(
                uid = user.uid,
                email = user.email ?: "",
                displayName = user.displayName,
                photoUrl = user.photoUrl?.toString()
            )
        )
    } catch (e: FirebaseAuthUserCollisionException) {
        Result.failure(Exception("Ya existe una cuenta con este correo. Intenta iniciar sesión."))
    } catch (e: FirebaseAuthWeakPasswordException) {
        Result.failure(Exception("Contraseña muy débil. Usa al menos 6 caracteres."))
    } catch (e: FirebaseAuthInvalidCredentialsException) {
        Result.failure(Exception("El formato del correo electrónico no es válido."))
    } catch (e: Exception) {
        val msg = when {
            e.message?.contains("EMAIL_ALREADY_IN_USE") == true ->
                "Ya existe una cuenta con este correo."
            e.message?.contains("NETWORK_REQUEST_FAILED") == true ->
                "Sin conexión a internet. Verifica tu red."
            else -> {
                android.util.Log.e("FirebaseAuth", "Error en signUp: ${e.message}", e)
                e.message ?: "Error al registrar"
            }
        }
        Result.failure(Exception(msg))
    }

    /**
     * Inicia sesión con email y contraseña
     */
    override suspend fun signIn(email: String, password: String): Result<User> = try {
        val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
        val user = result.user ?: throw Exception("No se pudo obtener usuario")
        Result.success(
            User(
                uid = user.uid,
                email = user.email ?: "",
                displayName = user.displayName,
                photoUrl = user.photoUrl?.toString()
            )
        )
    } catch (e: FirebaseAuthInvalidUserException) {
        Result.failure(Exception("No existe cuenta con este correo. Verifica o regístrate."))
    } catch (e: FirebaseAuthInvalidCredentialsException) {
        val msg = when {
            e.message?.contains("INVALID_LOGIN_CREDENTIALS") == true ||
            e.message?.contains("INVALID_PASSWORD") == true ||
            e.message?.contains("WRONG_PASSWORD") == true ->
                "Contraseña incorrecta. Revísala e intenta de nuevo."
            else -> "Credenciales inválidas. Verifica tu correo y contraseña."
        }
        Result.failure(Exception(msg))
    } catch (e: Exception) {
        val msg = when {
            e.message?.contains("INVALID_LOGIN_CREDENTIALS") == true ->
                "Correo o contraseña incorrectos."
            e.message?.contains("USER_NOT_FOUND") == true ->
                "No existe cuenta con este correo. Verifica o regístrate."
            e.message?.contains("TOO_MANY_ATTEMPTS") == true ||
            e.message?.contains("TOO_MANY_REQUESTS") == true ->
                "Demasiados intentos fallidos. Espera unos minutos antes de reintentar."
            e.message?.contains("USER_DISABLED") == true ->
                "Esta cuenta ha sido desactivada. Contacta soporte."
            e.message?.contains("NETWORK_REQUEST_FAILED") == true ->
                "Sin conexión a internet. Verifica tu red."
            else -> {
                android.util.Log.e("FirebaseAuth", "Error en signIn: ${e.message}", e)
                e.message ?: "Error al iniciar sesión"
            }
        }
        Result.failure(Exception(msg))
    }

    /**
     * Cierra sesión
     */
    override suspend fun logout(): Result<Unit> = try {
        firebaseAuth.signOut()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    /**
     * Actualiza el perfil del usuario (nombre y foto)
     */
    override suspend fun updateUserProfile(displayName: String?, photoUrl: String?): Result<User> = try {
        val currentUser = firebaseAuth.currentUser ?: throw Exception("No hay usuario autenticado")

        val profileUpdates = userProfileChangeRequest {
            if (displayName != null) this.displayName = displayName
            if (photoUrl != null) this.photoUri = android.net.Uri.parse(photoUrl)
        }

        currentUser.updateProfile(profileUpdates).await()

        Result.success(
            User(
                uid = currentUser.uid,
                email = currentUser.email ?: "",
                displayName = currentUser.displayName,
                photoUrl = currentUser.photoUrl?.toString()
            )
        )
    } catch (e: Exception) {
        Result.failure(e)
    }

    /**
     * Envía correo de recuperación de contraseña (B-09)
     */
    override suspend fun sendPasswordReset(email: String): Result<Unit> = try {
        firebaseAuth.sendPasswordResetEmail(email).await()
        Result.success(Unit)
    } catch (e: Exception) {
        android.util.Log.e("FirebaseAuth", "Error en sendPasswordReset: ${e.message}", e)
        Result.failure(e)
    }

    /**
     * Inicia sesión con Google usando idToken de Credential Manager (B-06)
     * GoogleAuthProvider.getCredential(idToken) → signInWithCredential()
     */
    override suspend fun signInWithGoogle(idToken: String): Result<User> = try {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val result = firebaseAuth.signInWithCredential(credential).await()
        val user = result.user ?: throw Exception("No se pudo obtener usuario de Google")
        Result.success(
            User(
                uid = user.uid,
                email = user.email ?: "",
                displayName = user.displayName,
                photoUrl = user.photoUrl?.toString()
            )
        )
    } catch (e: Exception) {
        android.util.Log.e("FirebaseAuth", "Error en signInWithGoogle: ${e.message}", e)
        Result.failure(e)
    }
}

