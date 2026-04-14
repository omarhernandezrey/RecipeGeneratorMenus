package com.example.recipe_generator.presentation.detail.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast

fun openRecipeVideo(context: Context, videoUrl: String) {
    if (videoUrl.isBlank()) {
        Toast.makeText(context, "Video no disponible", Toast.LENGTH_SHORT).show()
        return
    }

    try {
        val appIntent = Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl)).apply {
            setPackage("com.google.android.youtube")
        }
        context.startActivity(appIntent)
    } catch (appError: Exception) {
        Log.w("RecipeVideoIntent", "No se pudo abrir en app YouTube: ${appError.message}")
        try {
            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl))
            context.startActivity(webIntent)
        } catch (webError: Exception) {
            Log.e("RecipeVideoIntent", "No se pudo abrir video: ${webError.message}")
            Toast.makeText(context, "No se pudo abrir el video", Toast.LENGTH_SHORT).show()
        }
    }
}

