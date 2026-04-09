package com.example.recipe_generator.presentation.myrecipes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import coil.compose.AsyncImage
import com.example.recipe_generator.data.remote.MealDbApi
import com.example.recipe_generator.data.remote.MealDbFullRecipe
import com.example.recipe_generator.data.sync.FirestoreSyncService
import com.example.recipe_generator.domain.model.UserRecipe
import com.example.recipe_generator.domain.repository.UserRecipeRepository
import com.example.recipe_generator.presentation.components.EditorialCard
import com.example.recipe_generator.presentation.components.PrimaryButton
import com.example.recipe_generator.presentation.profile.downloadImageToInternalStorage
import com.example.recipe_generator.presentation.theme.OnSurface
import com.example.recipe_generator.presentation.theme.OnSurfaceVariant
import com.example.recipe_generator.presentation.theme.Primary
import com.example.recipe_generator.presentation.theme.PrimaryContainer
import com.example.recipe_generator.presentation.theme.Surface
import com.example.recipe_generator.presentation.theme.rounded_full
import com.example.recipe_generator.presentation.theme.rounded_lg
import com.example.recipe_generator.presentation.theme.spacing_2
import com.example.recipe_generator.presentation.theme.spacing_3
import com.example.recipe_generator.presentation.theme.spacing_4
import com.example.recipe_generator.presentation.theme.spacing_6
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * Pantalla para buscar recetas internacionales en TheMealDB e importarlas
 * a "Mis Recetas" con un solo tap.
 */
@Composable
fun RecipeSearchScreen(
    userId: String,
    userRecipeRepository: UserRecipeRepository,
    firestoreSyncService: FirestoreSyncService,
    onBack: () -> Unit,
    onImported: () -> Unit
) {
    var selectedRecipe by remember { mutableStateOf<MealDbFullRecipe?>(null) }

    if (selectedRecipe != null) {
        RecipeImportPreviewScreen(
            recipe = selectedRecipe!!,
            userId = userId,
            userRecipeRepository = userRecipeRepository,
            firestoreSyncService = firestoreSyncService,
            onBack = { selectedRecipe = null },
            onImported = onImported
        )
    } else {
        RecipeSearchListScreen(
            onBack = onBack,
            onRecipeSelected = { selectedRecipe = it }
        )
    }
}

// ─── Lista de resultados ────────────────────────────────────────────────────

@Composable
private fun RecipeSearchListScreen(
    onBack: () -> Unit,
    onRecipeSelected: (MealDbFullRecipe) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var query      by remember { mutableStateOf("") }
    var results    by remember { mutableStateOf<List<MealDbFullRecipe>>(emptyList()) }
    var isLoading  by remember { mutableStateOf(false) }
    var hasSearched by remember { mutableStateOf(false) }

    fun search() {
        if (query.isBlank()) return
        coroutineScope.launch {
            isLoading  = true
            hasSearched = true
            results    = MealDbApi.searchFullRecipes(query)
            isLoading  = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Surface)
    ) {
        // Header
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
                    text = "Buscar recetas",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = OnSurface
                )
                Text(
                    text = "Busca e importa recetas internacionales",
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurfaceVariant
                )
            }
        }

        // Barra de búsqueda
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            placeholder = { Text("chicken, pasta, sushi, beef...") },
            trailingIcon = {
                IconButton(onClick = { search() }, enabled = !isLoading) {
                    Icon(Icons.Outlined.Search, contentDescription = "Buscar", tint = Primary)
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { search() }),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing_6)
        )

        Spacer(modifier = Modifier.height(spacing_4))

        // Área de resultados
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = spacing_6),
            contentAlignment = Alignment.Center
        ) {
            when {
                isLoading -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = Primary, modifier = Modifier.size(40.dp))
                        Spacer(modifier = Modifier.height(spacing_3))
                        Text(
                            text = "Buscando recetas...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = OnSurfaceVariant
                        )
                    }
                }

                !hasSearched -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(spacing_2)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = null,
                            tint = Primary.copy(alpha = 0.4f),
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = "Busca recetas del mundo",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = OnSurface
                        )
                        Text(
                            text = "Escribe el nombre de un plato en inglés\ny toca el ícono de búsqueda.\nEj: chicken, pasta, beef, fish",
                            style = MaterialTheme.typography.bodyMedium,
                            color = OnSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                results.isEmpty() -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(spacing_2)
                    ) {
                        Text(
                            text = "Sin resultados",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = OnSurface
                        )
                        Text(
                            text = "Prueba en inglés: \"chicken\", \"pasta\",\n\"beef\", \"seafood\", \"vegetarian\"",
                            style = MaterialTheme.typography.bodyMedium,
                            color = OnSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(spacing_4)
                    ) {
                        item { Spacer(modifier = Modifier.height(spacing_2)) }
                        items(results, key = { it.id }) { recipe ->
                            RecipeSearchResultCard(
                                recipe = recipe,
                                onClick = { onRecipeSelected(recipe) }
                            )
                        }
                        item { Spacer(modifier = Modifier.height(spacing_6)) }
                    }
                }
            }
        }
    }
}

@Composable
private fun RecipeSearchResultCard(
    recipe: MealDbFullRecipe,
    onClick: () -> Unit
) {
    EditorialCard(
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing_4),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = recipe.thumbUrl,
                contentDescription = recipe.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(rounded_lg))
                    .background(PrimaryContainer.copy(alpha = 0.15f))
                    .aspectRatio(1f)
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(spacing_2)
            ) {
                Text(
                    text = recipe.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = OnSurface
                )
                Text(
                    text = buildString {
                        if (recipe.category.isNotBlank()) append(recipe.category)
                        if (recipe.area.isNotBlank()) {
                            if (isNotEmpty()) append(" · ")
                            append(recipe.area)
                        }
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurfaceVariant
                )
                Text(
                    text = "${recipe.ingredients.size} ingredientes",
                    style = MaterialTheme.typography.labelMedium,
                    color = Primary,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Icon(
                imageVector = Icons.Outlined.Download,
                contentDescription = "Importar",
                tint = Primary.copy(alpha = 0.6f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

// ─── Preview + importar ─────────────────────────────────────────────────────

@Composable
private fun RecipeImportPreviewScreen(
    recipe: MealDbFullRecipe,
    userId: String,
    userRecipeRepository: UserRecipeRepository,
    firestoreSyncService: FirestoreSyncService,
    onBack: () -> Unit,
    onImported: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isSaving by remember { mutableStateOf(false) }

    fun importRecipe() {
        coroutineScope.launch {
            isSaving = true

            // Descargar imagen a filesDir para que persista offline
            val recipeId = UUID.randomUUID().toString()
            val imagePath = if (recipe.thumbUrl.isNotBlank()) {
                downloadImageToInternalStorage(
                    context  = context,
                    fileName = "recipe_image_$recipeId",
                    imageUrl = recipe.thumbUrl
                ) ?: ""
            } else ""

            // Convertir instrucciones a lista de pasos
            val steps = recipe.instructions
                .split("\n", "\r\n")
                .map { it.trim() }
                .filter { it.isNotBlank() && it.length > 3 }
                .ifEmpty { listOf(recipe.instructions.trim()) }

            val userRecipe = UserRecipe(
                id           = recipeId,
                userId       = userId,
                title        = recipe.name,
                imageRes     = imagePath,
                category     = mapCategory(recipe.category),
                description  = "${recipe.name} — receta ${recipe.area}".trim(),
                ingredients  = recipe.ingredients,
                steps        = steps,
                difficulty   = "Medio",
                createdAt    = System.currentTimeMillis(),
                updatedAt    = System.currentTimeMillis()
            )

            runCatching {
                userRecipeRepository.addRecipe(userRecipe)
                firestoreSyncService.uploadRecipe(userRecipe)
            }

            isSaving = false
            onImported()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Surface)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing_4, vertical = spacing_4),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack, enabled = !isSaving) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Volver",
                    tint = OnSurface
                )
            }
            Column(modifier = Modifier.padding(start = spacing_2)) {
                Text(
                    text = "Vista previa",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = OnSurface
                )
                Text(
                    text = "Revisa la receta antes de guardarla",
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurfaceVariant
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = spacing_6),
            verticalArrangement = Arrangement.spacedBy(spacing_4)
        ) {
            // Imagen del plato
            if (recipe.thumbUrl.isNotBlank()) {
                AsyncImage(
                    model = recipe.thumbUrl,
                    contentDescription = recipe.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(RoundedCornerShape(rounded_lg))
                        .background(PrimaryContainer.copy(alpha = 0.15f))
                )
            }

            // Título y metadata
            Text(
                text = recipe.name,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = OnSurface
            )
            if (recipe.category.isNotBlank() || recipe.area.isNotBlank()) {
                Text(
                    text = listOf(recipe.category, recipe.area)
                        .filter { it.isNotBlank() }
                        .joinToString(" · "),
                    style = MaterialTheme.typography.bodyLarge,
                    color = OnSurfaceVariant
                )
            }

            // Ingredientes
            EditorialCard {
                Text(
                    text = "Ingredientes (${recipe.ingredients.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = OnSurface
                )
                Spacer(modifier = Modifier.height(spacing_3))
                Column(verticalArrangement = Arrangement.spacedBy(spacing_2)) {
                    recipe.ingredients.forEach { ingredient ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(spacing_2),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(
                                text = "•",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Primary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = ingredient,
                                style = MaterialTheme.typography.bodyMedium,
                                color = OnSurface
                            )
                        }
                    }
                }
            }

            // Preparación
            val steps = recipe.instructions
                .split("\n", "\r\n")
                .map { it.trim() }
                .filter { it.isNotBlank() && it.length > 3 }
                .ifEmpty { listOf(recipe.instructions.trim()) }

            EditorialCard {
                Text(
                    text = "Preparación (${steps.size} pasos)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = OnSurface
                )
                Spacer(modifier = Modifier.height(spacing_3))
                Column(verticalArrangement = Arrangement.spacedBy(spacing_3)) {
                    steps.forEachIndexed { index, step ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(spacing_3),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(
                                        color = Primary.copy(alpha = 0.12f),
                                        shape = RoundedCornerShape(rounded_full)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${index + 1}",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Primary
                                )
                            }
                            Text(
                                text = step,
                                style = MaterialTheme.typography.bodyMedium,
                                color = OnSurface,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(spacing_2))
        }

        // Botón guardar (fijo abajo)
        Box(modifier = Modifier.padding(spacing_6)) {
            PrimaryButton(
                text = if (isSaving) "Guardando receta..." else "Guardar en Mis Recetas",
                onClick = { if (!isSaving) importRecipe() },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/** Mapea categorías de TheMealDB a las categorías locales */
private fun mapCategory(mealDbCategory: String): String = when (mealDbCategory.lowercase()) {
    "breakfast"                    -> "Desayuno"
    "dessert", "starter"           -> "Postre"
    "side", "miscellaneous", "vegan",
    "vegetarian", "goat", "lamb",
    "pork", "beef", "chicken",
    "seafood", "pasta"             -> "Almuerzo"
    else                           -> "Almuerzo"
}
