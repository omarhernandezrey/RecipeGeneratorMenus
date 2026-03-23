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
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.Schedule
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
import com.example.recipe_generator.ui.components.HomeEditorialTopAppBar
import com.example.recipe_generator.ui.components.InfoChip
import com.example.recipe_generator.ui.components.editorialBottomBarContentPadding
import com.example.recipe_generator.ui.components.editorialFabBottomPadding
import com.example.recipe_generator.ui.components.editorialTopBarContentPadding
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
        val topContentPadding = editorialTopBarContentPadding()
        val bottomNavHeight = editorialBottomBarContentPadding()

        // 1. Contenido principal (Scroll)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = topContentPadding)
                .verticalScroll(rememberScrollState())
        ) {
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

            DayTabLayout(selectedDay) { selectedDay = it }


            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing_6),
                verticalArrangement = Arrangement.spacedBy(spacing_10)
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

                Spacer(modifier = Modifier.height(bottomNavHeight + spacing_10))
            }
        }

        // 2. Degradado inferior (Fading edge)
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(bottomNavHeight + spacing_10)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Surface.copy(alpha = 0f),
                            Surface.copy(alpha = 0.7f),
                            Surface
                        )
                    )
                )
        )

        // 3. Barra de navegación inferior
        Box(
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            EditorialBottomNavBar(
                selectedItem = selectedNavItem,
                onItemSelected = onNavItemSelected
            )
        }

        // 4. FAB (Botón de la estrella) - AL FINAL PARA QUE ESTÉ SIEMPRE ENCIMA
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = spacing_6, bottom = editorialFabBottomPadding())
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

        // 5. Barra superior
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .background(Surface)
        ) {
            HomeEditorialTopAppBar(title = "Diseñador de Menús")
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
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
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
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = "Sin imagen",
                                modifier = Modifier.size(48.dp),
                                tint = com.example.recipe_generator.ui.theme.Primary
                            )
                        }
                    }
                )

                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(spacing_4)
                        .size(48.dp)
                        .background(
                            Color.White.copy(alpha = 0.88f),
                            shape = RoundedCornerShape(rounded_full)
                        )
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) Tertiary else OnSurfaceVariant,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onClick)
                    .padding(spacing_8)
            ) {
                Text(
                    text = recipe.title,
                    style = MaterialTheme.typography.headlineLarge,
                    color = OnSurface,
                    modifier = Modifier.padding(bottom = spacing_4)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(spacing_3),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    InfoChip(icon = Icons.Outlined.Schedule, text = "${recipe.timeInMinutes} min")
                    InfoChip(icon = Icons.Outlined.LocalFireDepartment, text = "${recipe.calories} Cal")
                    DifficultyChip(difficulty = recipe.difficulty)
                }
            }
        }
    }
}
