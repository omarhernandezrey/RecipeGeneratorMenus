package com.example.recipe_generator

import com.example.recipe_generator.data.repository.RecipeRepositoryImpl
import com.example.recipe_generator.domain.usecase.GenerateMenuUseCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * F3-37: Test unitario GenerateMenuUseCase — lógica de filtrado por dificultad/tipo.
 *
 * Verifica que el caso de uso filtra correctamente las recetas según los parámetros.
 * Firma: invoke(maxDifficulty, selectedTypes, selectedDiets)
 */
class GenerateMenuUseCaseTest {

    private lateinit var generateMenuUseCase: GenerateMenuUseCase

    @Before
    fun setup() {
        val repository = RecipeRepositoryImpl()
        generateMenuUseCase = GenerateMenuUseCase(repository)
    }

    @Test
    fun `invoke sin filtros retorna todas las recetas`() = runTest {
        val result = generateMenuUseCase(
            maxDifficulty = "Difícil",
            selectedTypes = emptySet(),
            selectedDiets = emptySet()
        ).first()
        assertTrue("Sin filtros debe retornar recetas", result.isNotEmpty())
    }

    @Test
    fun `invoke con dificultad Fácil solo retorna recetas fáciles`() = runTest {
        val result = generateMenuUseCase(
            maxDifficulty = "Fácil",
            selectedTypes = emptySet(),
            selectedDiets = emptySet()
        ).first()
        assertTrue("Resultado no debe estar vacío", result.isNotEmpty())
        assertTrue(
            "Todas las recetas deben ser Fácil",
            result.all { it.difficulty == "Fácil" }
        )
    }

    @Test
    fun `invoke con dificultad Medio retorna Fácil y Medio`() = runTest {
        val result = generateMenuUseCase(
            maxDifficulty = "Medio",
            selectedTypes = emptySet(),
            selectedDiets = emptySet()
        ).first()
        assertTrue("Resultado no debe estar vacío", result.isNotEmpty())
        assertTrue(
            "Todas las recetas deben ser Fácil o Medio",
            result.all { it.difficulty == "Fácil" || it.difficulty == "Medio" }
        )
        assertTrue(
            "NO debe contener recetas Difícil",
            result.none { it.difficulty == "Difícil" }
        )
    }

    @Test
    fun `invoke filtra por tipo Desayuno correctamente`() = runTest {
        val result = generateMenuUseCase(
            maxDifficulty = "Difícil",
            selectedTypes = setOf("Desayunos"),
            selectedDiets = emptySet()
        ).first()
        if (result.isNotEmpty()) {
            assertTrue(
                "Resultados filtrados por Desayuno deben contener 'Desayuno'",
                result.all { it.category.contains("Desayuno", ignoreCase = true) ||
                        "Desayunos".contains(it.category, ignoreCase = true) }
            )
        }
    }

    @Test
    fun `invoke con Difícil retorna más o igual recetas que con Fácil`() = runTest {
        val facilResult = generateMenuUseCase("Fácil", emptySet(), emptySet()).first()
        val dificilResult = generateMenuUseCase("Difícil", emptySet(), emptySet()).first()
        assertTrue(
            "Con dificultad mayor debe haber más o igual recetas",
            dificilResult.size >= facilResult.size
        )
    }

    @Test
    fun `invoke resultado es consistente para mismos parámetros`() = runTest {
        val result1 = generateMenuUseCase("Medio", emptySet(), emptySet()).first()
        val result2 = generateMenuUseCase("Medio", emptySet(), emptySet()).first()
        assertTrue(
            "Mismos parámetros deben dar mismo resultado",
            result1.map { it.id }.toSet() == result2.map { it.id }.toSet()
        )
    }
}
