package com.example.recipe_generator.presentation.profile

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

@Composable
internal fun rememberProfileImage(photoRef: String?): ImageBitmap? {
    val context = LocalContext.current
    val bitmap = produceState<ImageBitmap?>(initialValue = null, photoRef) {
        value = loadProfileImage(context, photoRef)
    }
    return bitmap.value
}

internal suspend fun loadProfileImage(
    context: Context,
    photoRef: String?
): ImageBitmap? {
    if (photoRef.isNullOrBlank()) return null

    return withContext(Dispatchers.IO) {
        runCatching {
            val uri = Uri.parse(photoRef)
            when (uri.scheme?.lowercase()) {
                "content" -> {
                    context.contentResolver.openInputStream(uri)?.use { stream ->
                        BitmapFactory.decodeStream(stream)?.asImageBitmap()
                    }
                }

                "file" -> BitmapFactory.decodeFile(uri.path)?.asImageBitmap()

                "http", "https" -> {
                    val connection = (URL(photoRef).openConnection() as HttpURLConnection).apply {
                        connectTimeout = 5_000
                        readTimeout = 5_000
                        instanceFollowRedirects = true
                        doInput = true
                    }

                    connection.inputStream.use { stream ->
                        BitmapFactory.decodeStream(stream)?.asImageBitmap()
                    }
                }

                else -> {
                    BitmapFactory.decodeFile(photoRef)?.asImageBitmap()
                }
            }
        }.getOrNull()
    }
}

/**
 * Guarda un Bitmap en filesDir con el nombre dado (sin extensión).
 * Ej.: fileName = "profile_photo_uid123"  →  filesDir/profile_photo_uid123.jpg
 * filesDir NO se borra al cerrar la app; sólo al desinstalar o limpiar datos.
 */
internal suspend fun saveBitmapToInternalStorage(context: Context, fileName: String, bitmap: Bitmap): String =
    withContext(Dispatchers.IO) {
        val outputFile = File(context.filesDir, "$fileName.jpg")
        FileOutputStream(outputFile).use { output ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 92, output)
        }
        Uri.fromFile(outputFile).toString()
    }

/**
 * Copia un content:// URI a filesDir con el nombre dado (sin extensión).
 * Los content:// URIs del selector de imágenes pierden su acceso al reiniciar
 * el proceso de la app; copiar el contenido evita ese problema.
 * Devuelve null si la copia falla.
 */
internal suspend fun copyContentUriToInternalStorage(context: Context, fileName: String, contentUri: Uri): String? =
    withContext(Dispatchers.IO) {
        runCatching {
            val outputFile = File(context.filesDir, "$fileName.jpg")
            context.contentResolver.openInputStream(contentUri)?.use { input ->
                FileOutputStream(outputFile).use { output ->
                    input.copyTo(output)
                }
            }
            Uri.fromFile(outputFile).toString()
        }.getOrNull()
    }
