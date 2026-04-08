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
import java.util.UUID

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

internal suspend fun saveBitmapToCache(context: Context, bitmap: Bitmap): String =
    withContext(Dispatchers.IO) {
        val outputFile = File(context.cacheDir, "profile_${UUID.randomUUID()}.jpg")
        FileOutputStream(outputFile).use { output ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 92, output)
        }
        Uri.fromFile(outputFile).toString()
    }
