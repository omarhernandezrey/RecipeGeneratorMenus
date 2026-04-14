package com.example.recipe_generator.presentation.ui

import android.widget.ImageView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bumptech.glide.Glide
import com.example.recipe_generator.R
import com.example.recipe_generator.RecipeGeneratorApp
import com.example.recipe_generator.domain.model.GlobalRecipe
import com.example.recipe_generator.domain.model.GlobalRecipeSource
import com.example.recipe_generator.presentation.components.EditorialCard
import com.example.recipe_generator.presentation.components.PrimaryButton
import com.example.recipe_generator.presentation.components.generarFallbackSeguro
import com.example.recipe_generator.presentation.components.getImagenSegura
import com.example.recipe_generator.presentation.theme.OnSurface
import com.example.recipe_generator.presentation.theme.OnSurfaceVariant
import com.example.recipe_generator.presentation.theme.Primary
import com.example.recipe_generator.presentation.theme.Surface
import com.example.recipe_generator.presentation.theme.spacing_2
import com.example.recipe_generator.presentation.theme.spacing_3
import com.example.recipe_generator.presentation.theme.spacing_4
import com.example.recipe_generator.presentation.theme.spacing_6
import com.example.recipe_generator.presentation.viewmodel.GlobalRecipeSearchViewModel

@Composable
fun GlobalRecipeSearchScreen(
    onBack: () -> Unit
) {
    val appContainer = (LocalContext.current.applicationContext as RecipeGeneratorApp).container
    val viewModel: GlobalRecipeSearchViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return GlobalRecipeSearchViewModel(
                    appContainer.searchGlobalRecipeUseCase
                ) as T
            }
        }
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing_4, vertical = spacing_4),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Volver",
                    tint = OnSurface
                )
            }
            Column(modifier = Modifier.padding(start = spacing_2)) {
                Text(
                    text = "Búsqueda global",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = OnSurface
                )
                Text(
                    text = "Firestore → TheMealDB → Fallback",
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurfaceVariant
                )
            }
        }

        OutlinedTextField(
            value = uiState.query,
            onValueChange = viewModel::onQueryChange,
            placeholder = { Text("Ej: ramen, empanada, shakshuka, pierogi...") },
            leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null, tint = Primary) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { viewModel.search() }),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing_6)
        )

        Spacer(modifier = Modifier.height(spacing_3))
        Box(modifier = Modifier.padding(horizontal = spacing_6)) {
            PrimaryButton(
                text = "Buscar receta",
                onClick = viewModel::search,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(spacing_4))

        when {
            uiState.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Primary)
                }
            }

            uiState.error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(spacing_6),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = uiState.error.orEmpty(),
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(spacing_3))
                        PrimaryButton(text = "Reintentar", onClick = {
                            viewModel.clearError()
                            viewModel.search()
                        })
                    }
                }
            }

            uiState.isEmpty -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(spacing_6),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Escribe cualquier receta para buscarla globalmente.",
                        color = OnSurfaceVariant
                    )
                }
            }

            uiState.recipe != null -> {
                RecipeResult(uiState.recipe!!)
            }
        }
    }
}

@Composable
private fun RecipeResult(recipe: GlobalRecipe) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = spacing_6),
        verticalArrangement = Arrangement.spacedBy(spacing_4)
    ) {
        EditorialCard {
            GlideRecipeImage(
                imageUrl = recipe.imagen,
                contentDescription = recipe.nombre,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            )

            Spacer(modifier = Modifier.height(spacing_4))
            Text(
                text = recipe.nombre,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = OnSurface
            )
            Spacer(modifier = Modifier.height(spacing_2))
            Text(
                text = "Fuente: ${recipe.source.label()}",
                style = MaterialTheme.typography.bodyMedium,
                color = Primary
            )
            if (recipe.pais.isNotBlank()) {
                Text(
                    text = "País: ${recipe.pais}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurfaceVariant
                )
            }
            if (recipe.keywords.isNotEmpty()) {
                Spacer(modifier = Modifier.height(spacing_2))
                Text(
                    text = "Keywords: ${recipe.keywords.joinToString(", ")}",
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurfaceVariant
                )
            }
        }
        Spacer(modifier = Modifier.height(spacing_6))
    }
}

@Composable
private fun GlideRecipeImage(
    imageUrl: String,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            ImageView(context).apply {
                scaleType = ImageView.ScaleType.CENTER_CROP
            }
        },
        update = { imageView ->
            val safeUrl = getImagenSegura(contentDescription, imageUrl)
            val guaranteedFallback = generarFallbackSeguro(contentDescription)
            Glide.with(imageView)
                .load(safeUrl)
                .centerCrop()
                .placeholder(R.drawable.img_placeholder)
                .error(
                    Glide.with(imageView)
                        .load(guaranteedFallback)
                        .centerCrop()
                        .error(R.drawable.img_placeholder)
                )
                .into(imageView)
            imageView.contentDescription = contentDescription
        }
    )
}

private fun GlobalRecipeSource.label(): String = when (this) {
    GlobalRecipeSource.FIRESTORE -> "Firebase"
    GlobalRecipeSource.THEMEALDB -> "TheMealDB + Cache Firebase"
    GlobalRecipeSource.FALLBACK -> "Fallback automático"
}

