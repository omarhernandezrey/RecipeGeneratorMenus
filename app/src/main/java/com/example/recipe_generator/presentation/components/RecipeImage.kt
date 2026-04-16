package com.example.recipe_generator.presentation.components

import android.net.Uri
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
 *
 * Lógica de resolución (en orden):
 *  1. Si [imageRes] ya es una URL http/https → carga directamente.
 *  2. Si [imageRes] es un path local file:// → carga desde disco.
 *  3. Si está vacío → busca automáticamente con [RecipeImageResolver] (MealDB + Pixabay).
 *  4. Si nada funciona → placeholder.
 */
@Composable
fun RecipeImage(
    recipeTitle: String,
    imageRes: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    var resolvedUrl by remember(recipeTitle, imageRes) { mutableStateOf(imageRes) }
    var isLoading by remember(recipeTitle, imageRes) { mutableStateOf(imageRes.isBlank()) }

    LaunchedEffect(recipeTitle, imageRes) {
        resolvedUrl = imageRes
        val shouldResolveOnline = imageRes.isBlank() || !imageRes.isDirectImageReference()
        if (shouldResolveOnline) {
            isLoading = true
            val found = RecipeImageResolver.resolve(recipeTitle, imageRes)
            resolvedUrl = found
            isLoading = false
        } else {
            isLoading = false
        }
    }

    Box(modifier = modifier) {
        when {
            resolvedUrl.isRemoteImageUrl() -> {
                AsyncImage(
                    model = resolvedUrl,
                    contentDescription = recipeTitle,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = contentScale,
                    placeholder = painterResource(R.drawable.img_placeholder),
                    error = painterResource(R.drawable.img_placeholder)
                )
            }
            resolvedUrl.startsWith("content://") -> {
                AsyncImage(
                    model = Uri.parse(resolvedUrl),
                    contentDescription = recipeTitle,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = contentScale,
                    placeholder = painterResource(R.drawable.img_placeholder),
                    error = painterResource(R.drawable.img_placeholder)
                )
            }
            resolvedUrl.startsWith("file://") || resolvedUrl.startsWith("/") || File(resolvedUrl).exists() -> {
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

private fun String.isRemoteImageUrl(): Boolean =
    startsWith("http://") || startsWith("https://")

private fun String.isDirectImageReference(): Boolean =
    isRemoteImageUrl() ||
        startsWith("file://") ||
        startsWith("content://") ||
        startsWith("/") ||
        File(this).exists()
