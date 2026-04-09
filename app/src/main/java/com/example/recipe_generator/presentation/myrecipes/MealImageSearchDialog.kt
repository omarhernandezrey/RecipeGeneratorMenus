package com.example.recipe_generator.presentation.myrecipes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.recipe_generator.data.remote.MealDbApi
import com.example.recipe_generator.data.remote.MealImage
import com.example.recipe_generator.presentation.profile.downloadImageToInternalStorage
import com.example.recipe_generator.presentation.theme.OnSurface
import com.example.recipe_generator.presentation.theme.OnSurfaceVariant
import com.example.recipe_generator.presentation.theme.Primary
import com.example.recipe_generator.presentation.theme.SurfaceContainerLowest
import com.example.recipe_generator.presentation.theme.rounded_lg
import com.example.recipe_generator.presentation.theme.rounded_md
import com.example.recipe_generator.presentation.theme.spacing_2
import com.example.recipe_generator.presentation.theme.spacing_3
import com.example.recipe_generator.presentation.theme.spacing_4
import com.example.recipe_generator.presentation.theme.spacing_6
import kotlinx.coroutines.launch

/**
 * Diálogo de búsqueda de imágenes usando TheMealDB (sin API key).
 *
 * @param initialQuery  texto de búsqueda inicial (título de la receta)
 * @param recipeId      ID de la receta — usado como nombre del archivo local
 * @param onImageSelected  devuelve el file:// URI de la imagen guardada en filesDir
 * @param onDismiss     cierra el diálogo sin seleccionar
 */
@Composable
fun MealImageSearchDialog(
    initialQuery: String,
    recipeId: String,
    onImageSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var query       by remember { mutableStateOf(initialQuery) }
    var results     by remember { mutableStateOf<List<MealImage>>(emptyList()) }
    var isSearching by remember { mutableStateOf(false) }
    var isSaving    by remember { mutableStateOf(false) }
    var hasSearched by remember { mutableStateOf(false) }

    fun search() {
        if (query.isBlank()) return
        coroutineScope.launch {
            isSearching = true
            hasSearched = true
            results = MealDbApi.searchMeals(query)
            isSearching = false
        }
    }

    // Búsqueda automática al abrir si hay título
    LaunchedEffect(Unit) {
        if (initialQuery.isNotBlank()) search()
    }

    Dialog(onDismissRequest = { if (!isSaving) onDismiss() }) {
        Card(
            shape = RoundedCornerShape(rounded_lg),
            colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(spacing_6)) {

                Text(
                    text = "Buscar imagen",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = OnSurface
                )
                Text(
                    text = "Busca en inglés para mejores resultados.\nEj: pasta, chicken, salad",
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurfaceVariant
                )

                Spacer(modifier = Modifier.height(spacing_4))

                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = { Text("chicken, pasta, salad...") },
                    trailingIcon = {
                        IconButton(onClick = { search() }, enabled = !isSearching && !isSaving) {
                            Icon(Icons.Outlined.Search, contentDescription = "Buscar", tint = Primary)
                        }
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { search() }),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(spacing_4))

                // Área de resultados — altura fija para que el diálogo no salte
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        isSearching || isSaving -> {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(color = Primary, modifier = Modifier.size(36.dp))
                                Spacer(modifier = Modifier.height(spacing_3))
                                Text(
                                    text = if (isSaving) "Guardando imagen..." else "Buscando...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = OnSurfaceVariant
                                )
                            }
                        }

                        !hasSearched -> {
                            Text(
                                text = "Escribe el nombre de un plato\ny toca el ícono de búsqueda",
                                style = MaterialTheme.typography.bodyMedium,
                                color = OnSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }

                        results.isEmpty() -> {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Sin resultados",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = OnSurface
                                )
                                Spacer(modifier = Modifier.height(spacing_2))
                                Text(
                                    text = "Intenta en inglés: \"chicken\", \"pasta\", \"beef\"",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = OnSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        else -> {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(3),
                                horizontalArrangement = Arrangement.spacedBy(spacing_2),
                                verticalArrangement = Arrangement.spacedBy(spacing_2),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(results) { meal ->
                                    AsyncImage(
                                        model = meal.thumbUrl,
                                        contentDescription = meal.name,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .aspectRatio(1f)
                                            .clip(RoundedCornerShape(rounded_md))
                                            .background(Primary.copy(alpha = 0.08f))
                                            .clickable(enabled = !isSaving) {
                                                coroutineScope.launch {
                                                    isSaving = true
                                                    val path = downloadImageToInternalStorage(
                                                        context = context,
                                                        fileName = "recipe_image_$recipeId",
                                                        imageUrl = meal.thumbUrl
                                                    )
                                                    isSaving = false
                                                    if (path != null) onImageSelected(path)
                                                }
                                            }
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(spacing_3))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss, enabled = !isSaving) {
                        Text("Cancelar")
                    }
                }
            }
        }
    }
}
