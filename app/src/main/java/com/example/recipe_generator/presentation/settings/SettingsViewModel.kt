package com.example.recipe_generator.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipe_generator.domain.model.UserPreferences
import com.example.recipe_generator.domain.repository.UserPrefsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel de la pantalla de ajustes.
 *
 * Lee y escribe preferencias a través de UserPrefsRepository (DataStore).
 * Cambios en tema/idioma se reflejan inmediatamente en la UI.
 *
 * Se completa en F3-35.
 * Capa: Presentation
 */
class SettingsViewModel(
    private val userPrefsRepository: UserPrefsRepository
) : ViewModel() {

    private val _preferences = MutableStateFlow(UserPreferences())
    val preferences: StateFlow<UserPreferences> = _preferences.asStateFlow()

    init {
        viewModelScope.launch {
            userPrefsRepository.getUserPreferences().collect { prefs ->
                _preferences.value = prefs
            }
        }
    }

    fun saveTheme(theme: String) {
        viewModelScope.launch { userPrefsRepository.saveTheme(theme) }
    }

    fun saveLanguage(language: String) {
        viewModelScope.launch { userPrefsRepository.saveLanguage(language) }
    }

    fun saveDefaultPortions(portions: Int) {
        viewModelScope.launch { userPrefsRepository.saveDefaultPortions(portions) }
    }

    fun toggleDiet(diet: String) {
        val current = _preferences.value.selectedDiets
        val updated = if (diet in current) current - diet else current + diet
        viewModelScope.launch { userPrefsRepository.saveSelectedDiets(updated) }
    }
}
