@file:Suppress("SpellCheckingInspection")

package com.example.recipe_generator

import android.app.Application
import com.example.recipe_generator.data.legacy.getAllRecipesAsDomainModel
import com.example.recipe_generator.di.AppContainer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Application class — punto de entrada global de la app.
 *
 * Responsabilidades:
 * - Inicializar el contenedor de dependencias (AppContainer)
 * - Sembrar la BD Room con datos legacy si está vacía (F2-20)
 *
 * Capa: Raíz (por encima de Presentation)
 */
class RecipeGeneratorApp : Application() {

    /**
     * Contenedor de dependencias manual (sin Hilt).
     * Accedido desde Activities y Fragments vía:
     *   (application as RecipeGeneratorApp).container
     */
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        seedDatabaseIfEmpty()
        com.example.recipe_generator.data.notification.NotificationHelper.createChannel(this)
    }

    /** Siembra Room con las 21 recetas legacy si la tabla está vacía. */
    private fun seedDatabaseIfEmpty() {
        CoroutineScope(Dispatchers.IO).launch {
            if (container.recipeRepository.count() == 0) {
                container.recipeRepository.insertAll(getAllRecipesAsDomainModel())
            }
        }
    }
}
