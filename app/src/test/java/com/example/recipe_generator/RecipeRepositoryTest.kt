package com.example.recipe_generator

import com.example.recipe_generator.data.legacy.getMenuForDayAsDomainModel
import com.example.recipe_generator.data.repository.RecipeRepositoryImpl
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * F3-36: Test unitario RecipeRepository — CRUD + filtros + favoritos.
 *
 * Usa RecipeRepositoryImpl con datos en memoria (sin Room para tests unitarios).
 * Verifica que getAllRecipes, getRecipesByDay, getRecipeById funcionan correctamente.
 */
class RecipeRepositoryTest {

    private lateinit var repository: RecipeRepositoryImpl

    @Before
    fun setup() {
        repository = RecipeRepositoryImpl()
    }

    @Test
    fun `getAllRecipes retorna lista no vacía`() = runTest {
        val recipes = repository.getAllRecipes().first()
        assertTrue("La lista de recetas no debe estar vacía", recipes.isNotEmpty())
    }

    @Test
    fun `getAllRecipes contiene al menos 21 recetas (7 días x 3 comidas)`() = runTest {
        val recipes = repository.getAllRecipes().first()
        assertTrue(
            "Debe haber al menos 21 recetas, hay ${recipes.size}",
            recipes.size >= 21
        )
    }

    @Test
    fun `getRecipesByDay retorna recetas del día correcto`() = runTest {
        val lunesRecipes = repository.getRecipesByDay("Lunes").first()
        assertTrue("Lunes debe tener recetas", lunesRecipes.isNotEmpty())
        assertTrue(
            "Todas las recetas deben ser del día Lunes",
            lunesRecipes.all { it.dayOfWeek == "Lunes" }
        )
    }

    @Test
    fun `getRecipesByDay retorna 3 comidas por día`() = runTest {
        val dias = listOf("Lunes", "Martes", "Miércoles")
        for (dia in dias) {
            val recipes = repository.getRecipesByDay(dia).first()
            assertEquals(
                "El día $dia debe tener exactamente 3 recetas",
                3,
                recipes.size
            )
        }
    }

    @Test
    fun `getRecipeById retorna la receta correcta`() = runTest {
        val recipe = repository.getRecipeById("1").first()
        assertNotNull("La receta con ID '1' debe existir", recipe)
        assertEquals("El ID debe coincidir", "1", recipe?.id)
    }

    @Test
    fun `getRecipeById retorna null para ID inexistente`() = runTest {
        val recipe = repository.getRecipeById("ID_INEXISTENTE_XYZ").first()
        assertEquals("ID inexistente debe retornar null", null, recipe)
    }

    @Test
    fun `searchRecipes filtra por título correctamente`() = runTest {
        val results = repository.searchRecipes("Quinoa").first()
        assertTrue("Búsqueda de Quinoa debe retornar resultados", results.isNotEmpty())
        assertTrue(
            "Resultados deben contener 'quinoa' en título o descripción",
            results.all { recipe ->
                recipe.title.contains("Quinoa", ignoreCase = true) ||
                        recipe.description.contains("Quinoa", ignoreCase = true)
            }
        )
    }

    @Test
    fun `searchRecipes retorna lista vacía para búsqueda sin resultados`() = runTest {
        val results = repository.searchRecipes("xyz_no_existe_123").first()
        assertTrue("Búsqueda sin resultados debe retornar lista vacía", results.isEmpty())
    }

    @Test
    fun `getRecipesByCategory filtra correctamente`() = runTest {
        val desayunos = repository.getRecipesByCategory("Desayuno").first()
        assertTrue("Debe haber desayunos", desayunos.isNotEmpty())
        assertTrue(
            "Todas deben ser desayunos",
            desayunos.all { it.category.equals("Desayuno", ignoreCase = true) }
        )
    }

    @Test
    fun `count retorna número correcto de recetas`() = runTest {
        val count = repository.count()
        assertTrue("El conteo debe ser mayor que 0", count > 0)
        val allRecipes = repository.getAllRecipes().first()
        assertEquals("El conteo debe coincidir con getAllRecipes", allRecipes.size, count)
    }

    @Test
    fun `insertAll reemplaza las recetas existentes`() = runTest {
        val nuevasRecetas = getMenuForDayAsDomainModel("Lunes")
        repository.insertAll(nuevasRecetas)
        val count = repository.count()
        assertEquals("Debe haber solo las recetas insertadas", nuevasRecetas.size, count)
    }
}
