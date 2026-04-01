package com.example.recipe_generator.data.legacy

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val FAVORITES_DATASTORE_NAME = "favorites_preferences"
private val Context.favoritesDataStore by preferencesDataStore(name = FAVORITES_DATASTORE_NAME)

class LegacyFavoritesRepository(private val context: Context) {
    private val favoriteRecipeIdsKey = stringSetPreferencesKey("favorite_recipe_ids")

    val favoriteRecipeIds: Flow<Set<String>> = context.favoritesDataStore.data.map { preferences ->
        preferences[favoriteRecipeIdsKey] ?: emptySet()
    }

    suspend fun toggleFavorite(recipeId: String) {
        context.favoritesDataStore.edit { preferences ->
            val currentIds = preferences[favoriteRecipeIdsKey].orEmpty().toMutableSet()
            if (!currentIds.add(recipeId)) {
                currentIds.remove(recipeId)
            }
            preferences[favoriteRecipeIdsKey] = currentIds
        }
    }

    suspend fun removeFavorite(recipeId: String) {
        context.favoritesDataStore.edit { preferences ->
            val currentIds = preferences[favoriteRecipeIdsKey].orEmpty().toMutableSet()
            currentIds.remove(recipeId)
            preferences[favoriteRecipeIdsKey] = currentIds
        }
    }
}
