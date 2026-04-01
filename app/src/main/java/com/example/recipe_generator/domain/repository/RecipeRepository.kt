package com.example.recipe_generator.domain.repository

import com.example.recipe_generator.domain.model.Recipe
import kotlinx.coroutines.flow.Flow

/**
 * Interfaz del repositorio de recetas — Capa de Dominio.
 *
 * Define el contrato que la capa de Datos debe cumplir.
 * La capa de Presentación NUNCA depende de la implementación concreta (Room),
 * solo de esta interfaz. Principio D de SOLID.
 *
 * Capa: Domain
 */
interface RecipeRepository {

    /** Todas las recetas como stream reactivo. */
    fun getAllRecipes(): Flow<List<Recipe>>

    /** Recetas de un día específico de la semana. */
    fun getRecipesByDay(day: String): Flow<List<Recipe>>

    /** Recetas por categoría: "Desayuno" | "Almuerzo" | "Cena". */
    fun getRecipesByCategory(category: String): Flow<List<Recipe>>

    /** Receta específica por ID. */
    fun getRecipeById(id: String): Flow<Recipe?>

    /**
     * Búsqueda por texto libre.
     * Busca en título y descripción (LIKE %query%).
     */
    fun searchRecipes(query: String): Flow<List<Recipe>>

    /** Inserta o reemplaza todas las recetas (usado por DatabaseSeeder). */
    suspend fun insertAll(recipes: List<Recipe>)

    /** Cuenta total de recetas en la base de datos. */
    suspend fun count(): Int
}
