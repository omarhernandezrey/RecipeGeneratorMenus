package com.example.recipe_generator.domain.repository

import com.example.recipe_generator.domain.model.UserPreferences
import kotlinx.coroutines.flow.Flow

/**
 * Interfaz del repositorio de preferencias de usuario — Capa de Dominio.
 *
 * Persistencia liviana a través de DataStore Preferences.
 * Separado de Recipe y Favorites para cumplir principio I de SOLID.
 *
 * Implementación concreta: UserPrefsRepositoryImpl (DataStore).
 *
 * Capa: Domain
 */
interface UserPrefsRepository {

    /** Stream reactivo de todas las preferencias del usuario. */
    fun getUserPreferences(): Flow<UserPreferences>

    /** Guarda el tema seleccionado: "Claro" | "Oscuro" | "Sistema". */
    suspend fun saveTheme(theme: String)

    /** Guarda el idioma seleccionado: "Español" | "Inglés" | "Portugués". */
    suspend fun saveLanguage(language: String)

    /** Guarda el número de porciones por defecto. */
    suspend fun saveDefaultPortions(portions: Int)

    /** Guarda el conjunto de dietas activas del usuario. */
    suspend fun saveSelectedDiets(diets: Set<String>)
}
