package com.example.recipe_generator.domain.model

/**
 * Modelo de dominio — UserRecipe.
 *
 * Representa una receta creada o personalizada por el usuario autenticado.
 * Es un POJO puro: sin anotaciones de Room, sin dependencias de Android.
 *
 * A diferencia de Recipe (catálogo general), UserRecipe pertenece a un
 * usuario específico (userId = Firebase UID) y puede sincronizarse con
 * Firestore (isSynced).
 *
 * Los ingredientes y pasos se almacenan como listas de Strings simples
 * (la capa de datos serializa/deserializa desde JSON en la entidad Room).
 *
 * Capa: Domain
 */
data class UserRecipe(
    val id: String,

    /** Firebase UID del propietario */
    val userId: String,

    val title: String,

    /** Nombre del drawable local o cadena vacía si no tiene imagen */
    val imageRes: String = "",

    /** URL remota persistida tras búsqueda exitosa (F-Images) */
    val imageUrl: String? = null,

    val timeInMinutes: Int = 0,

    val calories: Int = 0,

    /** "Fácil" | "Medio" | "Difícil" */
    val difficulty: String = "Fácil",

    /** "Desayuno" | "Almuerzo" | "Cena" | "Snack" */
    val category: String = "",

    val description: String = "",

    /** "Lunes" | "Martes" | … | "Domingo" — día asignado en el plan semanal */
    val dayOfWeek: String = "",

    /** "Desayuno" | "Almuerzo" | "Cena" */
    val mealType: String = "",

    val videoYoutube: String? = null,

    /** Lista de ingredientes. Ej: ["200g harina", "2 huevos"] */
    val ingredients: List<String> = emptyList(),

    /** Lista de pasos de preparación. Ej: ["Mezclar harina", "Hornear 30 min"] */
    val steps: List<String> = emptyList(),

    /** false = pendiente de sync con Firestore; true = sincronizado */
    val isSynced: Boolean = false,

    val createdAt: Long = System.currentTimeMillis(),

    val updatedAt: Long = System.currentTimeMillis()
)
