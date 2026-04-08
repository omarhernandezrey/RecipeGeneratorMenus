package com.example.recipe_generator

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.recipe_generator.data.local.AppDatabase
import com.example.recipe_generator.data.repository.UserRecipeRepositoryImpl
import com.example.recipe_generator.data.repository.WeeklyPlanRepositoryImpl
import com.example.recipe_generator.domain.model.UserRecipe
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * C-14: Test de integración Room para el flujo:
 * crear receta -> aparece en "Mis Recetas"
 * asignar a día -> aparece en plan semanal
 * eliminar receta -> desaparece de ambos
 *
 * Usa AppDatabase en memoria para validar el comportamiento real
 * de UserRecipeRepository + WeeklyPlanRepository.
 */
@RunWith(AndroidJUnit4::class)
class UserRecipeWeeklyPlanIntegrationTest {

    private lateinit var database: AppDatabase
    private lateinit var userRecipeRepository: UserRecipeRepositoryImpl
    private lateinit var weeklyPlanRepository: WeeklyPlanRepositoryImpl

    private val userId = "test_user_c14"

    @Before
    fun setup() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        userRecipeRepository = UserRecipeRepositoryImpl(
            userRecipeDao = database.userRecipeDao(),
            weeklyPlanDao = database.weeklyPlanDao()
        )

        weeklyPlanRepository = WeeklyPlanRepositoryImpl(
            weeklyPlanDao = database.weeklyPlanDao(),
            userRecipeDao = database.userRecipeDao(),
            recipeDao = database.recipeDao()
        )
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun createAssignDeleteRecipe_updatesMyRecipesAndWeeklyPlan() = runTest {
        val recipe = UserRecipe(
            id = "user_recipe_c14",
            userId = userId,
            title = "Bowl Proteico",
            category = "Desayuno",
            description = "Receta de prueba para integración",
            dayOfWeek = "Lunes",
            mealType = "Desayuno",
            ingredients = listOf("Avena", "Yogurt"),
            steps = listOf("Mezclar", "Servir")
        )

        userRecipeRepository.addRecipe(recipe)

        val myRecipesAfterCreate = userRecipeRepository.getMyRecipes(userId).first()
        assertEquals("La receta creada debe aparecer en Mis Recetas", 1, myRecipesAfterCreate.size)
        assertEquals("Bowl Proteico", myRecipesAfterCreate.first().title)

        weeklyPlanRepository.setMeal(
            userId = userId,
            day = "Lunes",
            mealType = "Desayuno",
            recipeId = recipe.id
        )

        val weeklyPlanForMonday = weeklyPlanRepository.getDay(userId, "Lunes").first()
        assertEquals("Debe existir una comida asignada para el lunes", 1, weeklyPlanForMonday.size)
        assertEquals(recipe.id, weeklyPlanForMonday.first().recipeId)
        assertEquals("Bowl Proteico", weeklyPlanForMonday.first().recipeTitle)

        userRecipeRepository.deleteRecipe(recipe)

        val myRecipesAfterDelete = userRecipeRepository.getMyRecipes(userId).first()
        assertTrue("La receta eliminada debe desaparecer de Mis Recetas", myRecipesAfterDelete.isEmpty())

        val weeklyPlanAfterDelete = weeklyPlanRepository.getDay(userId, "Lunes").first()
        assertFalse(
            "La receta eliminada no debe seguir referenciada en el plan semanal",
            weeklyPlanAfterDelete.any { it.recipeId == recipe.id }
        )
        assertTrue(
            "La celda debe desaparecer del plan semanal cuando se elimina la receta",
            weeklyPlanAfterDelete.isEmpty()
        )
    }
}
