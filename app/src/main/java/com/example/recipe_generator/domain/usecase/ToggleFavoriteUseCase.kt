package com.example.recipe_generator.domain.usecase

import com.example.recipe_generator.domain.repository.FavoritesRepository

/**
 * Caso de uso — Alternar estado favorito de una receta.
 *
 * Si la receta es favorita la quita; si no lo es la agrega.
 * La lógica de alternancia vive aquí (dominio), no en el ViewModel.
 *
 * Capa: Domain
 */
class ToggleFavoriteUseCase(
    private val favoritesRepository: FavoritesRepository
) {
    /**
     * Ejecuta el caso de uso.
     * @param userId UID del usuario autenticado.
     * @param recipeId ID de la receta a alternar.
     */
    suspend operator fun invoke(userId: String, recipeId: String) {
        favoritesRepository.toggleFavorite(userId, recipeId)
    }
}
