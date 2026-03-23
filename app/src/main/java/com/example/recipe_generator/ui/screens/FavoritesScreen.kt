package com.example.recipe_generator.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.recipe_generator.data.Recipe
import com.example.recipe_generator.ui.components.EditorialBottomNavBar
import com.example.recipe_generator.ui.theme.Background
import com.example.recipe_generator.ui.theme.OnPrimary
import com.example.recipe_generator.ui.theme.OnSecondaryContainer
import com.example.recipe_generator.ui.theme.OnSurface
import com.example.recipe_generator.ui.theme.OnSurfaceVariant
import com.example.recipe_generator.ui.theme.Outline
import com.example.recipe_generator.ui.theme.OutlineVariant
import com.example.recipe_generator.ui.theme.Primary
import com.example.recipe_generator.ui.theme.PrimaryFixedDim
import com.example.recipe_generator.ui.theme.SecondaryContainer
import com.example.recipe_generator.ui.theme.SurfaceContainer
import com.example.recipe_generator.ui.theme.SurfaceContainerHigh
import com.example.recipe_generator.ui.theme.SurfaceContainerLow
import com.example.recipe_generator.ui.theme.SurfaceContainerLowest
import com.example.recipe_generator.ui.theme.rounded_md
import com.example.recipe_generator.ui.theme.spacing_3
import com.example.recipe_generator.ui.theme.spacing_4
import com.example.recipe_generator.ui.theme.spacing_6
import com.example.recipe_generator.ui.theme.spacing_8

private const val FAVORITES_PROFILE_IMAGE =
    "https://lh3.googleusercontent.com/aida-public/AB6AXuB2xtj_r97gsEUurBmuOwkrxnpW7yFeqbQN49f2Q79dIXXT3KFVXeIrQYLSYkUT_TrcscsTFavakiUZ_SKEOnTS-t8yDUZ5Nk2sh8TR1sSgmFlPphMmtbSvy4Gs81b8aaCXpo_JpPWBRZIWe6CNJ4d0rMGaUqI1arpd0k-UxOq2s8N1yD8P_bYtik4H5hSLeTp7dSRrkcOVhoQlK3CNBReGwEWVL741RcR7k91urFLVQXuqDRlBUmY9T6jfUa37KCR9ePusAJ272j4"

@Composable
fun FavoritesScreen(
    recipes: List<Recipe>,
    selectedNavItem: Int,
    onNavItemSelected: (Int) -> Unit,
    onRecipeSelected: (Recipe) -> Unit,
    onRemoveFavorite: (String) -> Unit
) {
    var query by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Todos") }

    val categories = remember(recipes) {
        listOf("Todos") + recipes.map { normalizeCategory(it.category) }.distinct()
    }
    val filteredRecipes = recipes.filter { recipe ->
        val matchesQuery = query.isBlank() ||
            recipe.title.contains(query, ignoreCase = true) ||
            recipe.description.contains(query, ignoreCase = true)
        val matchesCategory = selectedCategory == "Todos" ||
            normalizeCategory(recipe.category) == selectedCategory
        matchesQuery && matchesCategory
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 160.dp),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = spacing_6,
                end = spacing_6,
                top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + 80.dp,
                bottom = 132.dp
            ),
            horizontalArrangement = Arrangement.spacedBy(spacing_6),
            verticalArrangement = Arrangement.spacedBy(spacing_6)
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                FavoritesSearchBar(
                    query = query,
                    onQueryChange = { query = it }
                )
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                FavoritesCategoryFilter(
                    categories = categories,
                    selectedCategory = selectedCategory,
                    onCategorySelected = { selectedCategory = it }
                )
            }

            if (filteredRecipes.isEmpty()) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    EmptyFavoritesState()
                }
            } else {
                itemsIndexed(
                    filteredRecipes,
                    span = { index, _ ->
                        if (shouldUseFeaturedCard(index, filteredRecipes.size)) {
                            GridItemSpan(maxLineSpan)
                        } else {
                            GridItemSpan(1)
                        }
                    }
                ) { index, recipe ->
                    if (shouldUseFeaturedCard(index, filteredRecipes.size)) {
                        FavoriteFeaturedCard(
                            recipe = recipe,
                            onClick = { onRecipeSelected(recipe) },
                            onRemoveFavorite = { onRemoveFavorite(recipe.id) }
                        )
                    } else {
                        FavoriteGridCard(
                            recipe = recipe,
                            onClick = { onRecipeSelected(recipe) },
                            onRemoveFavorite = { onRemoveFavorite(recipe.id) }
                        )
                    }
                }
            }
        }

        FavoritesTopBar()

        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
            EditorialBottomNavBar(
                selectedItem = selectedNavItem,
                onItemSelected = onNavItemSelected
            )
        }
    }
}

@Composable
private fun FavoritesTopBar() {
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xCCFAF9FC))
            .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding())
            .padding(horizontal = spacing_6, vertical = spacing_4),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(SurfaceContainer)
            ) {
                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(FAVORITES_PROFILE_IMAGE)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Perfil",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }

        Text(
            text = "Favoritos",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
            color = Primary
        )

        IconButton(onClick = {}) {
            Icon(
                imageVector = Icons.Filled.Notifications,
                contentDescription = "Notificaciones",
                tint = OnSurfaceVariant
            )
        }
    }
}

@Composable
private fun FavoritesSearchBar(
    query: String,
    onQueryChange: (String) -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        placeholder = {
            Text(
                text = "Buscar en favoritos...",
                style = MaterialTheme.typography.bodyLarge,
                color = Outline.copy(alpha = 0.6f)
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = "Buscar",
                tint = Outline
            )
        },
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = SurfaceContainerLow,
            unfocusedContainerColor = SurfaceContainerLow,
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            cursorColor = Primary
        )
    )
}

@Composable
private fun FavoritesCategoryFilter(
    categories: List<String>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(spacing_3)
    ) {
        items(categories) { category ->
            val isSelected = category == selectedCategory
            Surface(
                modifier = Modifier.clickable { onCategorySelected(category) },
                shape = RoundedCornerShape(percent = 50),
                color = if (isSelected) Primary else SurfaceContainerHigh
            ) {
                Text(
                    text = category,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 10.dp),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                    color = if (isSelected) OnPrimary else OnSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun FavoriteGridCard(
    recipe: Recipe,
    onClick: () -> Unit,
    onRemoveFavorite: () -> Unit
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        shape = RoundedCornerShape(rounded_md),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
            ) {
                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(recipe.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = recipe.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    loading = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(SurfaceContainerLow)
                        )
                    }
                )

                IconButton(
                    onClick = onRemoveFavorite,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(spacing_4)
                        .size(40.dp)
                        .background(
                            color = Color.White.copy(alpha = 0.9f),
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Quitar de favoritos",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            Column(
                modifier = Modifier.padding(spacing_6)
            ) {
                Text(
                    text = recipe.title,
                    style = MaterialTheme.typography.headlineLarge,
                    color = OnSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(spacing_4))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(spacing_4),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FavoriteStatChip(
                        icon = "⏱",
                        text = "${recipe.timeInMinutes} min",
                        containerColor = SecondaryContainer.copy(alpha = 0.3f),
                        textColor = OnSecondaryContainer
                    )
                    FavoriteStatChip(
                        icon = "⚡",
                        text = "${recipe.calories} kcal",
                        containerColor = SurfaceContainerHigh,
                        textColor = OnSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun FavoriteFeaturedCard(
    recipe: Recipe,
    onClick: () -> Unit,
    onRemoveFavorite: () -> Unit
) {
    val isWide = LocalConfiguration.current.screenWidthDp >= 700
    val context = LocalContext.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        shape = RoundedCornerShape(rounded_md),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        if (isWide) {
            Row {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(280.dp)
                ) {
                    SubcomposeAsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(recipe.imageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = recipe.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                FavoriteFeaturedContent(
                    recipe = recipe,
                    modifier = Modifier.weight(1f),
                    onRemoveFavorite = onRemoveFavorite
                )
            }
        } else {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                ) {
                    SubcomposeAsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(recipe.imageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = recipe.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                FavoriteFeaturedContent(
                    recipe = recipe,
                    modifier = Modifier.fillMaxWidth(),
                    onRemoveFavorite = onRemoveFavorite
                )
            }
        }
    }
}

@Composable
private fun FavoriteFeaturedContent(
    recipe: Recipe,
    modifier: Modifier,
    onRemoveFavorite: () -> Unit
) {
    Column(
        modifier = modifier.padding(spacing_8),
        verticalArrangement = Arrangement.spacedBy(spacing_4)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = recipe.title,
                style = MaterialTheme.typography.displaySmall,
                color = OnSurface,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(spacing_4))

            Surface(
                modifier = Modifier.clickable(onClick = onRemoveFavorite),
                shape = CircleShape,
                color = SurfaceContainer
            ) {
                Box(
                    modifier = Modifier.size(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Quitar de favoritos",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }

        Text(
            text = recipe.description,
            style = MaterialTheme.typography.bodyLarge,
            color = OnSurfaceVariant,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            FavoriteMetricColumn(
                label = "Tiempo",
                value = "${recipe.timeInMinutes} min"
            )
            Spacer(
                modifier = Modifier
                    .padding(horizontal = spacing_6)
                    .width(1.dp)
                    .height(32.dp)
                    .background(OutlineVariant.copy(alpha = 0.3f))
            )
            FavoriteMetricColumn(
                label = "Energía",
                value = "${recipe.calories} kcal"
            )
        }
    }
}

@Composable
private fun FavoriteMetricColumn(label: String, value: String) {
    Column {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = Outline,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            color = OnSurface,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun FavoriteStatChip(
    icon: String,
    text: String,
    containerColor: Color,
    textColor: Color
) {
    Surface(
        shape = RoundedCornerShape(percent = 50),
        color = containerColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = icon,
                style = MaterialTheme.typography.labelLarge
            )
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                color = textColor,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun EmptyFavoritesState() {
    Surface(
        shape = RoundedCornerShape(28.dp),
        color = SurfaceContainerLowest
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing_8, vertical = 56.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(spacing_4)
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .background(
                        color = PrimaryFixedDim.copy(alpha = 0.28f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = null,
                    tint = Primary
                )
            }
            Text(
                text = "Todavía no tienes favoritos",
                style = MaterialTheme.typography.headlineSmall,
                color = OnSurface
            )
            Text(
                text = "Toca el corazón en una receta para guardarla y verla aquí.",
                style = MaterialTheme.typography.bodyLarge,
                color = OnSurfaceVariant
            )
        }
    }
}

private fun normalizeCategory(category: String): String {
    return when (category.lowercase()) {
        "comida" -> "Almuerzo"
        else -> category
    }
}

private fun shouldUseFeaturedCard(index: Int, totalItems: Int): Boolean {
    return totalItems >= 3 && index == 2
}
