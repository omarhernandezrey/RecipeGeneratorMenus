package com.example.recipe_generator.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context

/**
 * AppWidget — RecipeWidgetProvider.
 *
 * Muestra la receta del día en el escritorio del dispositivo.
 * Usa RemoteViews para actualizar la UI del widget.
 *
 * Implementación completa en F3-21 a F3-25.
 * Declaración en AndroidManifest.xml en F3-25.
 *
 * Cubre LF7 completo: AppWidgetProvider
 */
class RecipeWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // F3-24: Intent + PendingIntent + RemoteViews + setOnClickPendingIntent
        for (appWidgetId in appWidgetIds) {
            updateWidget(context, appWidgetManager, appWidgetId)
        }
    }

    private fun updateWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        // Implementación en F3-24
    }
}
