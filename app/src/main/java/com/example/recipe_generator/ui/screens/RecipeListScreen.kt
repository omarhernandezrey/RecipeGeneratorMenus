package com.example.recipe_generator.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.recipe_generator.data.Recipe
import com.example.recipe_generator.data.getMenuForDay
import com.example.recipe_generator.ui.components.DayTabLayout
import com.example.recipe_generator.ui.components.DifficultyChip
import com.example.recipe_generator.ui.components.EditorialBottomNavBar
import com.example.recipe_generator.ui.components.EditorialTopAppBar
import com.example.recipe_generator.ui.components.InfoChip
import com.example.recipe_generator.ui.theme.*

@Composable
fun RecipeListScreen(
    selectedNavItem: Int = 0,
    onNavItemSelected: (Int) -> Unit = {},
    favoriteRecipeIds: Set<String> = emptySet(),
    onToggleFavorite: (String) -> Unit = {},
    onRecipeSelected: (Recipe) -> Unit = {}
) {
    var selectedDay by remember { mutableStateOf("Lunes") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Surface)
    ) {
        // Scrollable Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 80.dp, bottom = 160.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Editorial Header
            Column(
                modifier = Modifier.padding(horizontal = spacing_6, vertical = spacing_8)
            ) {
                Text(
                    text = "CURADO PARA TI",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Primary,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.height(spacing_2))
                Text(
                    text = "Menú Semanal",
                    style = MaterialTheme.typography.displayLarge,
                    color = OnSurface
                )
            }

            // Day Tab Layout
            DayTabLayout(selectedDay) { selectedDay = it }

            // Recipe Sections - 1 card per category
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing_6),
                verticalArrangement = Arrangement.spacedBy(spacing_12)
            ) {
                val recipesForDay = getMenuForDay(selectedDay)

                recipesForDay.forEach { recipe ->
                    RecipeSection(
                        recipe = recipe,
                        favoriteRecipeIds = favoriteRecipeIds,
                        onToggleFavorite = onToggleFavorite,
                        onRecipeSelected = onRecipeSelected
                    )
                }

                Spacer(modifier = Modifier.height(spacing_12))
            }
        }

        // FAB with gradient and proper shadow
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = spacing_6, bottom = 112.dp)
                .size(64.dp)
                .shadow(
                    elevation = 16.dp,
                    shape = RoundedCornerShape(rounded_full)
                )
                .clip(RoundedCornerShape(rounded_full))
                .background(
                    Brush.linearGradient(listOf(Primary, PrimaryContainer))
                ),
            contentAlignment = Alignment.Center
        ) {
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = "Sorpréndeme",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        // Bottom Nav Bar
        Box(
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            EditorialBottomNavBar(
                selectedItem = selectedNavItem,
                onItemSelected = onNavItemSelected
            )
        }

        // Top App Bar - Fixed at top
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .background(Surface)
        ) {
            EditorialTopAppBar()
        }
    }
}

@Composable
fun RecipeSection(
    recipe: Recipe,
    favoriteRecipeIds: Set<String> = emptySet(),
    onToggleFavorite: (String) -> Unit = {},
    onRecipeSelected: (Recipe) -> Unit = {}
) {
    Column {
        // Section Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = spacing_6),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = recipe.category,
                style = MaterialTheme.typography.headlineLarge,
                color = OnSurface
            )
            Text(
                text = recipe.categorySubtitle.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = Tertiary,
                letterSpacing = 1.5.sp
            )
        }

        // Recipe Card
        RecipeCard(
            recipe = recipe,
            isFavorite = recipe.id in favoriteRecipeIds,
            onToggleFavorite = { onToggleFavorite(recipe.id) },
            onClick = { onRecipeSelected(recipe) }
        )
    }
}

@Composable
fun RecipeCard(
    recipe: Recipe,
    isFavorite: Boolean = false,
    onToggleFavorite: () -> Unit = {},
    onClick: () -> Unit = {}
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(rounded_md),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column {
            // Image with 16:9 aspect ratio
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .clip(RoundedCornerShape(topStart = rounded_md, topEnd = rounded_md))
                    .clickable(onClick = onClick)
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
                                .background(SurfaceContainerLow),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(32.dp),
                                color = Primary
                            )
                        }
                    },
                    error = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(SurfaceContainerLow),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🍽️", fontSize = 48.sp)
                        }
                    }
                )

                // Favorite Button - matching Stitch: w-12 h-12 top-4 right-4
                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(spacing_4)
                        .size(48.dp)
                        .background(
                            Color.White.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(rounded_full)
                        )
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Content - matching Stitch: p-8 (32dp)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onClick)
                    .padding(spacing_8)
            ) {
                // Title - matching Stitch: text-2xl font-extrabold
                Text(
                    text = recipe.title,
                    style = MaterialTheme.typography.headlineLarge,
                    color = OnSurface,
                    modifier = Modifier.padding(bottom = spacing_4)
                )

                // Info Chips - wrap to next line if needed
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    InfoChip(icon = "⏱", text = "${recipe.timeInMinutes} min")
                    InfoChip(icon = "🔥", text = "${recipe.calories} Cal")
                    DifficultyChip(difficulty = recipe.difficulty)
                }
            }
        }
    }
}
