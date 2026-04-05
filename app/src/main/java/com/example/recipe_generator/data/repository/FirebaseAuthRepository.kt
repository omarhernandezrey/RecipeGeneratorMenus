package com.example.recipe_generator.data.repository

import com.example.recipe_generator.domain.model.User
import com.example.recipe_generator.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
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

    /**
     * Emite el usuario autenticado actual. Observa cambios en tiempo real
     */
    override fun getCurrentUser(): Flow<User?> = callbackFlow {
        try {
            val authStateListener = FirebaseAuth.AuthStateListener { auth ->
                try {
                    val currentUser = auth.currentUser
                    val user = if (currentUser != null) {
                        User(
                            uid = currentUser.uid,
                            email = currentUser.email ?: "",
                            displayName = currentUser.displayName,
                            photoUrl = currentUser.photoUrl?.toString()
                        )
                    } else {
                        null
                    }
                    trySend(user)
                } catch (e: Exception) {
                    // Si hay error al procesar el usuario, envía null
                    trySend(null)
                }
            }

            firebaseAuth.addAuthStateListener(authStateListener)

            awaitClose {
                try {
                    firebaseAuth.removeAuthStateListener(authStateListener)
                } catch (e: Exception) {
                    // Ignorar errores al remover listener
                }
            }
        } catch (e: Exception) {
            // Si hay error al crear listener, emite null
            trySend(null)
            awaitClose()
        }
    }

    /**
     * Registra nuevo usuario en Firebase
     */
    override suspend fun signUp(email: String, password: String): Result<User> = try {
        val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        val user = result.user ?: throw Exception("Usuario no creado")
        Result.success(
            User(
                uid = user.uid,
                email = user.email ?: "",
                displayName = user.displayName,
                photoUrl = user.photoUrl?.toString()
            )
        )
    } catch (e: Exception) {
        // Log de error para debugging
        android.util.Log.e("FirebaseAuth", "Error en signUp: ${e.message}", e)
        Result.failure(e)
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
    } catch (e: Exception) {
        android.util.Log.e("FirebaseAuth", "Error en signIn: ${e.message}", e)
        Result.failure(e)
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

