package com.example.recipe_generator.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad Room — UserProfileEntity.
 *
 * Tabla "user_profile": perfil del usuario autenticado cacheado localmente.
 * La clave primaria es el Firebase UID — hay exactamente un perfil por usuario.
 *
 * Estrategia offline-first:
 *  - Al iniciar sesión: Firestore → Room (caché local).
 *  - Al editar perfil: Room + Firestore (fuente de verdad en la nube).
 *
 * preferredDietsJson almacena la lista de dietas seleccionadas serializada en JSON.
 * Ej: ["Vegetariano", "Sin gluten"]
 *
 * C-03
 * Capa: Data
 */
@Entity(tableName = "user_profile")
data class UserProfileEntity(

    /** Firebase UID — clave primaria única por usuario */
    @PrimaryKey
    val uid: String,

    /** Nombre visible del usuario (Firebase displayName) */
    val displayName: String = "",

    /** Correo electrónico del usuario */
    val email: String = "",

    /** URL de la foto de perfil (Firebase Storage o null si no tiene) */
    val photoUrl: String? = null,

    /** Dietas preferidas serializadas en JSON. Ej: ["Vegetariano","Sin gluten"] */
    val preferredDietsJson: String = "[]",

    /** Número de porciones por defecto para el generador de menús (1–10) */
    val defaultPortions: Int = 2,

    /** Epoch millis — fecha de creación de la cuenta */
    val createdAt: Long = System.currentTimeMillis()
)
