package com.example.recipe_generator.domain.repository

import com.example.recipe_generator.domain.model.UserRecipe
import kotlinx.coroutines.flow.Flow

/**
 * Interfaz del repositorio de recetas del usuario — Capa de Dominio.
 *
 * Define el contrato que la capa de Datos debe cumplir.
 * La capa de Presentación solo depende de esta interfaz, nunca de Room
 * ni de la implementación concreta. Principio D de SOLID.
 *
 * Todas las operaciones reciben userId (Firebase UID) para garantizar
 * aislamiento total de datos entre usuarios.
 *
 * C-08
 * Capa: Domain
 */
interface UserRecipeRepository {

    /**
     * Retorna todas las recetas del usuario como Flow reactivo.
     * Se actualiza automáticamente cuando Room detecta cambios en la tabla.
     */
    fun getMyRecipes(userId: String): Flow<List<UserRecipe>>

    /**
     * Retorna las recetas del usuario asignadas a un día de la semana.
     * Valores válidos: "Lunes" | "Martes" | … | "Domingo"
     */
    fun getRecipesForDay(userId: String, day: String): Flow<List<UserRecipe>>

    /**
     * Busca recetas del usuario cuyo título contenga el texto indicado.
     */
    fun searchRecipes(userId: String, query: String): Flow<List<UserRecipe>>

    /**
     * Agrega una nueva receta del usuario.
     * El ID debe generarse antes de llamar este método (UUID.randomUUID()).
     * isSynced se establece en false — pendiente de subir a Firestore.
     */
    suspend fun addRecipe(recipe: UserRecipe)

    /**
     * Actualiza los datos de una receta existente del usuario.
     * isSynced se restablece a false — pendiente de sincronizar cambios.
     */
    suspend fun updateRecipe(recipe: UserRecipe)

    /**
     * Elimina una receta del usuario por su ID.
     */
    suspend fun deleteRecipe(recipe: UserRecipe)

    /**
     * Elimina todas las recetas del usuario (usado al cerrar sesión).
     */
    suspend fun deleteAllByUser(userId: String)
}
