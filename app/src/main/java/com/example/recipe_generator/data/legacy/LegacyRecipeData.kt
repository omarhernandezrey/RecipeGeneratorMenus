package com.example.recipe_generator.data.legacy

data class Recipe(
    val id: String,
    val title: String,
    val imageUrl: String,
    val timeInMinutes: Int,
    val calories: Int,
    val difficulty: String,
    val category: String,
    val categorySubtitle: String,
    val isFavorite: Boolean = false,
    val rating: Double = 4.5,
    val description: String = "Una receta equilibrada, fresca y pensada para el día a día.",
    val proteinGrams: Int = 18,
    val carbsGrams: Int = 65,
    val fatGrams: Int = 22,
    val ingredients: List<String> = emptyList(),
    val ingredientTags: List<String> = emptyList(),
    val steps: List<RecipeStep> = emptyList()
)

data class RecipeStep(
    val title: String,
    val description: String
)

val weeklyMenus = mapOf(
    "Lunes" to listOf(
        Recipe(
            id = "1",
            title = "Scrambled Eggs with Avocado",
            imageUrl = "img_placeholder",
            timeInMinutes = 15,
            calories = 350,
            difficulty = "Fácil",
            category = "Desayuno",
            categorySubtitle = "Impulso de Energía",
            proteinGrams = 16,
            carbsGrams = 14,
            fatGrams = 21
        ),
        Recipe(
            id = "2",
            title = "Ensalada de Quinoa y Vegetales Rostizados",
            imageUrl = "img_placeholder",
            timeInMinutes = 25,
            calories = 520,
            difficulty = "Medio",
            category = "Almuerzo",
            categorySubtitle = "Equilibrado y Nutritivo",
            rating = 4.5,
            description = "Una opción saludable, fresca y llena de proteínas para tu almuerzo",
            proteinGrams = 18,
            carbsGrams = 65,
            fatGrams = 22,
            ingredients = listOf(
                "1 taza de Quinoa",
                "2 tazas de Agua",
                "1 Pimiento Rojo",
                "1 Calabacín"
            ),
            ingredientTags = listOf("Aceite de Oliva", "Limón", "Sal y Pimienta"),
            steps = listOf(
                RecipeStep(
                    title = "Lavar la quinoa",
                    description = "Enjuaga bien la quinoa bajo agua fría hasta que el agua salga clara para quitar el amargor."
                ),
                RecipeStep(
                    title = "Cortar los vegetales",
                    description = "Corta el pimiento y el calabacín en cubos uniformes para una cocción pareja."
                ),
                RecipeStep(
                    title = "Rostizar a 200C",
                    description = "Coloca los vegetales en una bandeja con aceite de oliva y sal. Rostiza hasta que estén dorados."
                ),
                RecipeStep(
                    title = "Mezclar y servir",
                    description = "Mezcla la quinoa cocida con los vegetales, añade limón y sirve a temperatura ambiente."
                )
            )
        ),
        Recipe(
            id = "3",
            title = "Tomato & Basil Soup",
            imageUrl = "img_placeholder",
            timeInMinutes = 20,
            calories = 310,
            difficulty = "Fácil",
            category = "Cena",
            categorySubtitle = "Ligero y Acogedor",
            proteinGrams = 9,
            carbsGrams = 28,
            fatGrams = 12
        )
    ),
    "Martes" to listOf(
        Recipe(
            id = "4",
            title = "Greek Yogurt with Granola",
            imageUrl = "img_placeholder",
            timeInMinutes = 5,
            calories = 280,
            difficulty = "Fácil",
            category = "Desayuno",
            categorySubtitle = "Impulso de Energía",
            proteinGrams = 14,
            carbsGrams = 30,
            fatGrams = 8
        ),
        Recipe(
            id = "5",
            title = "Grilled Chicken Breast",
            imageUrl = "img_placeholder",
            timeInMinutes = 30,
            calories = 450,
            difficulty = "Medio",
            category = "Almuerzo",
            categorySubtitle = "Equilibrado y Nutritivo",
            proteinGrams = 38,
            carbsGrams = 24,
            fatGrams = 18
        ),
        Recipe(
            id = "6",
            title = "Vegetable Stir Fry",
            imageUrl = "img_placeholder",
            timeInMinutes = 18,
            calories = 320,
            difficulty = "Fácil",
            category = "Cena",
            categorySubtitle = "Ligero y Acogedor",
            proteinGrams = 11,
            carbsGrams = 36,
            fatGrams = 10
        )
    ),
    "Miércoles" to listOf(
        Recipe(
            id = "7",
            title = "Croissants & Jam",
            imageUrl = "img_placeholder",
            timeInMinutes = 25,
            calories = 320,
            difficulty = "Medio",
            category = "Desayuno",
            categorySubtitle = "Impulso de Energía",
            proteinGrams = 7,
            carbsGrams = 34,
            fatGrams = 14
        ),
        Recipe(
            id = "8",
            title = "Mediterranean Pasta Salad",
            imageUrl = "img_placeholder",
            timeInMinutes = 20,
            calories = 580,
            difficulty = "Medio",
            category = "Almuerzo",
            categorySubtitle = "Equilibrado y Nutritivo",
            proteinGrams = 19,
            carbsGrams = 71,
            fatGrams = 21
        ),
        Recipe(
            id = "9",
            title = "Baked Salmon",
            imageUrl = "img_placeholder",
            timeInMinutes = 25,
            calories = 420,
            difficulty = "Medio",
            category = "Cena",
            categorySubtitle = "Ligero y Acogedor",
            proteinGrams = 33,
            carbsGrams = 8,
            fatGrams = 24
        )
    )
)

fun getMenuForDay(day: String): List<Recipe> {
    return weeklyMenus[day] ?: weeklyMenus["Lunes"]!!
}

fun getMenuForDayAsDomainModel(day: String): List<com.example.recipe_generator.domain.model.Recipe> {
    return getMenuForDay(day).map { legacyRecipe ->
        com.example.recipe_generator.domain.model.Recipe(
            id = legacyRecipe.id,
            title = legacyRecipe.title,
            imageRes = legacyRecipe.imageUrl,
            timeInMinutes = legacyRecipe.timeInMinutes,
            calories = legacyRecipe.calories,
            difficulty = legacyRecipe.difficulty,
            category = legacyRecipe.category,
            categorySubtitle = legacyRecipe.categorySubtitle,
            description = legacyRecipe.description,
            isFavorite = legacyRecipe.isFavorite,
            rating = legacyRecipe.rating,
            proteinGrams = legacyRecipe.proteinGrams,
            carbsGrams = legacyRecipe.carbsGrams,
            fatGrams = legacyRecipe.fatGrams,
            dayOfWeek = day,
            ingredients = emptyList(),
            ingredientTags = legacyRecipe.ingredientTags,
            steps = legacyRecipe.steps.mapIndexed { index, recipeStep -> 
                com.example.recipe_generator.domain.model.RecipeStep(
                    stepNumber = index + 1,
                    title = recipeStep.title,
                    description = recipeStep.description
                )
            }
        )
    }
}

fun getAllRecipes(): List<Recipe> {
    return weeklyMenus.values.flatten()
}

fun getFeaturedRecipeDetail(): Recipe {
    return weeklyMenus["Lunes"]!!.first { it.id == "2" }
}

fun getAllRecipesAsDomainModel(): List<com.example.recipe_generator.domain.model.Recipe> {
    return weeklyMenus.entries.flatMap { (day, recipes) ->
        recipes.map { legacyRecipe ->
            com.example.recipe_generator.domain.model.Recipe(
                id = legacyRecipe.id,
                title = legacyRecipe.title,
                imageRes = legacyRecipe.imageUrl,
                timeInMinutes = legacyRecipe.timeInMinutes,
                calories = legacyRecipe.calories,
                difficulty = legacyRecipe.difficulty,
                category = legacyRecipe.category,
                categorySubtitle = legacyRecipe.categorySubtitle,
                description = legacyRecipe.description,
                isFavorite = legacyRecipe.isFavorite,
                rating = legacyRecipe.rating,
                proteinGrams = legacyRecipe.proteinGrams,
                carbsGrams = legacyRecipe.carbsGrams,
                fatGrams = legacyRecipe.fatGrams,
                dayOfWeek = day,
                ingredients = emptyList(),
                ingredientTags = legacyRecipe.ingredientTags,
                steps = legacyRecipe.steps.mapIndexed { index, recipeStep ->
                    com.example.recipe_generator.domain.model.RecipeStep(
                        stepNumber = index + 1,
                        title = recipeStep.title,
                        description = recipeStep.description
                    )
                }
            )
        }
    }
}
