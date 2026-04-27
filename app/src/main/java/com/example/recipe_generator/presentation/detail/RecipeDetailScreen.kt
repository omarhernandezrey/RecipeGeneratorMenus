package com.example.recipe_generator.presentation.detail

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.recipe_generator.data.remote.RecipeVideoResolver
import com.example.recipe_generator.domain.model.Recipe
import com.example.recipe_generator.presentation.components.DetailEditorialTopAppBar
import com.example.recipe_generator.presentation.components.EditorialBottomNavBar
import com.example.recipe_generator.presentation.components.RecipeImage
import com.example.recipe_generator.presentation.detail.components.RecipeVideoSection
import com.example.recipe_generator.presentation.detail.components.RecipeVideoUiState
import com.example.recipe_generator.presentation.components.editorialBottomBarContentPadding
import com.example.recipe_generator.presentation.theme.Background
import com.example.recipe_generator.presentation.theme.OnPrimary
import com.example.recipe_generator.presentation.theme.OnSecondaryContainer
import com.example.recipe_generator.presentation.theme.OnSurface
import com.example.recipe_generator.presentation.theme.OnSurfaceVariant
import com.example.recipe_generator.presentation.theme.Primary
import com.example.recipe_generator.presentation.theme.PrimaryContainer
import com.example.recipe_generator.presentation.theme.PrimaryFixedDim
import com.example.recipe_generator.presentation.theme.RecipeGeneratorTheme
import com.example.recipe_generator.presentation.theme.Secondary
import com.example.recipe_generator.presentation.theme.SecondaryContainer
import com.example.recipe_generator.presentation.theme.Surface as AppSurface
import com.example.recipe_generator.presentation.theme.SurfaceContainerHigh
import com.example.recipe_generator.presentation.theme.SurfaceContainerHighest
import com.example.recipe_generator.presentation.theme.SurfaceContainerLow
import com.example.recipe_generator.presentation.theme.SurfaceContainerLowest
import com.example.recipe_generator.presentation.theme.Tertiary
import com.example.recipe_generator.presentation.theme.rounded_full
import com.example.recipe_generator.presentation.theme.rounded_lg
import com.example.recipe_generator.presentation.theme.spacing_1
import com.example.recipe_generator.presentation.theme.spacing_2
import com.example.recipe_generator.presentation.theme.spacing_3
import com.example.recipe_generator.presentation.theme.spacing_4
import com.example.recipe_generator.presentation.theme.spacing_5
import com.example.recipe_generator.presentation.theme.spacing_6
import com.example.recipe_generator.presentation.theme.spacing_8
import com.example.recipe_generator.presentation.theme.spacing_10
import java.util.Locale

@Composable
fun RecipeDetailScreen(
    recipe: Recipe = Recipe(
        id = "default",
        title = "Receta",
        imageRes = "",
        timeInMinutes = 30,
        calories = 500,
        difficulty = "Medio",
        category = "Almuerzo",
        categorySubtitle = "Equilibrado",
        description = "Una deliciosa receta",
        dayOfWeek = "Lunes"
    ),
    selectedNavItem: Int = 0,
    onNavItemSelected: (Int) -> Unit = {},
    isFavorite: Boolean = recipe.isFavorite,
    onToggleFavorite: (String) -> Unit = {},
    onBackClick: () -> Unit = {},
    videoUiState: RecipeVideoUiState? = null
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
            DetailEditorialTopAppBar(
                title = "Menú Semanal",
                leadingContent = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = OnSurfaceVariant
                        )
                    }
                }
            )

            HeroSection(recipe = recipe)

            Column(
                modifier = Modifier
                    .padding(horizontal = spacing_6)
                    .padding(bottom = editorialBottomBarContentPadding() + spacing_3)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = (-48).dp),
                    shape = RoundedCornerShape(rounded_lg),
                    colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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

                        RecipeMainContent(
                            recipe = recipe,
                            videoUiState = videoUiState
                        )
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
private fun HeroSection(recipe: Recipe) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(4f / 5f)
    ) {
        RecipeImage(
            recipeTitle = recipe.title,
            imageRes    = recipe.imageRes,
            modifier    = Modifier.fillMaxSize()
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
                icon = Icons.Outlined.AccessTime,
                text = "${recipe.timeInMinutes} min",
                iconTint = Primary
            )
            QuickInfoChip(
                icon = Icons.Outlined.Tune,
                text = recipe.difficulty,
                iconTint = Primary
            )
            QuickInfoChip(
                icon = Icons.Filled.Star,
                text = String.format(Locale.US, "%.1f", recipe.rating),
                iconTint = Tertiary
            )
        }
    }
}

@Composable
private fun QuickInfoChip(icon: ImageVector, text: String, iconTint: Color = Primary) {
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
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = iconTint
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
    // F3-07: animateColorAsState() para animación suave al cambiar favorito
    val favStartColor by animateColorAsState(
        targetValue = if (isFavorite) Primary else SurfaceContainerHigh,
        animationSpec = tween(400), label = "favStart"
    )
    val favEndColor by animateColorAsState(
        targetValue = if (isFavorite) PrimaryContainer else SurfaceContainerHighest,
        animationSpec = tween(400), label = "favEnd"
    )
    val favContentColor by animateColorAsState(
        targetValue = if (isFavorite) OnPrimary else OnSurfaceVariant,
        animationSpec = tween(400), label = "favContent"
    )
    val favoriteColors = listOf(favStartColor, favEndColor)

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
                        contentDescription = if (isFavorite) "Eliminar de favoritos" else "Guardar en favoritos",
                        tint = favContentColor,
                    )
                    Text(
                        text = if (isFavorite) "Guardado" else "Guardar en Favoritos",
                        style = MaterialTheme.typography.labelLarge,
                        color = favContentColor,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        CircleActionButton(symbol = "↗", contentDescription = "Compartir")
        CircleActionButton(
            symbol = "+",
            contentDescription = "Añadir al carrito"
        )
    }
}

@Composable
private fun CircleActionButton(
    symbol: String,
    contentDescription: String
) {
    Surface(
        modifier = Modifier
            .size(52.dp)
            .clickable(onClickLabel = contentDescription) { /* Acción */ },
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
private fun RecipeMainContent(
    recipe: Recipe,
    videoUiState: RecipeVideoUiState? = null
) {
    val resolvedVideoUiState = videoUiState ?: rememberRecipeVideoUiState(recipe)

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

        Spacer(modifier = Modifier.height(16.dp))
        RecipeVideoSection(videoUiState = resolvedVideoUiState)
        Spacer(modifier = Modifier.height(24.dp))
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
                        text = buildString {
                            if (ingredient.quantity.isNotBlank()) {
                                append(ingredient.quantity)
                            }
                            if (ingredient.unit.isNotBlank()) {
                                if (isNotEmpty()) append(" ")
                                append(ingredient.unit)
                            }
                            if (ingredient.name.isNotBlank()) {
                                if (isNotEmpty()) append(" ")
                                append(ingredient.name)
                            }
                        },
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

// ─── Modal Bottom Sheet ───────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailBottomSheet(
    recipe: Recipe,
    isFavorite: Boolean,
    onToggleFavorite: (String) -> Unit,
    onDismiss: () -> Unit,
    videoUiState: RecipeVideoUiState? = null
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val resolvedVideoUiState = videoUiState ?: rememberRecipeVideoUiState(recipe)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Background,
        contentColor = OnSurface,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            // ── Hero: imagen + drag handle superpuesto + chips ──────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            ) {
                RecipeImage(
                    recipeTitle = recipe.title,
                    imageRes    = recipe.imageRes,
                    modifier    = Modifier.fillMaxSize()
                )

                // Gradiente inferior para que los chips sean legibles
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            Brush.verticalGradient(
                                colorStops = arrayOf(
                                    0.0f to Color.Transparent,
                                    0.55f to Color.Transparent,
                                    1.0f to Color.Black.copy(alpha = 0.72f)
                                )
                            )
                        )
                )

                // Drag handle encima de la imagen
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = spacing_3),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(rounded_full))
                            .background(Color.White.copy(alpha = 0.55f))
                    )
                }

                // Chips de info rápida en la parte inferior
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(horizontal = spacing_6, vertical = spacing_5),
                    horizontalArrangement = Arrangement.spacedBy(spacing_2)
                ) {
                    if (recipe.timeInMinutes > 0) {
                        QuickInfoChip(
                            icon = Icons.Outlined.AccessTime,
                            text = "${recipe.timeInMinutes} min",
                            iconTint = Primary
                        )
                    }
                    if (recipe.difficulty.isNotBlank()) {
                        QuickInfoChip(
                            icon = Icons.Outlined.Tune,
                            text = recipe.difficulty,
                            iconTint = Primary
                        )
                    }
                    QuickInfoChip(
                        icon = Icons.Filled.Star,
                        text = String.format(Locale.US, "%.1f", recipe.rating),
                        iconTint = Tertiary
                    )
                }
            }

            // ── Bloque de encabezado: categoría + título + descripción ──
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Background)
                    .padding(horizontal = spacing_6, vertical = spacing_6),
                verticalArrangement = Arrangement.spacedBy(spacing_3)
            ) {
                // Fila: badge categoría + día
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(rounded_full),
                        color = PrimaryFixedDim.copy(alpha = 0.22f)
                    ) {
                        Text(
                            text = recipe.category.uppercase(),
                            modifier = Modifier.padding(horizontal = spacing_4, vertical = spacing_2),
                            style = MaterialTheme.typography.labelSmall,
                            color = Primary,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.4.sp
                        )
                    }
                    if (recipe.dayOfWeek.isNotBlank()) {
                        Text(
                            text = recipe.dayOfWeek,
                            style = MaterialTheme.typography.labelMedium,
                            color = OnSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Título
                Text(
                    text = recipe.title,
                    style = MaterialTheme.typography.headlineLarge,
                    color = OnSurface,
                    fontWeight = FontWeight.ExtraBold
                )

                // Descripción
                if (recipe.description.isNotBlank()) {
                    Text(
                        text = recipe.description,
                        style = MaterialTheme.typography.bodyLarge,
                        color = OnSurfaceVariant,
                        lineHeight = 22.sp
                    )
                }
            }

            // ── Botón favorito ──────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Background)
                    .padding(horizontal = spacing_6)
            ) {
                ActionHub(
                    isFavorite = isFavorite,
                    onFavoriteClick = { onToggleFavorite(recipe.id) }
                )
            }

            // ── Separador ───────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing_6, vertical = spacing_6)
                    .height(1.dp)
                    .background(OnSurfaceVariant.copy(alpha = 0.1f))
            )

            // ── Estadísticas nutricionales en fila ──────────────────────
            if (recipe.calories > 0 || recipe.proteinGrams > 0) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Background)
                        .padding(horizontal = spacing_6)
                ) {
                    SheetSectionLabel(text = "Información nutricional")
                    Spacer(modifier = Modifier.height(spacing_4))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(spacing_3)
                    ) {
                        SheetNutrientTile(
                            modifier   = Modifier.weight(1f),
                            label      = "Calorías",
                            value      = "${recipe.calories}",
                            unit       = "kcal",
                            accentColor = Primary
                        )
                        SheetNutrientTile(
                            modifier = Modifier.weight(1f),
                            label    = "Proteína",
                            value    = "${recipe.proteinGrams}",
                            unit     = "g"
                        )
                        SheetNutrientTile(
                            modifier = Modifier.weight(1f),
                            label    = "Carbos",
                            value    = "${recipe.carbsGrams}",
                            unit     = "g"
                        )
                        SheetNutrientTile(
                            modifier = Modifier.weight(1f),
                            label    = "Grasas",
                            value    = "${recipe.fatGrams}",
                            unit     = "g"
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = spacing_6, vertical = spacing_6)
                        .height(1.dp)
                        .background(OnSurfaceVariant.copy(alpha = 0.1f))
                )
            }

            // ── Ingredientes ────────────────────────────────────────────
            if (recipe.ingredients.isNotEmpty() || recipe.ingredientTags.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Background)
                        .padding(horizontal = spacing_6)
                ) {
                    SheetSectionLabel(text = "Ingredientes")
                    Spacer(modifier = Modifier.height(spacing_4))
                    IngredientsList(recipe = recipe)
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = spacing_6, vertical = spacing_6)
                        .height(1.dp)
                        .background(OnSurfaceVariant.copy(alpha = 0.1f))
                )
            }

            // ── Pasos de preparación ────────────────────────────────────
            if (recipe.steps.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Background)
                        .padding(horizontal = spacing_6)
                ) {
                    SheetSectionLabel(text = "Preparación")
                    Spacer(modifier = Modifier.height(spacing_4))
                    StepsList(recipe = recipe)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            RecipeVideoSection(videoUiState = resolvedVideoUiState)
            Spacer(modifier = Modifier.height(24.dp))

            // Espacio inferior para gestos del sistema
            Spacer(modifier = Modifier.height(spacing_10))
        }
    }
}

@Composable
private fun SheetSectionLabel(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spacing_3)
    ) {
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(20.dp)
                .clip(RoundedCornerShape(rounded_full))
                .background(Primary)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.titleLarge,
            color = OnSurface,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun SheetNutrientTile(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    unit: String,
    accentColor: Color = OnSurface
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(spacing_3),
        color = SurfaceContainerLow
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = spacing_4, horizontal = spacing_2),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(spacing_1)
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                color = accentColor,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )
            Text(
                text = unit,
                style = MaterialTheme.typography.labelSmall,
                color = OnSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = OnSurfaceVariant,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun rememberRecipeVideoUiState(recipe: Recipe): RecipeVideoUiState {
    val videoResolver = remember { RecipeVideoResolver() }
    val recipeTitle = recipe.title
    val currentVideo = recipe.videoYoutube
    val fallbackUrl = remember(recipeTitle) {
        "https://www.youtube.com/results?search_query=${android.net.Uri.encode("como preparar $recipeTitle receta tutorial")}"
    }

    val state by produceState<RecipeVideoUiState>(
        initialValue = RecipeVideoUiState.Loading,
        key1 = recipeTitle,
        key2 = currentVideo
    ) {
        if (recipeTitle.isBlank()) {
            value = RecipeVideoUiState.Empty
            return@produceState
        }

        val resolved = runCatching {
            videoResolver.resolve(currentVideo, recipeTitle)
        }.getOrElse {
            value = RecipeVideoUiState.Error(
                message = "No se pudo obtener el video",
                fallbackUrl = fallbackUrl
            )
            return@produceState
        }

        value = if (resolved.isBlank()) {
            RecipeVideoUiState.Error(
                message = "No hay video disponible",
                fallbackUrl = fallbackUrl
            )
        } else {
            RecipeVideoUiState.Ready(
                videoUrl = resolved,
                fromFallback = resolved.contains("/results?search_query=")
            )
        }
    }

    return state
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
