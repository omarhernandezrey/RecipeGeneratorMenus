package com.example.recipe_generator.data.remote

/**
 * Base de datos local de recetas colombianas — sin API, sin red.
 * Ingredientes y pasos en español.
 */
object ColombianRecipesDatabase {

    val recipes: List<MealDbFullRecipe> = listOf(

        MealDbFullRecipe(
            id = "col_001",
            name = "Bandeja Paisa",
            category = "Almuerzo",
            area = "Colombia",
            thumbUrl = "",
            instructions = """
Cocina los frijoles rojos con un trozo de costilla de cerdo, cebolla cabezona y un poco de sal. Deja hervir a fuego medio durante 1 hora o hasta que estén blandos.
Prepara el hogao: sofríe tomate y cebolla larga picados en aceite a fuego medio durante 15 minutos hasta obtener una salsa espesa.
Sazona la carne molida con sal, comino y pimienta. Fríe en sartén con un poco de aceite y mézclala con la mitad del hogao.
Fríe las tajadas de chorizo y la morcilla en una sartén a fuego medio-alto hasta que estén doradas.
Fríe el chicharrón en aceite bien caliente hasta que quede crujiente y dorado.
Cocina el arroz blanco aparte con sal y un poco de aceite.
Fríe los huevos al gusto (fritos o revueltos).
Fríe las rodajas de plátano maduro en aceite caliente hasta que doren.
Sirve todo en un plato grande: arroz, frijoles con su caldo, carne molida, chicharrón, chorizo, morcilla, huevo, plátano maduro, aguacate y arepa.
            """.trimIndent(),
            ingredients = listOf(
                "300 g frijoles rojos",
                "200 g costilla de cerdo",
                "150 g carne molida de res",
                "2 chorizos antioqueños",
                "1 morcilla",
                "150 g chicharrón",
                "2 huevos",
                "1 taza arroz blanco",
                "1 plátano maduro",
                "1 aguacate",
                "2 arepas",
                "2 tomates",
                "1 cebolla cabezona",
                "Cebolla larga al gusto",
                "Sal, comino y pimienta al gusto"
            )
        ),

        MealDbFullRecipe(
            id = "col_002",
            name = "Ajiaco Bogotano",
            category = "Almuerzo",
            area = "Colombia",
            thumbUrl = "",
            instructions = """
Pon a hervir en una olla grande el agua con las presas de pollo, el maíz troceado y sal. Cocina por 20 minutos.
Agrega las tres variedades de papa: papa criolla, papa pastusa y papa única. La papa criolla se deshace y espesa el caldo.
Añade las mazorcas de maíz y la guasca seca. Cocina a fuego medio durante 30 minutos más.
Retira el pollo cuando esté tierno, desmecha la carne y devuélvela a la olla.
Ajusta la sal y deja cocinar 10 minutos adicionales hasta que el caldo esté bien espeso.
Sirve en plato hondo con crema de leche, alcaparras y aguacate al lado.
            """.trimIndent(),
            ingredients = listOf(
                "500 g pollo (presas con hueso)",
                "300 g papa criolla",
                "300 g papa pastusa",
                "200 g papa única",
                "2 mazorcas de maíz",
                "1 cucharada guasca seca",
                "Crema de leche al gusto",
                "2 cucharadas alcaparras",
                "1 aguacate",
                "Sal al gusto",
                "2 litros de agua"
            )
        ),

        MealDbFullRecipe(
            id = "col_003",
            name = "Sancocho de Gallina",
            category = "Almuerzo",
            area = "Colombia",
            thumbUrl = "",
            instructions = """
Pon a hervir la gallina en agua con sal, cilantro, cebolla larga y ajo por 40 minutos a fuego medio.
Agrega el plátano verde pelado y troceado. Cocina 15 minutos.
Incorpora la yuca pelada y cortada en trozos grandes. Cocina 20 minutos más.
Añade el maíz troceado y las papas peladas. Cocina hasta que todo esté blando.
Prepara el hogao aparte: sofríe tomate, cebolla y ajo picados con un poco de aceite.
Mezcla el hogao con el sancocho y ajusta la sal.
Sirve bien caliente con arroz blanco, aguacate y ají picante al lado.
            """.trimIndent(),
            ingredients = listOf(
                "1 gallina entera cortada en presas",
                "2 plátanos verdes",
                "500 g yuca",
                "2 mazorcas de maíz",
                "4 papas medianas",
                "2 tomates",
                "1 cebolla cabezona",
                "Cebolla larga al gusto",
                "3 dientes de ajo",
                "Cilantro al gusto",
                "Sal y comino al gusto",
                "1 aguacate para servir"
            )
        ),

        MealDbFullRecipe(
            id = "col_004",
            name = "Arroz con Pollo",
            category = "Almuerzo",
            area = "Colombia",
            thumbUrl = "",
            instructions = """
Sazona el pollo con sal, pimienta, comino y ajo molido. Deja marinar 30 minutos.
En una olla grande calienta aceite y dora el pollo por todos lados a fuego alto.
Retira el pollo y en la misma olla sofríe la cebolla, el tomate, el ajo y el pimentón durante 10 minutos.
Agrega el arroz lavado y sofríe 3 minutos con el hogao.
Incorpora el caldo de pollo caliente, los guisantes y el pollo. La proporción es 1 taza de arroz por 2 tazas de caldo.
Cocina tapado a fuego bajo durante 20 minutos sin destapar.
Decora con cilantro picado y sirve caliente.
            """.trimIndent(),
            ingredients = listOf(
                "500 g pollo en presas",
                "2 tazas arroz",
                "4 tazas caldo de pollo",
                "1 taza guisantes (arvejas)",
                "1 pimentón rojo",
                "2 tomates",
                "1 cebolla cabezona",
                "3 dientes de ajo",
                "Cilantro picado al gusto",
                "Sal, pimienta y comino al gusto",
                "2 cucharadas aceite"
            )
        ),

        MealDbFullRecipe(
            id = "col_005",
            name = "Changua (Sopa de Leche)",
            category = "Desayuno",
            area = "Colombia",
            thumbUrl = "",
            instructions = """
En una olla mediana mezcla el agua con la leche y lleva a fuego medio.
Agrega la cebolla larga entera, sal al gusto y deja hervir.
Cuando hierva, rompe los huevos directamente en la olla con cuidado para que queden enteros.
Cocina los huevos pochados 3 minutos sin revolver.
Sirve en tazón hondo: el huevo entero sumergido en el caldo de leche.
Agrega cilantro picado y trozos de pan chicharrón o calado remojado.
            """.trimIndent(),
            ingredients = listOf(
                "2 tazas leche entera",
                "1 taza agua",
                "2 huevos",
                "2 tallos cebolla larga",
                "Cilantro fresco picado",
                "Pan chicharrón o calado",
                "Sal al gusto"
            )
        ),

        MealDbFullRecipe(
            id = "col_006",
            name = "Arepa de Choclo con Queso",
            category = "Desayuno",
            area = "Colombia",
            thumbUrl = "",
            instructions = """
Muele el choclo (maíz tierno) en un procesador o extrae los granos frescos rallados.
Mezcla el choclo molido con la mantequilla derretida, el azúcar, la sal y la harina de maíz hasta obtener una masa suave.
Forma discos gruesos de unos 10 cm de diámetro y 1 cm de grosor con la masa.
Coloca los discos en una plancha o budare a fuego medio engrasado con mantequilla.
Cocina 5 minutos por cada lado hasta que queden doradas.
Abre la arepa por la mitad y rellena con queso blanco o queso campesino generosamente.
Sirve caliente.
            """.trimIndent(),
            ingredients = listOf(
                "3 mazorcas de maíz tierno (choclo)",
                "2 cucharadas mantequilla",
                "1 cucharada azúcar",
                "1/2 taza harina de maíz",
                "200 g queso blanco o campesino",
                "Sal al gusto"
            )
        ),

        MealDbFullRecipe(
            id = "col_007",
            name = "Empanadas Colombianas de Pipián",
            category = "Snack",
            area = "Colombia",
            thumbUrl = "",
            instructions = """
Prepara el pipián: cocina las papas en agua con sal hasta que estén blandas. Machácalas.
Sofríe cebolla larga, tomate y ají en aceite. Agrega el maní molido y las papas machacadas. Mezcla bien y sazona con sal y comino.
Para la masa: mezcla la harina de maíz amarilla con agua tibia con sal hasta obtener una masa suave que no se rompa.
Forma bolitas de masa del tamaño de una mandarina. Aplana cada bolita en un plástico para formar un círculo delgado.
Pon una cucharada del relleno de pipián en el centro. Dobla la masa para cerrar y sella los bordes presionando con los dedos.
Fríe las empanadas en aceite bien caliente durante 3 minutos por lado hasta que queden doradas y crujientes.
Sirve con ají casero y limón.
            """.trimIndent(),
            ingredients = listOf(
                "2 tazas harina de maíz amarilla",
                "Agua tibia necesaria",
                "3 papas medianas",
                "1/2 taza maní tostado molido",
                "2 tallos cebolla larga",
                "2 tomates",
                "Ají picante al gusto",
                "Aceite para freír",
                "Sal y comino al gusto"
            )
        ),

        MealDbFullRecipe(
            id = "col_008",
            name = "Frijoles Antioqueños con Hogao",
            category = "Almuerzo",
            area = "Colombia",
            thumbUrl = "",
            instructions = """
Remoja los frijoles cargamanto la noche anterior en agua fría.
Al día siguiente escurre y cocina en agua nueva con un trozo de tocino, sal y comino en olla a presión durante 25 minutos.
Prepara el hogao: sofríe tomate picado, cebolla larga y ajo en aceite durante 15 minutos a fuego medio hasta que forme una pasta.
Agrega el hogao a los frijoles ya tiernos. Mezcla bien.
Deja cocinar juntos 10 minutos más hasta que el caldo espese a tu gusto.
Sirve con arroz, chicharrón, plátano maduro frito y aguacate.
            """.trimIndent(),
            ingredients = listOf(
                "500 g frijoles cargamanto",
                "150 g tocino o costilla de cerdo",
                "3 tomates",
                "3 tallos cebolla larga",
                "3 dientes de ajo",
                "Sal y comino al gusto",
                "2 cucharadas aceite",
                "Agua suficiente"
            )
        ),

        MealDbFullRecipe(
            id = "col_009",
            name = "Cazuela de Mariscos",
            category = "Almuerzo",
            area = "Colombia",
            thumbUrl = "",
            instructions = """
Sofríe en mantequilla la cebolla cabezona picada, el ajo y el pimentón durante 8 minutos.
Agrega los camarones, los calamares y los mejillones. Cocina 5 minutos a fuego alto.
Vierte la leche de coco y el caldo de pescado. Lleva a hervor suave.
Añade el tomate picado, el cilantro y el ají. Sazona con sal y pimienta.
Cocina a fuego bajo 15 minutos hasta que los mariscos estén tiernos y la salsa espese.
Sirve en cazuela de barro con arroz blanco y patacones.
            """.trimIndent(),
            ingredients = listOf(
                "300 g camarones pelados",
                "200 g anillos de calamar",
                "200 g mejillones limpios",
                "400 ml leche de coco",
                "1 taza caldo de pescado",
                "1 cebolla cabezona",
                "1 pimentón rojo",
                "3 dientes de ajo",
                "2 tomates",
                "Cilantro y ají al gusto",
                "Sal y pimienta al gusto",
                "2 cucharadas mantequilla"
            )
        ),

        MealDbFullRecipe(
            id = "col_010",
            name = "Tamales Tolimenses",
            category = "Almuerzo",
            area = "Colombia",
            thumbUrl = "",
            instructions = """
Prepara la masa: mezcla la harina de maíz precocida con el caldo de pollo tibio, manteca y sal hasta obtener una masa suave y manejable.
Cocina las presas de pollo y el cerdo por separado en agua con sal y especias hasta que estén tiernos.
Prepara el guiso: sofríe tomate, cebolla, ajo y pimentón en aceite. Agrega las papas peladas y los guisantes.
Remoja las hojas de plátano en agua caliente para que sean flexibles.
Extiende una porción de masa en la hoja de plátano. Coloca encima pollo, cerdo, un trozo de papa, guisantes, zanahoria y un poco del guiso.
Dobla la hoja para cerrar el tamal y amarra bien con pita.
Cocina los tamales en olla con agua hirviendo durante 1 hora.
Sirve caliente directamente en la hoja.
            """.trimIndent(),
            ingredients = listOf(
                "3 tazas harina de maíz precocida",
                "500 g pollo en presas",
                "300 g costilla de cerdo",
                "3 papas",
                "1 taza guisantes (arvejas)",
                "2 zanahorias",
                "2 tomates",
                "1 cebolla cabezona",
                "Hojas de plátano",
                "Manteca de cerdo",
                "Caldo de pollo",
                "Sal, comino y pimienta al gusto"
            )
        ),

        MealDbFullRecipe(
            id = "col_011",
            name = "Sopa de Mondongo",
            category = "Almuerzo",
            area = "Colombia",
            thumbUrl = "",
            instructions = """
Limpia bien el mondongo (panza de res) frotándolo con limón y sal. Lava varias veces con agua fría.
Cocina el mondongo en agua con ajo, cebolla y sal en olla a presión durante 40 minutos.
Retira y corta en tiras finas. Reserva el caldo.
En una olla grande sofríe cebolla larga, tomate, ajo y hogao. Agrega el mondongo y el caldo colado.
Incorpora las papas peladas y troceadas, el plátano verde, la yuca y el maíz.
Cocina a fuego medio 30 minutos más hasta que todo esté tierno.
Sazona con sal, comino y pimienta. Sirve con cilantro picado y arroz blanco.
            """.trimIndent(),
            ingredients = listOf(
                "700 g mondongo (panza de res)",
                "3 papas medianas",
                "1 plátano verde",
                "200 g yuca",
                "1 mazorca de maíz",
                "3 tomates",
                "Cebolla larga y cabezona",
                "3 dientes de ajo",
                "Cilantro al gusto",
                "Sal, comino y pimienta al gusto",
                "2 limones (para limpiar)"
            )
        ),

        MealDbFullRecipe(
            id = "col_012",
            name = "Arroz con Leche",
            category = "Postre",
            area = "Colombia",
            thumbUrl = "",
            instructions = """
Lava el arroz y ponlo a cocinar en agua hasta que ablande, unos 15 minutos.
Cuando el agua se haya consumido, agrega la leche entera poco a poco revolviendo constantemente.
Añade el azúcar, la canela en rama, la ralladura de limón y los clavos de olor.
Cocina a fuego bajo revolviendo continuamente para que no se pegue, durante 20 minutos.
Cuando el arroz esté cremoso y espeso retira la canela y los clavos.
Sirve en platos hondos o vasitos. Espolvorea canela molida por encima.
Puede servirse caliente o frío.
            """.trimIndent(),
            ingredients = listOf(
                "1 taza arroz",
                "1 litro leche entera",
                "3/4 taza azúcar",
                "1 rama de canela",
                "3 clavos de olor",
                "Ralladura de 1 limón",
                "Canela molida para decorar",
                "2 tazas agua"
            )
        ),

        MealDbFullRecipe(
            id = "col_013",
            name = "Buñuelos Colombianos",
            category = "Postre",
            area = "Colombia",
            thumbUrl = "",
            instructions = """
Mezcla el queso costeño rallado, la harina de maíz, el almidón de yuca, el huevo, la sal y el azúcar.
Amasa hasta obtener una mezcla homogénea. Si queda muy seca agrega un poco de leche.
Forma bolitas del tamaño de una pelota de ping-pong, asegurándote de que queden lisas y sin grietas.
Calienta abundante aceite en una sartén profunda a fuego medio (170°C).
Fríe las bolitas en el aceite caliente. Inicialmente se hundirán, luego flotarán y se hincharán.
Cocina durante 6-8 minutos volteándolas suavemente hasta que estén doradas y crujientes por fuera.
Escurre sobre papel absorbente y sirve calientes.
            """.trimIndent(),
            ingredients = listOf(
                "250 g queso costeño rallado",
                "1 taza almidón de yuca",
                "1/2 taza harina de maíz",
                "1 huevo",
                "1 cucharadita azúcar",
                "Pizca de sal",
                "Leche si es necesario",
                "Aceite para freír"
            )
        ),

        MealDbFullRecipe(
            id = "col_014",
            name = "Patacones con Hogao",
            category = "Snack",
            area = "Colombia",
            thumbUrl = "",
            instructions = """
Pela los plátanos verdes y córtalos en rodajas gruesas de 2 cm.
Fríe en aceite caliente por 3 minutos hasta que estén levemente dorados pero no completamente cocidos.
Saca las rodajas y aplánalas con un tostonero o la base de un vaso hasta que queden discos delgados.
Vuelve a freír los discos en aceite caliente durante 3 minutos más hasta que queden crujientes y dorados.
Escurre sobre papel absorbente y sazóna con sal.
Prepara el hogao: sofríe tomate y cebolla larga picados en aceite durante 15 minutos a fuego medio.
Sirve los patacones calientes con el hogao encima y queso blanco rallado.
            """.trimIndent(),
            ingredients = listOf(
                "2 plátanos verdes",
                "3 tomates",
                "3 tallos cebolla larga",
                "100 g queso blanco",
                "Aceite para freír",
                "Sal al gusto"
            )
        ),

        MealDbFullRecipe(
            id = "col_015",
            name = "Fritanga Colombiana",
            category = "Almuerzo",
            area = "Colombia",
            thumbUrl = "",
            instructions = """
Sazona las costillas de cerdo con sal, pimienta y ajo molido. Deja marinar 1 hora.
Cocina las costillas en agua con sal y laurel por 30 minutos. Luego termínalas a la parrilla o en sartén caliente.
Fríe los chorizos en sartén a fuego medio hasta que estén bien dorados.
Prepara el chicharrón: fríe la piel de cerdo salada en aceite bien caliente hasta que quede inflada y crujiente.
Fríe los plátanos maduros en rodajas hasta dorar.
Fríe las papitas criollas con cáscara hasta que estén doradas.
Sirve todo junto en una bandeja grande con ensalada de tomate y cebolla, limón y ají.
            """.trimIndent(),
            ingredients = listOf(
                "400 g costillas de cerdo",
                "2 chorizos antioqueños",
                "150 g chicharrón",
                "2 plátanos maduros",
                "300 g papitas criollas",
                "2 tomates para ensalada",
                "1 cebolla cabezona",
                "Limón y ají al gusto",
                "Sal, pimienta y ajo al gusto"
            )
        )
    )

    /** Filtra por nombre/categoría — búsqueda local instantánea */
    fun search(query: String): List<MealDbFullRecipe> {
        if (query.isBlank()) return recipes
        val q = query.lowercase().trim()
        return recipes.filter { r ->
            r.name.lowercase().contains(q) ||
            r.category.lowercase().contains(q) ||
            r.ingredients.any { it.lowercase().contains(q) }
        }
    }
}
