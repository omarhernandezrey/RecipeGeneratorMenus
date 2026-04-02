package com.example.recipe_generator.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.recipe_generator.MainActivity
import com.example.recipe_generator.R
import com.example.recipe_generator.data.legacy.getMenuForDay
import java.util.Calendar

/**
 * AppWidget — RecipeWidgetProvider.
 *
 * Muestra la receta del día en el escritorio del dispositivo.
 * Usa RemoteViews para actualizar la UI del widget (sin Compose, compatible con AppWidget).
 *
 * Cumple LF7 completo: AppWidgetProvider nativo Android.
 * F3-21: AppWidgetProvider + receta del día.
 * F3-22: widget_recipe.xml — RemoteViews: TextView + Button.
 * F3-23: recipe_widget_info.xml — appwidget-provider config.
 * F3-24: onUpdate() con Intent + PendingIntent + RemoteViews + setOnClickPendingIntent.
 * F3-25: Declarado en AndroidManifest.xml como <receiver>.
 *
 * Capa: Widget (infraestructura Android)
 */
class RecipeWidgetProvider : AppWidgetProvider() {

    // F3-24: onUpdate() — actualiza todos los widgets activos
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateWidget(context, appWidgetManager, appWidgetId)
        }
    }

    private fun updateWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        // Obtener el día actual
        val dayOfWeek = getCurrentDayInSpanish()

        // Obtener la primera receta del día (receta principal = almuerzo)
        val recipesForDay = getMenuForDay(dayOfWeek)
        val featuredRecipe = recipesForDay.firstOrNull { it.category == "Almuerzo" }
            ?: recipesForDay.firstOrNull()

        // F3-24: Intent + PendingIntent para abrir MainActivity al hacer clic
        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // F3-24: RemoteViews — actualiza UI del widget con los datos de la receta
        val views = RemoteViews(context.packageName, R.layout.widget_recipe).apply {
            if (featuredRecipe != null) {
                setTextViewText(R.id.widget_day_label, "RECETA DEL $dayOfWeek".uppercase())
                setTextViewText(R.id.widget_recipe_title, featuredRecipe.title)
                setTextViewText(R.id.widget_recipe_time, "⏱ ${featuredRecipe.timeInMinutes} min")
                setTextViewText(R.id.widget_recipe_calories, "🔥 ${featuredRecipe.calories} kcal")
            } else {
                setTextViewText(R.id.widget_day_label, "RECIPE GENERATOR")
                setTextViewText(R.id.widget_recipe_title, "Abre la app para ver el menú del día")
                setTextViewText(R.id.widget_recipe_time, "")
                setTextViewText(R.id.widget_recipe_calories, "")
            }

            // F3-24: setOnClickPendingIntent — toque en botón abre la app
            setOnClickPendingIntent(R.id.widget_open_button, pendingIntent)
            setOnClickPendingIntent(R.id.widget_recipe_title, pendingIntent)
        }

        // Aplicar el RemoteViews al widget
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    /** Convierte el día de la semana actual al formato del menú en español. */
    private fun getCurrentDayInSpanish(): String {
        return when (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> "Lunes"
            Calendar.TUESDAY -> "Martes"
            Calendar.WEDNESDAY -> "Miércoles"
            Calendar.THURSDAY -> "Jueves"
            Calendar.FRIDAY -> "Viernes"
            Calendar.SATURDAY -> "Sábado"
            Calendar.SUNDAY -> "Domingo"
            else -> "Lunes"
        }
    }
}
