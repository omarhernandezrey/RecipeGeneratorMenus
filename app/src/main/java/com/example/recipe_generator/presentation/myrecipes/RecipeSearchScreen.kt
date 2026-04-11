package com.example.recipe_generator.presentation.myrecipes

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
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
import com.example.recipe_generator.data.remote.ColombianRecipesDatabase
import com.example.recipe_generator.data.remote.FoodTranslator
import com.example.recipe_generator.data.remote.MealDbApi
import com.example.recipe_generator.data.remote.MealDbFullRecipe
import com.example.recipe_generator.data.sync.FirestoreSyncService
import com.example.recipe_generator.domain.model.UserRecipe
import com.example.recipe_generator.domain.repository.UserRecipeRepository
import com.example.recipe_generator.domain.usecase.ResolveRecipeVideoUseCase
import com.example.recipe_generator.presentation.components.EditorialCard
import com.example.recipe_generator.presentation.components.PrimaryButton
import com.example.recipe_generator.presentation.detail.components.RecipeVideoSection
import com.example.recipe_generator.presentation.detail.components.RecipeVideoUiState
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

@Composable
fun RecipeSearchScreen(
    userId: String,
    userRecipeRepository: UserRecipeRepository,
    firestoreSyncService: FirestoreSyncService,
    resolveRecipeVideoUseCase: ResolveRecipeVideoUseCase,
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
            resolveRecipeVideoUseCase = resolveRecipeVideoUseCase,
            onBack = { selectedRecipe = null },
            onImported = onImported
        )
    } else {
        RecipeSearchTabsScreen(
            onBack = onBack,
            onRecipeSelected = { selectedRecipe = it }
        )
    }
}

// ─── Pantalla de búsqueda con pestañas ─────────────────────────────────────

@Composable
private fun RecipeSearchTabsScreen(
    onBack: () -> Unit,
    onRecipeSelected: (MealDbFullRecipe) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var query       by remember { mutableStateOf("") }

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
                    text = "Busca en español y guarda en tus recetas",
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurfaceVariant
                )
            }
        }

        // Barra de búsqueda compartida
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            placeholder = {
                Text(
                    if (selectedTab == 0) "ajiaco, bandeja, buñuelos..."
                    else "pollo, pasta, sopa, arroz..."
                )
            },
            leadingIcon = {
                Icon(Icons.Outlined.Search, contentDescription = null, tint = Primary)
            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing_6)
        )

        Spacer(modifier = Modifier.height(spacing_3))

        // Tabs
        TabRow(selectedTabIndex = selectedTab) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = {
                    Text(
                        "Colombianas",
                        fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                    )
                }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = {
                    Text(
                        "Del Mundo",
                        fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                    )
                }
            )
        }

        // Contenido según pestaña activa
        when (selectedTab) {
            0 -> ColombianSearchContent(
                query = query,
                onRecipeSelected = onRecipeSelected
            )
            1 -> InternationalSearchContent(
                query = query,
                onRecipeSelected = onRecipeSelected
            )
        }
    }
}

// ─── Pestaña Colombianas ────────────────────────────────────────────────────

@Composable
private fun ColombianSearchContent(
    query: String,
    onRecipeSelected: (MealDbFullRecipe) -> Unit
) {
    // Filtro local instantáneo — sin red
    val results = remember(query) {
        ColombianRecipesDatabase.search(query)
    }

    if (results.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(spacing_6),
            contentAlignment = Alignment.Center
        ) {
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
                    text = "Prueba: \"ajiaco\", \"pollo\", \"postre\"",
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = spacing_6),
            verticalArrangement = Arrangement.spacedBy(spacing_4)
        ) {
            item { Spacer(modifier = Modifier.height(spacing_4)) }
            items(results, key = { it.id }) { recipe ->
                RecipeResultCard(recipe = recipe, onClick = { onRecipeSelected(recipe) })
            }
            item { Spacer(modifier = Modifier.height(spacing_6)) }
        }
    }
}

// ─── Pestaña Del Mundo ──────────────────────────────────────────────────────

@Composable
private fun InternationalSearchContent(
    query: String,
    onRecipeSelected: (MealDbFullRecipe) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var results    by remember { mutableStateOf<List<MealDbFullRecipe>>(emptyList()) }
    var isLoading  by remember { mutableStateOf(false) }
    var hasSearched by remember { mutableStateOf(false) }
    var lastQuery  by remember { mutableStateOf("") }

    // Busca automáticamente cuando el query cambia (con debounce implícito: al escribir Enter o cambiar de tab)
    LaunchedEffect(query) {
        if (query.isBlank()) {
            results = emptyList()
            hasSearched = false
        }
    }

    fun search() {
        if (query.isBlank()) return
        lastQuery = query
        coroutineScope.launch {
            isLoading   = true
            hasSearched = true
            val englishQuery = FoodTranslator.toEnglish(query)
            results = MealDbApi.searchFullRecipes(englishQuery)
            isLoading = false
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Botón buscar explícito para internacionales (requiere red)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing_6, vertical = spacing_3),
            horizontalArrangement = Arrangement.End
        ) {
            Box(
                modifier = Modifier
                    .background(Primary.copy(alpha = 0.1f), RoundedCornerShape(rounded_full))
                    .clickable { search() }
                    .padding(horizontal = spacing_4, vertical = spacing_2)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(spacing_2)
                ) {
                    Icon(
                        Icons.Outlined.Search,
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Buscar en internet",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = Primary
                    )
                }
            }
        }

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
                            text = "Buscando recetas del mundo...",
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
                            text = "Escribe en español",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = OnSurface
                        )
                        Text(
                            text = "Escribe el plato que buscas y toca\n\"Buscar en internet\".\nEj: pollo, pasta, sopa, mariscos",
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
                            text = "Prueba: pollo, carne, mariscos,\npasta, sopa, postre",
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
                            RecipeResultCard(
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

// ─── Tarjeta de resultado compartida ───────────────────────────────────────

@Composable
private fun RecipeResultCard(
    recipe: MealDbFullRecipe,
    onClick: () -> Unit
) {
    EditorialCard(modifier = Modifier.clickable(onClick = onClick)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing_4),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (recipe.thumbUrl.isNotBlank()) {
                AsyncImage(
                    model = recipe.thumbUrl,
                    contentDescription = recipe.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(rounded_lg))
                        .background(PrimaryContainer.copy(alpha = 0.15f))
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(rounded_lg))
                        .background(PrimaryContainer.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = recipe.name.take(2).uppercase(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = Primary
                    )
                }
            }

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
                if (recipe.category.isNotBlank() || recipe.area.isNotBlank()) {
                    Text(
                        text = listOf(recipe.category, recipe.area)
                            .filter { it.isNotBlank() }
                            .joinToString(" · "),
                        style = MaterialTheme.typography.bodySmall,
                        color = OnSurfaceVariant
                    )
                }
                Text(
                    text = "${recipe.ingredients.size} ingredientes",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Primary
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

// ─── Vista previa + importar ────────────────────────────────────────────────

@Composable
private fun RecipeImportPreviewScreen(
    recipe: MealDbFullRecipe,
    userId: String,
    userRecipeRepository: UserRecipeRepository,
    firestoreSyncService: FirestoreSyncService,
    resolveRecipeVideoUseCase: ResolveRecipeVideoUseCase,
    onBack: () -> Unit,
    onImported: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isSaving by remember { mutableStateOf(false) }

    val steps = remember(recipe.instructions) {
        recipe.instructions
            .split("\n", "\r\n")
            .map { it.trim() }
            .filter { it.isNotBlank() && it.length > 3 }
            .ifEmpty { listOf(recipe.instructions.trim()) }
    }
    val fallbackUrl = remember(recipe.name) { buildVideoFallbackUrl(recipe.name) }
    val videoUiState by produceState<RecipeVideoUiState>(
        initialValue = RecipeVideoUiState.Loading,
        key1 = recipe.name
    ) {
        val resolved = runCatching {
            resolveRecipeVideoUseCase(
                currentVideoUrl = null,
                recipeTitle = recipe.name
            )
        }.getOrElse {
            value = RecipeVideoUiState.Error(
                message = "No se pudo resolver el video",
                fallbackUrl = fallbackUrl
            )
            return@produceState
        }

        value = if (resolved.isBlank()) {
            RecipeVideoUiState.Error(
                message = "No se encontró un video exacto",
                fallbackUrl = fallbackUrl
            )
        } else {
            RecipeVideoUiState.Ready(
                videoUrl = resolved,
                fromFallback = resolved.contains("/results?search_query=")
            )
        }
    }

    fun importRecipe() {
        coroutineScope.launch {
            isSaving = true
            val recipeId = UUID.randomUUID().toString()
            val imagePath = if (recipe.thumbUrl.isNotBlank()) {
                downloadImageToInternalStorage(
                    context  = context,
                    fileName = "recipe_image_$recipeId",
                    imageUrl = recipe.thumbUrl
                ) ?: ""
            } else ""

            val userRecipe = UserRecipe(
                id          = recipeId,
                userId      = userId,
                title       = recipe.name,
                imageRes    = imagePath,
                category    = mapToLocalCategory(recipe.category),
                description = buildString {
                    append(recipe.name)
                    if (recipe.area.isNotBlank()) append(" — cocina ${recipe.area}")
                },
                ingredients = recipe.ingredients,
                steps       = steps,
                videoYoutube = when (val state = videoUiState) {
                    is RecipeVideoUiState.Ready -> state.videoUrl
                    is RecipeVideoUiState.Error -> state.fallbackUrl ?: fallbackUrl
                    RecipeVideoUiState.Empty -> fallbackUrl
                    RecipeVideoUiState.Loading -> fallbackUrl
                },
                difficulty  = "Medio",
                createdAt   = System.currentTimeMillis(),
                updatedAt   = System.currentTimeMillis()
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
                    text = "Revisa antes de guardar",
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

            // Pasos
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

            RecipeVideoSection(videoUiState = videoUiState)

            Spacer(modifier = Modifier.height(spacing_2))
        }

        Box(modifier = Modifier.padding(spacing_6)) {
            PrimaryButton(
                text = if (isSaving) "Guardando receta..." else "Guardar en Mis Recetas",
                onClick = { if (!isSaving) importRecipe() },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

private fun mapToLocalCategory(category: String): String = when (category.lowercase()) {
    "desayuno"        -> "Desayuno"
    "postre"          -> "Postre"
    "snack", "entrada", "aperitivo" -> "Snack"
    else              -> "Almuerzo"
}

private fun buildVideoFallbackUrl(recipeName: String): String {
    return "https://www.youtube.com/results?search_query=${
        Uri.encode("como preparar $recipeName receta tutorial")
    }"
}
