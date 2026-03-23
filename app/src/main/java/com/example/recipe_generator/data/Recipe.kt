package com.example.recipe_generator.data

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
            imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuD-skupaaoHmltsTInwWb1dIMEL5ju1swLEhsPoOygIAO2q2WkhSbwWBZ5XVlj3Ydyy3cx06JiqmCaXirYHYS7-o39xNqBjWyL5q95kJt4KTslbtGKH-p1IkzZqs6QzgLmhdvZ3fwiKOkrUJw5K9E5wZuVHV3CAOJH6Zy2i-qkLwjqiV4dqUspuxujiFO-TLVqJ3gmTmd0qUJ3j4NO5OgYmn2eEIXISYj1z9ZpFRPhT4JXU_Hsd_5y3TGR9Ni8k7H2TmiTCF3N03Ns",
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
            imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuAerh3Tc_sf8AoOJhTh69161Gv5EGI_35uhHQMgiEPAoLi9wKd0G1Iow8Ew8LZ340V_QC5-4yjcEKLt9IgAO8UvJznBSfP6PQEo1NAg-gVJtOLrACNzn9lHBUVkhfmLWaFXKpffd5VvncMnisOTSCBF-doOGPdG-p_5FEilp-BCBGuPtNNSrgQM7L6DPShmU5A0VFfQ_b7jd5dRroXaYKLaA2dthdDOrfIVAmZVdJatOgbhb5T5OKZzUNbC_iEG8nd--M6s7AxrknI",
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
            imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuCCI-KpLGASvL0cR8555kBm2kXT6iF8DfysL0o-KI_fy6hVP1mORYGcTrCXFUF5i20D-aSddb3Yj_M1v1EMCkPs1lGZg2dD_0FeF5c13z1-KYAlt09KuqbfFJRGLmslj-2oX-e8VsLhp2fEtu8PQtZ7ngRCk8L0YNDX0tSEMA_gRrInkO7fKUcKv7ftivjf1YiBik5yfcuqNWUZmp6_nJsk5PGJqKAT5V-NZy9ZbzxUaCuHKnTMFUTir_6k55MWz1jauQYfx6wXbWk",
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
            imageUrl = "https://www.themealdb.com/images/media/meals/y2irzl1585563479.jpg",
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
            imageUrl = "https://www.themealdb.com/images/media/meals/wyxwsp1486979827.jpg",
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
            imageUrl = "https://www.themealdb.com/images/media/meals/rwvw8q1765660071.jpg",
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
            imageUrl = "https://www.themealdb.com/images/media/meals/7mxnzz1593350801.jpg",
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
            imageUrl = "https://www.themealdb.com/images/media/meals/wvqpwt1468339226.jpg",
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
            imageUrl = "https://www.themealdb.com/images/media/meals/ikizdm1763760862.jpg",
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

fun getAllRecipes(): List<Recipe> {
    return weeklyMenus.values.flatten()
}

fun getFeaturedRecipeDetail(): Recipe {
    return weeklyMenus["Lunes"]!!.first { it.id == "2" }
}
