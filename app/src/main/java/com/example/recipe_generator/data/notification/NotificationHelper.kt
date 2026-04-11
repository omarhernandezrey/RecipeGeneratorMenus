package com.example.recipe_generator.data.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.recipe_generator.R
import java.util.concurrent.TimeUnit

/**
 * Helper para notificaciones locales del sistema.
 *
 * Canal: "recetas_channel" (importancia DEFAULT).
 * Auto-cancelación a las 24 horas vía setTimeoutAfter().
 *
 * Capa: Data
 */
object NotificationHelper {

    private const val CHANNEL_ID   = "recetas_channel"
    private const val CHANNEL_NAME = "Recetas & Menús"
    private const val ID_PLAN      = 2001
    private val ONE_DAY_MS = TimeUnit.DAYS.toMillis(1)

    /** Debe llamarse en Application.onCreate() */
    fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Avisos cuando generas un menú o guardas una receta"
            }
            context.getSystemService(NotificationManager::class.java)
                ?.createNotificationChannel(channel)
        }
    }

    /**
     * Notificación expandida cuando se genera el plan semanal.
     * Lista cuántos slots se llenaron por tipo de comida.
     */
    fun showPlanGeneratedNotification(
        context: Context,
        hasBreakfasts: Boolean,
        hasLunches: Boolean,
        hasDinners: Boolean
    ) {
        val lines = mutableListOf<String>()
        if (hasBreakfasts) lines += "🌅 Desayuno · 7 días planificados"
        if (hasLunches)    lines += "☀️ Almuerzo · 7 días planificados"
        if (hasDinners)    lines += "🌙 Cena · 7 días planificados"
        if (lines.isEmpty()) return

        val style = NotificationCompat.InboxStyle()
            .setBigContentTitle("🍽️ ¡Plan semanal generado!")
        lines.forEach { style.addLine(it) }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.img_food_1)
            .setContentTitle("🍽️ ¡Plan semanal generado!")
            .setContentText("Tu menú para los próximos 7 días está listo")
            .setStyle(style)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setTimeoutAfter(ONE_DAY_MS)
            .build()

        postSafely(context, ID_PLAN, notification)
    }

    /**
     * Notificación cuando el usuario guarda una receta personal.
     */
    fun showRecipeCreatedNotification(context: Context, recipeName: String) {
        val notifId = 3000 + (recipeName.hashCode() and 0xFFFF)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.img_food_1)
            .setContentTitle("📖 Receta guardada")
            .setContentText("\"$recipeName\" ya está en Mis Recetas")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setTimeoutAfter(ONE_DAY_MS)
            .build()

        postSafely(context, notifId, notification)
    }

    private fun postSafely(context: Context, id: Int, notification: android.app.Notification) {
        try {
            NotificationManagerCompat.from(context).notify(id, notification)
        } catch (_: SecurityException) {
            // POST_NOTIFICATIONS no concedido — silencioso
        }
    }
}
