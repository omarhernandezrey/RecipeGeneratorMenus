package com.example.recipe_generator.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.recipe_generator.domain.model.UserPreferences
import com.example.recipe_generator.domain.repository.UserPrefsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val USER_PREFERENCES_DATASTORE_NAME = "user_preferences"
private val Context.userPreferencesDataStore by preferencesDataStore(name = USER_PREFERENCES_DATASTORE_NAME)
private val themeKey = stringPreferencesKey("theme")
private val languageKey = stringPreferencesKey("language")
private val defaultPortionsKey = intPreferencesKey("default_portions")
private val selectedDietsKey = stringPreferencesKey("selected_diets")

/**
 * Implementación del repositorio de preferencias — Capa de Datos.
 *
 * Usa DataStore Preferences para persistencia liviana.
 * Se completa en F3-30.
 *
 * Capa: Data
 */
class UserPrefsRepositoryImpl(
    private val context: Context
) : UserPrefsRepository {

    override fun getUserPreferences(): Flow<UserPreferences> =
        context.userPreferencesDataStore.data.map { preferences ->
            UserPreferences(
                theme = preferences[themeKey] ?: "Claro",
                language = preferences[languageKey] ?: "Español",
                defaultPortions = preferences[defaultPortionsKey] ?: 2,
                selectedDiets = preferences[selectedDietsKey]
                    ?.split("|")
                    ?.filter { it.isNotBlank() }
                    ?.toSet()
                    ?: emptySet()
            )
        }

    override suspend fun saveTheme(theme: String) {
        context.userPreferencesDataStore.edit { preferences ->
            preferences[themeKey] = theme
        }
    }

    override suspend fun saveLanguage(language: String) {
        context.userPreferencesDataStore.edit { preferences ->
            preferences[languageKey] = language
        }
    }

    override suspend fun saveDefaultPortions(portions: Int) {
        context.userPreferencesDataStore.edit { preferences ->
            preferences[defaultPortionsKey] = portions
        }
    }

    override suspend fun saveSelectedDiets(diets: Set<String>) {
        context.userPreferencesDataStore.edit { preferences ->
            preferences[selectedDietsKey] = diets.joinToString("|")
        }
    }
}
