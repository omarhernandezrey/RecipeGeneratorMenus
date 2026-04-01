package com.example.recipe_generator

import android.app.Application
import com.example.recipe_generator.di.AppContainer

/**
 * Application class — punto de entrada global de la app.
 *
 * Responsabilidades:
 * - Inicializar el contenedor de dependencias (AppContainer)
 * - Exponer dependencias compartidas al resto de capas
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
    }
}
