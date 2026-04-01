package com.example.recipe_generator.data.repository

import com.example.recipe_generator.domain.model.UserPreferences
import com.example.recipe_generator.domain.repository.UserPrefsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * Implementación del repositorio de preferencias — Capa de Datos.
 *
 * Usa DataStore Preferences para persistencia liviana.
 * Se completa en F3-30.
 *
 * Capa: Data
 */
class UserPrefsRepositoryImpl(
    // private val dataStore: DataStore<Preferences>  ← se inyecta en F3-30
) : UserPrefsRepository {

    override fun getUserPreferences(): Flow<UserPreferences> = flowOf(UserPreferences())

    override suspend fun saveTheme(theme: String) { /* F3-30 */ }

    override suspend fun saveLanguage(language: String) { /* F3-30 */ }

    override suspend fun saveDefaultPortions(portions: Int) { /* F3-30 */ }

    override suspend fun saveSelectedDiets(diets: Set<String>) { /* F3-30 */ }
}
