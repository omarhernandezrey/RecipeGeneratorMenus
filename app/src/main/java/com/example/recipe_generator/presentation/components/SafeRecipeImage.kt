package com.example.recipe_generator.presentation.components

import android.net.Uri
import android.util.Log
import java.util.Locale

private const val IMG_DEBUG_TAG = "IMG_DEBUG"

fun getImagenSegura(nombre: String, url: String?): String {
    val candidate = url?.trim()
    val finalUrl = if (!candidate.isNullOrBlank() && isValidHttpUrl(candidate)) {
        candidate
    } else {
        generarFallback(nombre)
    }
    Log.d(IMG_DEBUG_TAG, "Receta: $nombre -> urlFinal: $finalUrl")
    return finalUrl
}

fun generarFallback(nombre: String): String {
    return generarFallbackSeguro(nombre)
}

fun generarFallbackSeguro(nombre: String): String {
    val query = nombre
        .trim()
        .lowercase(Locale.ROOT)
        .replace("\\s+".toRegex(), "+")
    return "https://via.placeholder.com/600x400.png?text=${Uri.encode(query)}"
}

private fun isValidHttpUrl(url: String): Boolean {
    return url.startsWith("https://") || url.startsWith("http://")
}
