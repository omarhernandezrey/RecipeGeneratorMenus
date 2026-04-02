package com.example.recipe_generator.data.mapper

import com.example.recipe_generator.data.local.entity.IngredientEntity
import com.example.recipe_generator.data.local.entity.RecipeEntity
import com.example.recipe_generator.data.local.entity.StepEntity
import com.example.recipe_generator.domain.model.Ingredient
import com.example.recipe_generator.domain.model.Recipe
import com.example.recipe_generator.domain.model.RecipeStep

/**
 * Mapper — RecipeMapper.
 *
 * Traduce entre Entity (capa de Datos) y Domain Model (capa de Dominio).
 * La capa de Presentación nunca ve Entities.
 * La capa de Dominio nunca ve Entities.
 *
 * Funciones de extensión para mantener el código idiomático en Kotlin.
 *
 * Capa: Data
 */

// ── Entity → Domain Model ─────────────────────────────────────────────

fun RecipeEntity.toDomain(
    ingredients: List<IngredientEntity> = emptyList(),
    steps: List<StepEntity> = emptyList()
): Recipe = Recipe(
    id = id,
    title = title,
    imageRes = imageRes,
    timeInMinutes = timeInMinutes,
    calories = calories,
    difficulty = difficulty,
    category = category,
    categorySubtitle = categorySubtitle,
    description = description,
    isFavorite = isFavorite,
    rating = rating,
    proteinGrams = proteinGrams,
    carbsGrams = carbsGrams,
    fatGrams = fatGrams,
    dayOfWeek = dayOfWeek,
    ingredients = ingredients.map { it.toDomain() },
    ingredientTags = ingredientTags.split("|").filter { it.isNotBlank() },
    steps = steps.map { it.toDomain() }
)

fun IngredientEntity.toDomain(): Ingredient = Ingredient(
    id = id,
    name = name,
    quantity = quantity,
    unit = unit
)

fun StepEntity.toDomain(): RecipeStep = RecipeStep(
    id = id,
    stepNumber = stepNumber,
    title = title,
    description = description
)

// ── Domain Model → Entity ─────────────────────────────────────────────

fun Recipe.toEntity(): RecipeEntity = RecipeEntity(
    id = id,
    title = title,
    imageRes = imageRes,
    timeInMinutes = timeInMinutes,
    calories = calories,
    difficulty = difficulty,
    category = category,
    categorySubtitle = categorySubtitle,
    description = description,
    isFavorite = isFavorite,
    rating = rating,
    proteinGrams = proteinGrams,
    carbsGrams = carbsGrams,
    fatGrams = fatGrams,
    dayOfWeek = dayOfWeek,
    ingredientTags = ingredientTags.joinToString("|")
)

fun Ingredient.toEntity(recipeId: String): IngredientEntity = IngredientEntity(
    id = id,
    recipeId = recipeId,
    name = name,
    quantity = quantity,
    unit = unit
)

fun RecipeStep.toEntity(recipeId: String): StepEntity = StepEntity(
    id = id,
    recipeId = recipeId,
    stepNumber = stepNumber,
    title = title,
    description = description
)
