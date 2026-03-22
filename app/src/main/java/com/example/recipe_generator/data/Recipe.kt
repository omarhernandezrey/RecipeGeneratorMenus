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
    val isFavorite: Boolean = false
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
            categorySubtitle = "Impulso de Energía"
        ),
        Recipe(
            id = "2",
            title = "Quinoa & Roasted Veggies",
            imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuCcB5FWDGJee0tus0iivV_8tJCRA3aNkec-jWi0GM2vdt5QvE_iKczOXn4MY58jgXZ708BGhetvOn58FYuBmMlbsbml7GoRv8DbbH3LuuDr0_Byy0T9huNECsTWUS1LuGXgQJaBvKLQ8HRoYZlb-VQyf6SLYWPW3Alhicgb65cWl4A23gLTx5-ksdqkzSQx-pslNHTlcqYrDzuMg1R81Ff-6WQ8ljCMLUILMCAT3-etf_ODMJiBvLzjFXSB7rixnYzYf25cDkpk7Ko",
            timeInMinutes = 25,
            calories = 520,
            difficulty = "Medio",
            category = "Almuerzo",
            categorySubtitle = "Equilibrado y Nutritivo"
        ),
        Recipe(
            id = "3",
            title = "Tomato & Basil Soup",
            imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuCCI-KpLGASvL0cR8555kBm2kXT6iF8DfysL0o-KI_fy6hVP1mORYGcTrCXFUF5i20D-aSddb3Yj_M1v1EMCkPs1lGZg2dD_0FeF5c13z1-KYAlt09KuqbfFJRGLmslj-2oX-e8VsLhp2fEtu8PQtZ7ngRCk8L0YNDX0tSEMA_gRrInkO7fKUcKv7ftivjf1YiBik5yfcuqNWUZmp6_nJsk5PGJqKAT5V-NZy9ZbzxUaCuHKnTMFUTir_6k55MWz1jauQYfx6wXbWk",
            timeInMinutes = 20,
            calories = 310,
            difficulty = "Fácil",
            category = "Cena",
            categorySubtitle = "Ligero y Acogedor"
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
            categorySubtitle = "Impulso de Energía"
        ),
        Recipe(
            id = "5",
            title = "Grilled Chicken Breast",
            imageUrl = "https://www.themealdb.com/images/media/meals/wyxwsp1486979827.jpg",
            timeInMinutes = 30,
            calories = 450,
            difficulty = "Medio",
            category = "Almuerzo",
            categorySubtitle = "Equilibrado y Nutritivo"
        ),
        Recipe(
            id = "6",
            title = "Vegetable Stir Fry",
            imageUrl = "https://www.themealdb.com/images/media/meals/rwvw8q1765660071.jpg",
            timeInMinutes = 18,
            calories = 320,
            difficulty = "Fácil",
            category = "Cena",
            categorySubtitle = "Ligero y Acogedor"
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
            categorySubtitle = "Impulso de Energía"
        ),
        Recipe(
            id = "8",
            title = "Mediterranean Pasta Salad",
            imageUrl = "https://www.themealdb.com/images/media/meals/wvqpwt1468339226.jpg",
            timeInMinutes = 20,
            calories = 580,
            difficulty = "Medio",
            category = "Almuerzo",
            categorySubtitle = "Equilibrado y Nutritivo"
        ),
        Recipe(
            id = "9",
            title = "Baked Salmon",
            imageUrl = "https://www.themealdb.com/images/media/meals/ikizdm1763760862.jpg",
            timeInMinutes = 25,
            calories = 420,
            difficulty = "Medio",
            category = "Cena",
            categorySubtitle = "Ligero y Acogedor"
        )
    )
)

fun getMenuForDay(day: String): List<Recipe> {
    return weeklyMenus[day] ?: weeklyMenus["Lunes"]!!
}
