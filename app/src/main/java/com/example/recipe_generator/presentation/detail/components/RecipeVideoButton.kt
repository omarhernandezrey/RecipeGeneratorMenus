package com.example.recipe_generator.presentation.detail.components
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

sealed interface RecipeVideoUiState {
    data object Loading : RecipeVideoUiState
    data object Empty : RecipeVideoUiState
    data class Ready(
        val videoUrl: String,
        val fromFallback: Boolean = false
    ) : RecipeVideoUiState
    data class Error(
        val message: String,
        val fallbackUrl: String? = null
    ) : RecipeVideoUiState
}

@Composable
fun RecipeVideoButton(
    videoUrl: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Button(
        onClick = { openRecipeVideo(context, videoUrl) },
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFFF0000)
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Play video",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Ver cómo preparar",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
fun RecipeVideoSection(
    videoUiState: RecipeVideoUiState,
    modifier: Modifier = Modifier
) {
    when (videoUiState) {
        RecipeVideoUiState.Loading -> {
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color(0xFFFF0000))
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Buscando video...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        RecipeVideoUiState.Empty -> {
            Text(
                text = "Video no disponible por ahora",
                modifier = modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        is RecipeVideoUiState.Ready -> {
            Column(modifier = modifier.fillMaxWidth()) {
                RecipeVideoButton(videoUrl = videoUiState.videoUrl)
                if (videoUiState.fromFallback) {
                    Text(
                        text = "Mostrando búsqueda sugerida en YouTube",
                        modifier = Modifier.padding(horizontal = 16.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        is RecipeVideoUiState.Error -> {
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = videoUiState.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
                if (!videoUiState.fallbackUrl.isNullOrBlank()) {
                    RecipeVideoButton(videoUrl = videoUiState.fallbackUrl)
                }
            }
        }
    }
}
