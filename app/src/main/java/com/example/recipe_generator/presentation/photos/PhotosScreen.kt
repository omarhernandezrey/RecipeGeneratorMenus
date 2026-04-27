package com.example.recipe_generator.presentation.photos

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.recipe_generator.R
import com.example.recipe_generator.RecipeGeneratorApp
import com.example.recipe_generator.domain.model.Recipe
import com.example.recipe_generator.presentation.theme.OnSurface
import com.example.recipe_generator.presentation.theme.OnSurfaceVariant
import com.example.recipe_generator.presentation.theme.Primary
import com.example.recipe_generator.presentation.theme.PrimaryContainer
import com.example.recipe_generator.presentation.theme.Secondary
import com.example.recipe_generator.presentation.theme.SecondaryContainer
import com.example.recipe_generator.presentation.theme.Surface
import com.example.recipe_generator.presentation.theme.SurfaceContainerLowest
import com.example.recipe_generator.presentation.theme.rounded_lg
import com.example.recipe_generator.presentation.theme.rounded_md
import com.example.recipe_generator.presentation.theme.spacing_2
import com.example.recipe_generator.presentation.theme.spacing_4
import com.example.recipe_generator.presentation.theme.spacing_6
import com.example.recipe_generator.presentation.theme.spacing_8

/**
 * PhotosScreen — Galería de fotos de recetas reales.
 *
 * Carga recetas desde Room via RecipeRepository.
 * LazyColumn con scroll (LF8: ListView equiv) — imagen por receta.
 * Click → muestra descripción de la receta seleccionada (LF4: estado reactivo).
 *
 * Capa: Presentation
 */

// Imágenes reales de comida (JPEG descargadas en res/drawable/)
private val foodDrawables = listOf(
    R.drawable.img_food_real_1,
    R.drawable.img_food_real_2,
    R.drawable.img_food_real_3,
    R.drawable.img_food_real_4,
    R.drawable.img_food_real_5
)

private fun drawableForIndex(index: Int): Int =
    foodDrawables[index % foodDrawables.size]

@Composable
fun PhotosScreen(modifier: Modifier = Modifier) {
    val appContainer = (LocalContext.current.applicationContext as RecipeGeneratorApp).container

    // Carga recetas reales desde la BD para el día Lunes como muestra representativa
    val recipesFlow = remember(appContainer.recipeRepository) {
        appContainer.recipeRepository.getAllRecipes()
    }
    val allRecipes by recipesFlow.collectAsStateWithLifecycle(initialValue = emptyList())

    // Usamos las primeras 10 recetas para la galería
    val galleryRecipes = remember(allRecipes) { allRecipes.take(10) }

    // LF4: estado local con remember { mutableStateOf() }
    var selectedRecipeId by remember { mutableStateOf<String?>(null) }
    val selectedRecipe = remember(selectedRecipeId, galleryRecipes) {
        galleryRecipes.firstOrNull { it.id == selectedRecipeId }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Surface)
    ) {
        // Panel de descripción al seleccionar una imagen (LF4: estado reactivo)
        val descBg by animateColorAsState(
            if (selectedRecipe != null) SecondaryContainer else Surface,
            animationSpec = tween(300),
            label = "descBg"
        )

        if (selectedRecipe != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(descBg)
                    .padding(spacing_6)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(spacing_2)) {
                    Text(
                        text = selectedRecipe.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = Primary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = selectedRecipe.description.ifBlank {
                            "${selectedRecipe.category} · ${selectedRecipe.timeInMinutes} min · ${selectedRecipe.calories} kcal"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = OnSurface
                    )
                    Text(
                        text = "Dificultad: ${selectedRecipe.difficulty}  |  Día: ${selectedRecipe.dayOfWeek}",
                        style = MaterialTheme.typography.labelSmall,
                        color = OnSurfaceVariant
                    )
                }
            }
        }

        // LF8: LazyColumn con barra de desplazamiento (equiv. ListView)
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = spacing_6),
            verticalArrangement = Arrangement.spacedBy(spacing_6)
        ) {
            item { Spacer(modifier = Modifier.height(spacing_4)) }

            items(galleryRecipes, key = { it.id }) { recipe ->
                val index = galleryRecipes.indexOf(recipe)
                RecipePhotoCard(
                    recipe = recipe,
                    drawableRes = drawableForIndex(index),
                    isSelected = recipe.id == selectedRecipeId,
                    onClick = {
                        selectedRecipeId = if (selectedRecipeId == recipe.id) null else recipe.id
                    }
                )
            }

            item {
                val navBarPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
                Spacer(modifier = Modifier.height(spacing_8 + navBarPadding))
            }
        }
    }
}

@Composable
private fun RecipePhotoCard(
    recipe: Recipe,
    drawableRes: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val elevation by animateColorAsState(
        if (isSelected) Primary else SurfaceContainerLowest,
        animationSpec = tween(200),
        label = "cardBorder"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(rounded_lg),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) PrimaryContainer.copy(alpha = 0.12f)
            else SurfaceContainerLowest
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 6.dp else 2.dp
        )
    ) {
        Column {
            // LF7: Image() con drawable local
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .clip(RoundedCornerShape(topStart = rounded_md, topEnd = rounded_md))
            ) {
                Image(
                    painter = painterResource(id = drawableRes),
                    contentDescription = recipe.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Primary.copy(alpha = 0.12f))
                    )
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(spacing_4)
                            .background(Primary, shape = RoundedCornerShape(rounded_md))
                            .padding(horizontal = spacing_4, vertical = spacing_2)
                    ) {
                        Text(
                            text = "Seleccionada",
                            style = MaterialTheme.typography.labelSmall,
                            color = androidx.compose.ui.graphics.Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Badge con tipo de comida
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(spacing_4)
                        .background(
                            androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.55f),
                            shape = RoundedCornerShape(rounded_md)
                        )
                        .padding(horizontal = spacing_4, vertical = spacing_2)
                ) {
                    Text(
                        text = recipe.category,
                        style = MaterialTheme.typography.labelSmall,
                        color = androidx.compose.ui.graphics.Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Column(
                modifier = Modifier.padding(spacing_6),
                verticalArrangement = Arrangement.spacedBy(spacing_2)
            ) {
                Text(
                    text = recipe.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isSelected) Primary else OnSurface,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${recipe.timeInMinutes} min  ·  ${recipe.calories} kcal  ·  ${recipe.difficulty}",
                    style = MaterialTheme.typography.labelSmall,
                    color = OnSurfaceVariant
                )
                Text(
                    text = if (isSelected) "Toca para deseleccionar" else "Toca para ver descripción",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isSelected) Secondary else OnSurfaceVariant
                )
            }
        }
    }
}
