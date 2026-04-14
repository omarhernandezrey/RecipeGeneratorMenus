package com.example.recipe_generator.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.recipe_generator.R
import com.example.recipe_generator.data.remote.RecipeImageResolver
import com.example.recipe_generator.presentation.theme.PrimaryContainer
import java.io.File

/**
 * Composable compartido para mostrar imágenes de recetas en cualquier pantalla.
 * F-Images: Prioriza imageUrl persistida para evitar imágenes aleatorias/chistosas.
 */
@Composable
fun RecipeImage(
    recipeTitle: String,
    imageRes: String,
    imageUrl: String? = null, // NUEVO: Prioridad absoluta
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    // Prioridad: 1. imageUrl (Nube/IA) -> 2. imageRes (Local)
    val initialUrl = when {
        !imageUrl.isNullOrBlank() -> imageUrl
        imageRes.startsWith("http") -> imageRes
        else -> ""
    }

    var resolvedUrl by remember(recipeTitle, imageRes, imageUrl) { mutableStateOf(initialUrl) }
    var isLoading by remember(recipeTitle, imageRes, imageUrl) { mutableStateOf(resolvedUrl.isBlank()) }

    LaunchedEffect(recipeTitle, imageRes, imageUrl) {
        if (resolvedUrl.isBlank()) {
            isLoading = true
            // Si no hay imagen guardada, buscamos una profesional (RecipeImageResolver ya tiene los filtros nuevos)
            val found = RecipeImageResolver.resolve(recipeTitle, imageRes)
            resolvedUrl = found
            isLoading = false
        }
    }

    Box(modifier = modifier) {
        when {
            resolvedUrl.startsWith("http") -> {
                AsyncImage(
                    model = getImagenSegura(recipeTitle, resolvedUrl),
                    contentDescription = recipeTitle,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = contentScale,
                    placeholder = painterResource(R.drawable.img_placeholder),
                    error = painterResource(R.drawable.img_placeholder)
                )
            }
            resolvedUrl.startsWith("file://") || resolvedUrl.startsWith("/") -> {
                val file = if (resolvedUrl.startsWith("file://"))
                    File(resolvedUrl.removePrefix("file://"))
                else
                    File(resolvedUrl)
                AsyncImage(
                    model = file,
                    contentDescription = recipeTitle,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = contentScale,
                    placeholder = painterResource(R.drawable.img_placeholder),
                    error = painterResource(R.drawable.img_placeholder)
                )
            }
            else -> {
                androidx.compose.foundation.Image(
                    painter = painterResource(R.drawable.img_placeholder),
                    contentDescription = recipeTitle,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = contentScale
                )
            }
        }

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(PrimaryContainer.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(32.dp),
                    strokeWidth = 2.dp
                )
            }
        }
    }
}
