package com.example.recipe_generator.presentation.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Whatshot
import androidx.compose.ui.graphics.vector.ImageVector
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.recipe_generator.R
import com.example.recipe_generator.domain.model.Recipe
import com.example.recipe_generator.presentation.components.EditorialBottomNavBar
import com.example.recipe_generator.presentation.components.HomeEditorialTopAppBar
import com.example.recipe_generator.presentation.components.editorialBottomBarContentPadding
import com.example.recipe_generator.presentation.components.editorialTopBarContentPadding
import com.example.recipe_generator.presentation.theme.Background
import com.example.recipe_generator.presentation.theme.OnPrimary
import com.example.recipe_generator.presentation.theme.OnSecondaryContainer
import com.example.recipe_generator.presentation.theme.OnSurface
import com.example.recipe_generator.presentation.theme.OnSurfaceVariant
import com.example.recipe_generator.presentation.theme.Outline
import com.example.recipe_generator.presentation.theme.OutlineVariant
import com.example.recipe_generator.presentation.theme.Primary
import com.example.recipe_generator.presentation.theme.PrimaryFixedDim
import com.example.recipe_generator.presentation.theme.SecondaryContainer
import com.example.recipe_generator.presentation.theme.SurfaceContainer
import com.example.recipe_generator.presentation.theme.SurfaceContainerHigh
import com.example.recipe_generator.presentation.theme.SurfaceContainerLow
import com.example.recipe_generator.presentation.theme.SurfaceContainerLowest
import com.example.recipe_generator.presentation.theme.rounded_md
import com.example.recipe_generator.presentation.theme.spacing_2
import com.example.recipe_generator.presentation.theme.spacing_3
import com.example.recipe_generator.presentation.theme.spacing_12
import com.example.recipe_generator.presentation.theme.spacing_4
import com.example.recipe_generator.presentation.theme.spacing_6
import com.example.recipe_generator.presentation.theme.spacing_8

@Composable
fun FavoritesScreen(
    recipes: List<Recipe>,
    categories: List<String>,
    query: String,
    onQueryChange: (String) -> Unit,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    selectedNavItem: Int,
    onNavItemSelected: (Int) -> Unit,
    onRecipeSelected: (Recipe) -> Unit,
    onRemoveFavorite: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        val topContentPadding = editorialTopBarContentPadding()

        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 160.dp),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = spacing_6,
                end = spacing_6,
                top = topContentPadding,
                bottom = editorialBottomBarContentPadding()
            ),
            horizontalArrangement = Arrangement.spacedBy(spacing_6),
            verticalArrangement = Arrangement.spacedBy(spacing_6)
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                FavoritesSearchBar(
                    query = query,
                    onQueryChange = onQueryChange
                )
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                FavoritesCategoryFilter(
                    categories = categories,
                    selectedCategory = selectedCategory,
                    onCategorySelected = onCategorySelected
                )
            }

            if (recipes.isEmpty()) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    EmptyFavoritesState()
                }
            } else {
                itemsIndexed(
                    recipes,
                    span = { index, _ ->
                        if (shouldUseFeaturedCard(index, recipes.size)) {
                            GridItemSpan(maxLineSpan)
                        } else {
                            GridItemSpan(1)
                        }
                    }
                ) { index, recipe ->
                    if (shouldUseFeaturedCard(index, recipes.size)) {
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

        HomeEditorialTopAppBar(title = "Menú Semanal")

        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
            EditorialBottomNavBar(
                selectedItem = selectedNavItem,
                onItemSelected = onNavItemSelected
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
        shape = RoundedCornerShape(rounded_md),
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
                    modifier = Modifier.padding(horizontal = spacing_6, vertical = spacing_3),
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
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        shape = RoundedCornerShape(rounded_md),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
            ) {
                androidx.compose.foundation.Image(
                    painter = painterResource(id = R.drawable.img_placeholder),
                    contentDescription = recipe.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
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
                        icon = Icons.Outlined.AccessTime,
                        text = "${recipe.timeInMinutes} min",
                        containerColor = SecondaryContainer.copy(alpha = 0.3f),
                        textColor = OnSecondaryContainer
                    )
                    FavoriteStatChip(
                        icon = Icons.Outlined.Whatshot,
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

    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        shape = RoundedCornerShape(rounded_md),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        if (isWide) {
            Row {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(280.dp)
                ) {
                    androidx.compose.foundation.Image(
                        painter = painterResource(id = R.drawable.img_placeholder),
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
                    androidx.compose.foundation.Image(
                        painter = painterResource(id = R.drawable.img_placeholder),
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
    icon: ImageVector,
    text: String,
    containerColor: Color,
    textColor: Color
) {
    Surface(
        shape = RoundedCornerShape(percent = 50),
        color = containerColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = spacing_3, vertical = spacing_2),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing_2)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = textColor
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
                .padding(horizontal = spacing_8, vertical = spacing_12),
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

internal fun normalizeCategory(category: String): String {
    return when (category.lowercase()) {
        "comida" -> "Almuerzo"
        else -> category
    }
}

private fun shouldUseFeaturedCard(index: Int, totalItems: Int): Boolean {
    return totalItems >= 3 && index == 2
}
