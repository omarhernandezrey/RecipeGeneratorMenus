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
            description = "Huevos revueltos cremosos acompañados de aguacate fresco, tostadas integrales y un toque de limón.",
            proteinGrams = 16,
            carbsGrams = 14,
            fatGrams = 21,
            ingredients = listOf("3 Huevos grandes", "1/2 Aguacate maduro", "2 rebanadas Pan integral"),
            ingredientTags = listOf("Mantequilla", "Sal y pimienta", "Limón"),
            steps = listOf(
                RecipeStep("Batir los huevos", "Bate los huevos en un tazón con sal y pimienta hasta integrar bien."),
                RecipeStep("Cocinar a fuego bajo", "Derrite mantequilla en sartén a fuego bajo. Agrega los huevos y revuelve suavemente."),
                RecipeStep("Preparar aguacate", "Aplasta el aguacate con un tenedor, agrega limón y sal al gusto."),
                RecipeStep("Servir", "Sirve los huevos sobre tostadas con el aguacate al lado.")
            )
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
            description = "Sopa cremosa de tomates frescos con albahaca aromática, perfecta para una cena reconfortante.",
            proteinGrams = 9,
            carbsGrams = 28,
            fatGrams = 12,
            ingredients = listOf("5 Tomates maduros", "1 Cebolla mediana", "2 dientes Ajo"),
            ingredientTags = listOf("Albahaca fresca", "Crema de leche", "Aceite de oliva"),
            steps = listOf(
                RecipeStep("Sofreír base", "Sofríe cebolla y ajo en aceite de oliva hasta transparentar."),
                RecipeStep("Añadir tomates", "Agrega tomates picados, sal y azúcar. Cocina 10 min a fuego medio."),
                RecipeStep("Licuar", "Tritura todo con una batidora hasta obtener textura suave."),
                RecipeStep("Servir", "Sirve caliente con albahaca fresca y un toque de crema.")
            )
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
            description = "Yogur griego cremoso con granola crujiente, miel y frutas rojas frescas.",
            proteinGrams = 14,
            carbsGrams = 30,
            fatGrams = 8,
            ingredients = listOf("200g Yogur griego natural", "1/2 taza Granola", "1 taza Frutas rojas"),
            ingredientTags = listOf("Miel", "Semillas de lino", "Canela"),
            steps = listOf(
                RecipeStep("Servir yogur", "Coloca el yogur griego en un tazón o vaso ancho."),
                RecipeStep("Añadir granola", "Esparce la granola sobre el yogur para añadir textura crujiente."),
                RecipeStep("Decorar", "Agrega las frutas rojas, un chorrito de miel y espolvorea canela.")
            )
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
            description = "Pechuga de pollo a la plancha marinada con hierbas mediterráneas y servida con ensalada.",
            proteinGrams = 38,
            carbsGrams = 24,
            fatGrams = 18,
            ingredients = listOf("2 Pechugas de pollo", "1 limón", "2 dientes Ajo"),
            ingredientTags = listOf("Orégano", "Tomillo", "Aceite de oliva"),
            steps = listOf(
                RecipeStep("Marinar el pollo", "Mezcla ajo, limón, orégano y tomillo. Marina el pollo 15 minutos."),
                RecipeStep("Calentar plancha", "Precalienta la plancha o sartén a fuego alto con un poco de aceite."),
                RecipeStep("Cocinar el pollo", "Cocina cada pechuga 6-7 minutos por lado hasta dorar bien."),
                RecipeStep("Reposar y servir", "Deja reposar 3 minutos antes de cortar. Sirve con ensalada verde.")
            )
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
            description = "Salteado de vegetales coloridos al wok con salsa de soya, jengibre y aceite de sésamo.",
            proteinGrams = 11,
            carbsGrams = 36,
            fatGrams = 10,
            ingredients = listOf("1 Pimiento rojo", "1 Zanahoria", "1 taza Brócoli"),
            ingredientTags = listOf("Salsa de soya", "Jengibre", "Aceite de sésamo"),
            steps = listOf(
                RecipeStep("Preparar vegetales", "Corta todos los vegetales en juliana o floretes similares."),
                RecipeStep("Calentar wok", "Calienta el wok a fuego muy alto. Agrega aceite de sésamo."),
                RecipeStep("Saltear", "Saltea los vegetales 5 minutos removiendo constantemente."),
                RecipeStep("Sazonar", "Agrega salsa de soya y jengibre rallado. Cocina 2 minutos más.")
            )
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
            description = "Croissants de mantequilla recién horneados con mermelada artesanal de frutos rojos.",
            proteinGrams = 7,
            carbsGrams = 34,
            fatGrams = 14,
            ingredients = listOf("4 Croissants de mantequilla", "4 cucharadas Mermelada frutos rojos", "Mantequilla"),
            ingredientTags = listOf("Mermelada", "Café", "Jugo de naranja"),
            steps = listOf(
                RecipeStep("Precalentar horno", "Precalienta el horno a 180°C durante 5 minutos."),
                RecipeStep("Calentar croissants", "Coloca los croissants en bandeja y calienta 5-7 minutos hasta crujir."),
                RecipeStep("Servir", "Acompaña con mermelada abundante y una bebida caliente.")
            )
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
            description = "Ensalada de pasta con aceitunas, queso feta, tomates cherry y vinagreta mediterránea.",
            proteinGrams = 19,
            carbsGrams = 71,
            fatGrams = 21,
            ingredients = listOf("250g Pasta fusilli", "100g Queso feta", "1/2 taza Aceitunas negras"),
            ingredientTags = listOf("Tomates cherry", "Albahaca", "Vinagreta"),
            steps = listOf(
                RecipeStep("Cocinar pasta", "Hierve la pasta al dente según instrucciones. Escurre y enfría."),
                RecipeStep("Preparar vinagreta", "Mezcla aceite de oliva, vinagre balsámico, orégano y sal."),
                RecipeStep("Mezclar ensalada", "Combina pasta con aceitunas, feta y tomates cherry."),
                RecipeStep("Aderezar y servir", "Vierte la vinagreta, mezcla bien y sirve a temperatura ambiente.")
            )
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
            description = "Filete de salmón al horno con costra de hierbas, limón y ajo, servido con vegetales.",
            proteinGrams = 33,
            carbsGrams = 8,
            fatGrams = 24,
            ingredients = listOf("2 filetes Salmón (200g c/u)", "1 Limón", "3 dientes Ajo"),
            ingredientTags = listOf("Eneldo", "Perejil", "Aceite de oliva"),
            steps = listOf(
                RecipeStep("Preparar marinada", "Mezcla ajo picado, eneldo, perejil, aceite y jugo de limón."),
                RecipeStep("Marinar salmón", "Unta la mezcla sobre el salmón y deja reposar 10 minutos."),
                RecipeStep("Hornear", "Hornea a 200°C por 12-15 minutos hasta que el salmón se desmenuce."),
                RecipeStep("Servir", "Sirve con rodajas de limón y vegetales al vapor.")
            )
        )
    ),
    // F3-26: Jueves — 3 recetas nuevas
    "Jueves" to listOf(
        Recipe(
            id = "10",
            title = "Smoothie Bowl de Frutas",
            imageUrl = "img_placeholder",
            timeInMinutes = 10,
            calories = 290,
            difficulty = "Fácil",
            category = "Desayuno",
            categorySubtitle = "Impulso de Energía",
            description = "Bowl refrescante con frutas tropicales, semillas y granola crujiente.",
            proteinGrams = 8,
            carbsGrams = 52,
            fatGrams = 7,
            ingredients = listOf("1 taza Mango", "1 Banana", "1/2 taza Leche de coco"),
            ingredientTags = listOf("Granola", "Semillas de chía", "Frutas frescas"),
            steps = listOf(
                RecipeStep("Licuar frutas", "Procesa el mango y la banana con leche de coco hasta obtener una crema espesa."),
                RecipeStep("Servir en bowl", "Vierte en un bowl y decora con granola, frutas y semillas de chía.")
            )
        ),
        Recipe(
            id = "11",
            title = "Pollo al Limón con Arroz",
            imageUrl = "img_placeholder",
            timeInMinutes = 35,
            calories = 490,
            difficulty = "Medio",
            category = "Almuerzo",
            categorySubtitle = "Equilibrado y Nutritivo",
            description = "Pechuga de pollo marinada en limón y hierbas, servida con arroz integral.",
            proteinGrams = 42,
            carbsGrams = 38,
            fatGrams = 12,
            ingredients = listOf("2 pechugas Pollo", "1 Limón", "1 taza Arroz integral"),
            ingredientTags = listOf("Ajo", "Romero", "Aceite de oliva"),
            steps = listOf(
                RecipeStep("Marinar pollo", "Mezcla jugo de limón, ajo y romero. Marina el pollo 20 minutos."),
                RecipeStep("Cocinar arroz", "Hierve el arroz integral según instrucciones del paquete."),
                RecipeStep("Asar pollo", "Cocina el pollo en sartén a fuego medio 6 minutos por lado.")
            )
        ),
        Recipe(
            id = "12",
            title = "Crema de Calabaza",
            imageUrl = "img_placeholder",
            timeInMinutes = 30,
            calories = 280,
            difficulty = "Fácil",
            category = "Cena",
            categorySubtitle = "Ligero y Acogedor",
            description = "Crema suave de calabaza con especias cálidas y crema de coco.",
            proteinGrams = 6,
            carbsGrams = 32,
            fatGrams = 14,
            ingredients = listOf("500g Calabaza", "1 taza Caldo vegetal", "200ml Leche de coco"),
            ingredientTags = listOf("Jengibre", "Cúrcuma", "Pimienta negra"),
            steps = listOf(
                RecipeStep("Cortar y asar", "Corta la calabaza en cubos y asa en horno 20 min a 180°C."),
                RecipeStep("Licuar", "Procesa la calabaza asada con el caldo hasta obtener crema suave."),
                RecipeStep("Condimentar", "Calienta con leche de coco, jengibre y cúrcuma. Sirve caliente.")
            )
        )
    ),
    // F3-26: Viernes — 3 recetas nuevas
    "Viernes" to listOf(
        Recipe(
            id = "13",
            title = "Tostadas con Aguacate y Huevo",
            imageUrl = "img_placeholder",
            timeInMinutes = 12,
            calories = 380,
            difficulty = "Fácil",
            category = "Desayuno",
            categorySubtitle = "Impulso de Energía",
            description = "Pan integral tostado con aguacate cremoso y huevo pochado perfecto.",
            proteinGrams = 18,
            carbsGrams = 28,
            fatGrams = 22,
            ingredients = listOf("2 rebanadas Pan integral", "1 Aguacate maduro", "2 Huevos"),
            ingredientTags = listOf("Sal de mar", "Pimienta", "Limón"),
            steps = listOf(
                RecipeStep("Tostar pan", "Tuesta las rebanadas de pan hasta que estén doradas."),
                RecipeStep("Preparar aguacate", "Aplasta el aguacate con limón, sal y pimienta."),
                RecipeStep("Pochar huevos", "Escalfa los huevos en agua con vinagre blanco por 3 minutos.")
            )
        ),
        Recipe(
            id = "14",
            title = "Tacos de Frijoles Negros",
            imageUrl = "img_placeholder",
            timeInMinutes = 20,
            calories = 430,
            difficulty = "Fácil",
            category = "Almuerzo",
            categorySubtitle = "Equilibrado y Nutritivo",
            description = "Tacos vegetarianos con frijoles negros, pico de gallo y queso fresco.",
            proteinGrams = 16,
            carbsGrams = 55,
            fatGrams = 14,
            ingredients = listOf("1 lata Frijoles negros", "8 Tortillas maíz", "2 Tomates"),
            ingredientTags = listOf("Cilantro", "Cebolla", "Queso fresco"),
            steps = listOf(
                RecipeStep("Calentar frijoles", "Calienta los frijoles con comino y ajo en sartén."),
                RecipeStep("Pico de gallo", "Pica tomate, cebolla y cilantro. Mezcla con limón."),
                RecipeStep("Armar tacos", "Coloca frijoles en tortillas calientes y agrega pico de gallo.")
            )
        ),
        Recipe(
            id = "15",
            title = "Ensalada Caprese",
            imageUrl = "img_placeholder",
            timeInMinutes = 8,
            calories = 260,
            difficulty = "Fácil",
            category = "Cena",
            categorySubtitle = "Ligero y Acogedor",
            description = "Clásica ensalada italiana con tomate, mozzarella fresca y albahaca.",
            proteinGrams = 14,
            carbsGrams = 8,
            fatGrams = 18,
            ingredients = listOf("3 Tomates maduros", "200g Mozzarella fresca", "Albahaca fresca"),
            ingredientTags = listOf("Aceite de oliva virgen", "Sal gruesa", "Pimienta"),
            steps = listOf(
                RecipeStep("Cortar ingredientes", "Corta tomate y mozzarella en rodajas de 1cm."),
                RecipeStep("Montar ensalada", "Intercala tomate y mozzarella. Añade albahaca entre cada capa."),
                RecipeStep("Aderezar", "Rocía con aceite de oliva, sal gruesa y pimienta al gusto.")
            )
        )
    ),
    // F3-26: Sábado — 3 recetas nuevas
    "Sábado" to listOf(
        Recipe(
            id = "16",
            title = "Pancakes de Avena con Arándanos",
            imageUrl = "img_placeholder",
            timeInMinutes = 20,
            calories = 340,
            difficulty = "Fácil",
            category = "Desayuno",
            categorySubtitle = "Impulso de Energía",
            description = "Pancakes esponjosos de avena sin azúcar refinada con arándanos frescos.",
            proteinGrams = 12,
            carbsGrams = 48,
            fatGrams = 10,
            ingredients = listOf("1 taza Avena molida", "2 Huevos", "1/2 taza Leche"),
            ingredientTags = listOf("Arándanos", "Miel", "Canela"),
            steps = listOf(
                RecipeStep("Mezclar masa", "Combina avena, huevos y leche hasta obtener masa homogénea."),
                RecipeStep("Cocinar", "Vierte cucharadas de masa en sartén antiadherente a fuego medio."),
                RecipeStep("Servir", "Acompaña con arándanos frescos y un toque de miel.")
            )
        ),
        Recipe(
            id = "17",
            title = "Paella Vegetariana",
            imageUrl = "img_placeholder",
            timeInMinutes = 45,
            calories = 520,
            difficulty = "Difícil",
            category = "Almuerzo",
            categorySubtitle = "Equilibrado y Nutritivo",
            description = "Paella española con vegetales de temporada, azafrán y pimentón ahumado.",
            proteinGrams = 14,
            carbsGrams = 82,
            fatGrams = 16,
            ingredients = listOf("2 tazas Arroz bomba", "1 Pimiento rojo", "1 Calabacín"),
            ingredientTags = listOf("Azafrán", "Pimentón ahumado", "Caldo vegetal"),
            steps = listOf(
                RecipeStep("Sofreír vegetales", "Saltea pimiento, calabacín y tomate en aceite de oliva."),
                RecipeStep("Añadir arroz", "Agrega el arroz y sofríe 2 min. Incorpora el azafrán."),
                RecipeStep("Cocinar a fuego lento", "Vierte el caldo caliente y cocina 18 min sin remover.")
            )
        ),
        Recipe(
            id = "18",
            title = "Sopa de Lentejas Rojas",
            imageUrl = "img_placeholder",
            timeInMinutes = 25,
            calories = 310,
            difficulty = "Fácil",
            category = "Cena",
            categorySubtitle = "Ligero y Acogedor",
            description = "Reconfortante sopa de lentejas rojas con curry suave y leche de coco.",
            proteinGrams = 18,
            carbsGrams = 38,
            fatGrams = 8,
            ingredients = listOf("1 taza Lentejas rojas", "1 lata Tomates pelados", "200ml Leche de coco"),
            ingredientTags = listOf("Curry en polvo", "Cúrcuma", "Cilantro"),
            steps = listOf(
                RecipeStep("Sofrito base", "Saltea cebolla y ajo. Añade curry y cúrcuma, cocina 1 min."),
                RecipeStep("Añadir lentejas", "Incorpora lentejas, tomates y caldo. Hierve 15 min."),
                RecipeStep("Finalizar", "Agrega leche de coco y ajusta sal. Sirve con cilantro.")
            )
        )
    ),
    // F3-26: Domingo — 3 recetas nuevas
    "Domingo" to listOf(
        Recipe(
            id = "19",
            title = "French Toast con Frutas",
            imageUrl = "img_placeholder",
            timeInMinutes = 15,
            calories = 360,
            difficulty = "Fácil",
            category = "Desayuno",
            categorySubtitle = "Impulso de Energía",
            description = "Torrija francesa esponjosa con miel y frutas frescas de temporada.",
            proteinGrams = 14,
            carbsGrams = 44,
            fatGrams = 16,
            ingredients = listOf("4 rebanadas Pan brioche", "3 Huevos", "1/2 taza Leche"),
            ingredientTags = listOf("Canela", "Vainilla", "Frutas mixtas"),
            steps = listOf(
                RecipeStep("Preparar mezcla", "Bate huevos, leche, canela y vainilla en un tazón."),
                RecipeStep("Remojar pan", "Sumerge cada rebanada en la mezcla por 30 segundos."),
                RecipeStep("Dorar y servir", "Cocina en mantequilla hasta dorar. Sirve con frutas y miel.")
            )
        ),
        Recipe(
            id = "20",
            title = "Arroz con Pollo Colombiano",
            imageUrl = "img_placeholder",
            timeInMinutes = 50,
            calories = 540,
            difficulty = "Difícil",
            category = "Almuerzo",
            categorySubtitle = "Equilibrado y Nutritivo",
            description = "Clásico arroz con pollo colombiano con cilantro, zanahoria y arvejas.",
            proteinGrams = 38,
            carbsGrams = 58,
            fatGrams = 14,
            ingredients = listOf("500g Pollo", "2 tazas Arroz", "1 Zanahoria"),
            ingredientTags = listOf("Cilantro", "Arvejas", "Caldo de pollo"),
            steps = listOf(
                RecipeStep("Cocinar pollo", "Hierve el pollo con cebolla y ajo hasta suavizar. Reserva el caldo."),
                RecipeStep("Sofreír", "Sofríe arroz con zanahoria y arvejas en aceite hasta dorar."),
                RecipeStep("Cocinar junto", "Agrega el pollo desmechado y el caldo. Cocina 20 min a fuego bajo.")
            )
        ),
        Recipe(
            id = "21",
            title = "Sopa de Tomate Rostizado",
            imageUrl = "img_placeholder",
            timeInMinutes = 35,
            calories = 200,
            difficulty = "Fácil",
            category = "Cena",
            categorySubtitle = "Ligero y Acogedor",
            description = "Sopa de tomates rostizados con albahaca fresca y un toque de crema.",
            proteinGrams = 5,
            carbsGrams = 22,
            fatGrams = 10,
            ingredients = listOf("6 Tomates maduros", "1 Cebolla", "3 dientes Ajo"),
            ingredientTags = listOf("Albahaca fresca", "Crema de leche", "Pan tostado"),
            steps = listOf(
                RecipeStep("Rostizar", "Asa tomates, cebolla y ajo en horno 200°C por 25 minutos."),
                RecipeStep("Licuar", "Procesa todo junto con caldo hasta obtener sopa suave."),
                RecipeStep("Servir", "Cuela, calienta y sirve con albahaca y un chorrito de crema.")
            )
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
