package com.example.recipe_generator.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad Room — UserRecipeEntity.
 *
 * Tabla "user_recipes": recetas creadas o personalizadas por el usuario autenticado.
 * Cada fila pertenece a un único usuario (campo userId = Firebase UID).
 *
 * Estrategia offline-first:
 *  - isSynced = false  → cambio local pendiente de subir a Firestore
 *  - isSynced = true   → sincronizado con Firestore
 *
 * ingredientsJson y stepsJson almacenan arrays serializados en JSON
 * (FirestoreSyncService y el mapper se encargan de serializar/deserializar).
 *
 * C-01
 * Capa: Data
 */
@Entity(tableName = "user_recipes")
data class UserRecipeEntity(
    /** UUID generado localmente — mismo ID que el documento Firestore */
    @PrimaryKey
    val id: String,

    /** Firebase UID del propietario — filtra datos entre usuarios */
    val userId: String,

    val title: String,

    /** Nombre del drawable local (ej. "ic_recipe_default") o ruta relativa */
    val imageRes: String = "",

    val timeInMinutes: Int = 0,

    val calories: Int = 0,

    /** "Fácil" | "Medio" | "Difícil" */
    val difficulty: String = "Fácil",

    /** Desayuno | Almuerzo | Cena | Snack */
    val category: String = "",

    val description: String = "",

    /** "Lunes" | "Martes" | … | "Domingo" — día asignado al plan semanal (opcional) */
    val dayOfWeek: String = "",

    /** "Desayuno" | "Almuerzo" | "Cena" */
    val mealType: String = "",

    /** Lista de ingredientes serializada en JSON. Ej: ["200g harina","2 huevos"] */
    val ingredientsJson: String = "[]",

    /** Lista de pasos serializada en JSON. Ej: ["Mezclar harina","Hornear 30 min"] */
    val stepsJson: String = "[]",

    /** false = pendiente de sync con Firestore; true = sincronizado */
    val isSynced: Boolean = false,

    /** Epoch millis — fecha de creación */
    val createdAt: Long = System.currentTimeMillis(),

    /** Epoch millis — fecha de última modificación */
    val updatedAt: Long = System.currentTimeMillis()
)
