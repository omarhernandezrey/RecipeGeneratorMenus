package com.example.recipe_generator.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.recipe_generator.data.Recipe
import com.example.recipe_generator.data.getFeaturedRecipeDetail
import com.example.recipe_generator.ui.components.EditorialBottomNavBar
import com.example.recipe_generator.ui.theme.Background
import com.example.recipe_generator.ui.theme.OnPrimary
import com.example.recipe_generator.ui.theme.OnSecondaryContainer
import com.example.recipe_generator.ui.theme.OnSurface
import com.example.recipe_generator.ui.theme.OnSurfaceVariant
import com.example.recipe_generator.ui.theme.Primary
import com.example.recipe_generator.ui.theme.PrimaryContainer
import com.example.recipe_generator.ui.theme.PrimaryFixedDim
import com.example.recipe_generator.ui.theme.RecipeGeneratorTheme
import com.example.recipe_generator.ui.theme.Secondary
import com.example.recipe_generator.ui.theme.SecondaryContainer
import com.example.recipe_generator.ui.theme.Surface as AppSurface
import com.example.recipe_generator.ui.theme.SurfaceContainerHigh
import com.example.recipe_generator.ui.theme.SurfaceContainerHighest
import com.example.recipe_generator.ui.theme.SurfaceContainerLow
import com.example.recipe_generator.ui.theme.SurfaceContainerLowest
import com.example.recipe_generator.ui.theme.Tertiary
import com.example.recipe_generator.ui.theme.rounded_full
import com.example.recipe_generator.ui.theme.rounded_lg
import com.example.recipe_generator.ui.theme.spacing_1
import com.example.recipe_generator.ui.theme.spacing_2
import com.example.recipe_generator.ui.theme.spacing_3
import com.example.recipe_generator.ui.theme.spacing_4
import com.example.recipe_generator.ui.theme.spacing_5
import com.example.recipe_generator.ui.theme.spacing_6
import com.example.recipe_generator.ui.theme.spacing_8
import java.util.Locale

@Composable
fun RecipeDetailScreen(
    recipe: Recipe = getFeaturedRecipeDetail(),
    selectedNavItem: Int = 0,
    onNavItemSelected: (Int) -> Unit = {},
    isFavorite: Boolean = recipe.isFavorite,
    onToggleFavorite: (String) -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            RecipeDetailTopBar(onBackClick = onBackClick)

            HeroSection(recipe = recipe)

            Column(
                modifier = Modifier
                    .padding(horizontal = spacing_6)
                    .padding(bottom = 140.dp)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = (-48).dp),
                    shape = RoundedCornerShape(rounded_lg),
                    colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(spacing_8)) {
                        Text(
                            text = recipe.title,
                            style = MaterialTheme.typography.displayMedium,
                            color = OnSurface
                        )

                        Spacer(modifier = Modifier.height(spacing_4))

                        Text(
                            text = recipe.description,
                            style = MaterialTheme.typography.bodyLarge,
                            color = OnSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(spacing_8))

                        ActionHub(
                            isFavorite = isFavorite,
                            onFavoriteClick = { onToggleFavorite(recipe.id) }
                        )

                        Spacer(modifier = Modifier.height(spacing_8))

                        NutritionalGrid(recipe = recipe)

                        Spacer(modifier = Modifier.height(spacing_8))

                        RecipeMainContent(recipe = recipe)
                    }
                }
            }
        }

        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
            EditorialBottomNavBar(
                selectedItem = selectedNavItem,
                onItemSelected = onNavItemSelected
            )
        }
    }
}

@Composable
private fun RecipeDetailTopBar(onBackClick: () -> Unit) {
    val topPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xCCFAF9FC))
            .padding(top = topPadding)
            .padding(horizontal = spacing_6, vertical = spacing_4),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Volver",
                tint = OnSurfaceVariant
            )
        }

        Text(
            text = "Gastronomía Editorial",
            style = MaterialTheme.typography.headlineSmall,
            color = Primary,
            fontWeight = FontWeight.ExtraBold
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Filled.Notifications,
                    contentDescription = "Notificaciones",
                    tint = Primary
                )
            }

            Surface(
                modifier = Modifier.size(32.dp),
                shape = CircleShape,
                color = PrimaryFixedDim.copy(alpha = 0.6f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "J",
                        style = MaterialTheme.typography.labelLarge,
                        color = Primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun HeroSection(recipe: Recipe) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(4f / 5f)
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
                    Text(
                        text = "Cargando...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = OnSurfaceVariant
                    )
                }
            }
        )

        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Background),
                        startY = 500f
                    )
                )
        )

        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = spacing_6, vertical = spacing_8),
            horizontalArrangement = Arrangement.spacedBy(spacing_2)
        ) {
            QuickInfoChip(
                iconText = "⏱",
                text = "${recipe.timeInMinutes} min"
            )
            QuickInfoChip(
                iconText = "≋",
                text = recipe.difficulty
            )
            QuickInfoChip(
                iconText = "★",
                text = String.format(Locale.US, "%.1f", recipe.rating)
            )
        }
    }
}

@Composable
private fun QuickInfoChip(iconText: String, text: String) {
    Surface(
        shape = RoundedCornerShape(rounded_full),
        color = Color.White.copy(alpha = 0.9f),
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = spacing_4, vertical = spacing_2),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing_1)
        ) {
            Text(
                text = iconText,
                style = MaterialTheme.typography.labelMedium,
                color = if (iconText == "★") Tertiary else Primary,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                color = OnSurface,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun ActionHub(isFavorite: Boolean, onFavoriteClick: () -> Unit) {
    val favoriteColors = if (isFavorite) {
        listOf(Primary, PrimaryContainer)
    } else {
        listOf(SurfaceContainerHigh, SurfaceContainerHighest)
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(spacing_3),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier,
            shape = RoundedCornerShape(rounded_full),
            color = Color.Transparent,
            shadowElevation = 8.dp
        ) {
            Box(
                modifier = Modifier
                    .background(
                        Brush.horizontalGradient(
                            colors = favoriteColors
                        )
                    )
                    .clickable(onClick = onFavoriteClick)
                    .padding(horizontal = spacing_6, vertical = spacing_4),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(spacing_2)
                ) {
                    Icon(
                        imageVector = if (isFavorite) {
                            Icons.Filled.Favorite
                        } else {
                            Icons.Outlined.FavoriteBorder
                        },
                        contentDescription = null,
                        tint = if (isFavorite) OnPrimary else OnSurfaceVariant,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        CircleActionButton(symbol = "↗")
        CircleActionButton(symbol = "+")
    }
}

@Composable
private fun CircleActionButton(
    symbol: String
) {
    Surface(
        modifier = Modifier.size(52.dp),
        shape = CircleShape,
        color = SurfaceContainerHigh
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = symbol,
                style = MaterialTheme.typography.titleLarge,
                color = OnSurfaceVariant,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun NutritionalGrid(recipe: Recipe) {
    Column(verticalArrangement = Arrangement.spacedBy(spacing_3)) {
        Row(horizontalArrangement = Arrangement.spacedBy(spacing_3)) {
            NutrientCard(
                modifier = Modifier.weight(1f),
                label = "Calorías",
                value = "${recipe.calories} kcal",
                containerColor = PrimaryFixedDim.copy(alpha = 0.3f),
                valueColor = Primary
            )
            NutrientCard(
                modifier = Modifier.weight(1f),
                label = "Proteínas",
                value = "${recipe.proteinGrams}g"
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(spacing_3)) {
            NutrientCard(
                modifier = Modifier.weight(1f),
                label = "Carbos",
                value = "${recipe.carbsGrams}g"
            )
            NutrientCard(
                modifier = Modifier.weight(1f),
                label = "Grasas",
                value = "${recipe.fatGrams}g"
            )
        }
    }
}

@Composable
private fun NutrientCard(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    containerColor: Color = SurfaceContainerLow,
    valueColor: Color = OnSurface
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = containerColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = spacing_5, horizontal = spacing_4),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = OnSurfaceVariant,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp
            )
            Spacer(modifier = Modifier.height(spacing_1))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                color = valueColor,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun RecipeMainContent(recipe: Recipe) {
    Column(verticalArrangement = Arrangement.spacedBy(spacing_8)) {
        RecipeSectionHeader(
            title = "Ingredientes",
            accentColor = SecondaryContainer
        )
        IngredientsList(recipe = recipe)

        RecipeSectionHeader(
            title = "Preparación",
            accentColor = PrimaryContainer
        )
        StepsList(recipe = recipe)
    }
}

@Composable
private fun RecipeSectionHeader(title: String, accentColor: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spacing_2)
    ) {
        Box(
            modifier = Modifier
                .width(32.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(rounded_full))
                .background(accentColor)
        )
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            color = OnSurface
        )
    }
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
private fun IngredientsList(recipe: Recipe) {
    Column(verticalArrangement = Arrangement.spacedBy(spacing_3)) {
        recipe.ingredients.forEach { ingredient ->
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = SurfaceContainerLow
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = spacing_4, vertical = spacing_4),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = ingredient,
                        style = MaterialTheme.typography.bodyLarge,
                        color = OnSurface
                    )
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = null,
                        tint = Secondary
                    )
                }
            }
        }

        if (recipe.ingredientTags.isNotEmpty()) {
            Spacer(modifier = Modifier.height(spacing_1))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(spacing_2),
                verticalArrangement = Arrangement.spacedBy(spacing_2)
            ) {
                recipe.ingredientTags.forEach { tag ->
                    Surface(
                        shape = RoundedCornerShape(rounded_full),
                        color = SecondaryContainer
                    ) {
                        Text(
                            text = tag,
                            modifier = Modifier.padding(horizontal = spacing_4, vertical = spacing_2),
                            style = MaterialTheme.typography.labelMedium,
                            color = OnSecondaryContainer,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StepsList(recipe: Recipe) {
    Column(verticalArrangement = Arrangement.spacedBy(spacing_6)) {
        recipe.steps.forEachIndexed { index, step ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(spacing_4),
                verticalAlignment = Alignment.Top
            ) {
                Surface(
                    modifier = Modifier.size(40.dp),
                    shape = CircleShape,
                    color = SurfaceContainerHighest
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "${index + 1}",
                            style = MaterialTheme.typography.titleMedium,
                            color = Primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Column {
                    Text(
                        text = step.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = OnSurface,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(spacing_1))
                    Text(
                        text = step.description,
                        style = MaterialTheme.typography.bodyLarge,
                        color = OnSurfaceVariant
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun RecipeDetailScreenPreview() {
    RecipeGeneratorTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = AppSurface
        ) {
            RecipeDetailScreen()
        }
    }
}
