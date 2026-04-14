package com.example.recipe_generator.presentation.home

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Whatshot
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.recipe_generator.data.remote.RecipeVideoResolver
import com.example.recipe_generator.domain.model.Recipe
import com.example.recipe_generator.presentation.components.DayTabLayout
import com.example.recipe_generator.presentation.components.DifficultyChip
import com.example.recipe_generator.presentation.components.EditorialBottomNavBar
import com.example.recipe_generator.presentation.components.HomeEditorialTopAppBar
import com.example.recipe_generator.presentation.components.InfoChip
import com.example.recipe_generator.presentation.components.RecipeImage
import com.example.recipe_generator.presentation.components.RecipeVideoPlayerDialog
import com.example.recipe_generator.presentation.components.YouTubeBrandIcon
import com.example.recipe_generator.presentation.components.editorialBottomBarContentPadding
import com.example.recipe_generator.presentation.components.editorialTopBarContentPadding
import com.example.recipe_generator.presentation.theme.*

@Composable
fun RecipeListScreen(
    selectedDay: String = "Lunes",
    recipes: List<Recipe> = emptyList(),
    onDaySelected: (String) -> Unit = {},
    selectedNavItem: Int = 0,
    onNavItemSelected: (Int) -> Unit = {},
    favoriteRecipeIds: Set<String> = emptySet(),
    onToggleFavorite: (String) -> Unit = {},
    onRecipeSelected: (Recipe) -> Unit = {},
    onProfileClick: () -> Unit = {},
    isSyncing: Boolean = false
) {
    var activeVideoUrl by rememberSaveable { mutableStateOf<String?>(null) }
    var activeVideoTitle by rememberSaveable { mutableStateOf("") }

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
            Spacer(modifier = Modifier.height(spacing_4))

            DayTabLayout(selectedDay = selectedDay, onDaySelected = onDaySelected)

            Spacer(modifier = Modifier.height(spacing_4))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing_6),
                verticalArrangement = Arrangement.spacedBy(spacing_10)
            ) {
                recipes.forEach { recipe ->
                    RecipeSection(
                        recipe = recipe,
                        favoriteRecipeIds = favoriteRecipeIds,
                        onToggleFavorite = onToggleFavorite,
                        onRecipeSelected = onRecipeSelected,
                        onOpenVideo = { videoUrl, recipeTitle ->
                            activeVideoUrl = videoUrl
                            activeVideoTitle = recipeTitle
                        }
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

        // 4. Barra superior
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .background(Surface)
        ) {
            HomeEditorialTopAppBar(
                title = "Menú Semanal",
                onProfileClick = onProfileClick,
                isSyncing = isSyncing
            )
        }

        activeVideoUrl?.let { videoUrl ->
            RecipeVideoPlayerDialog(
                recipeTitle = activeVideoTitle,
                videoUrl = videoUrl,
                onDismiss = { activeVideoUrl = null }
            )
        }
    }
}

@Composable
fun RecipeSection(
    recipe: Recipe,
    favoriteRecipeIds: Set<String> = emptySet(),
    onToggleFavorite: (String) -> Unit = {},
    onRecipeSelected: (Recipe) -> Unit = {},
    onOpenVideo: (String, String) -> Unit = { _, _ -> }
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = spacing_4),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = recipe.category,
                style = MaterialTheme.typography.headlineMedium,
                color = OnSurface,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = recipe.categorySubtitle.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = Primary,
                letterSpacing = 1.2.sp
            )
        }

        RecipeCard(
            recipe = recipe,
            isFavorite = recipe.id in favoriteRecipeIds,
            onToggleFavorite = { onToggleFavorite(recipe.id) },
            onClick = { onRecipeSelected(recipe) },
            onOpenVideo = { videoUrl -> onOpenVideo(videoUrl, recipe.title) }
        )
    }
}

@Composable
fun RecipeCard(
    recipe: Recipe,
    isFavorite: Boolean = false,
    onToggleFavorite: () -> Unit = {},
    onClick: () -> Unit = {},
    onOpenVideo: (String) -> Unit = {}
) {
    val resolvedVideoUrl = remember(recipe.title, recipe.videoYoutube) {
        RecipeVideoResolver().resolve(
            currentVideoUrl = recipe.videoYoutube,
            recipeTitle = recipe.title
        )
    }
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
                RecipeImage(
                    recipeTitle = recipe.title,
                    imageRes    = recipe.imageRes,
                    modifier    = Modifier.fillMaxSize()
                )

                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(spacing_4)
                        .size(44.dp)
                        .background(
                            Color.White.copy(alpha = 0.92f),
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
                    .padding(horizontal = spacing_8, vertical = spacing_6),
                verticalArrangement = Arrangement.spacedBy(spacing_4)
            ) {
                Text(
                    text = recipe.title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = OnSurface,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(spacing_3),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    InfoChip(icon = Icons.Outlined.AccessTime, text = "${recipe.timeInMinutes} min")
                    InfoChip(icon = Icons.Outlined.Whatshot, text = "${recipe.calories} Cal")
                    if (recipe.difficulty.isNotBlank()) {
                        DifficultyChip(difficulty = recipe.difficulty)
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    MiniAnimatedYoutubePlayButton(
                        onClick = { onOpenVideo(resolvedVideoUrl) }
                    )
                }
            }
        }
    }
}

@Composable
private fun MiniAnimatedYoutubePlayButton(
    onClick: () -> Unit
) {
    val transition = rememberInfiniteTransition(label = "miniVideoPulse")
    val pulseScale = transition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )
    val pulseAlpha = transition.animateFloat(
        initialValue = 0.16f,
        targetValue = 0.34f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Box(
        modifier = Modifier.size(width = 40.dp, height = 28.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(width = 38.dp, height = 26.dp)
                .scale(pulseScale.value)
                .background(
                    Color(0xFFFF0000).copy(alpha = pulseAlpha.value),
                    RoundedCornerShape(10.dp)
                )
        )
        Box(
            modifier = Modifier
                .size(width = 34.dp, height = 22.dp)
                .clip(RoundedCornerShape(8.dp))
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            YouTubeBrandIcon(modifier = Modifier.fillMaxSize())
        }
    }
}

