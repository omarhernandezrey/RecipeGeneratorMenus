package com.example.recipe_generator.presentation.weeklyplan

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.RestaurantMenu
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.recipe_generator.RecipeGeneratorApp
import com.example.recipe_generator.domain.model.Recipe
import com.example.recipe_generator.domain.model.UserRecipe
import com.example.recipe_generator.presentation.components.AppTextField
import com.example.recipe_generator.presentation.components.EditorialCard
import com.example.recipe_generator.presentation.theme.OnSurface
import com.example.recipe_generator.presentation.theme.OnSurfaceVariant
import com.example.recipe_generator.presentation.theme.Primary
import com.example.recipe_generator.presentation.theme.PrimaryContainer
import com.example.recipe_generator.presentation.theme.SurfaceContainerLow
import com.example.recipe_generator.presentation.theme.rounded_full
import com.example.recipe_generator.presentation.theme.spacing_2
import com.example.recipe_generator.presentation.theme.spacing_3
import com.example.recipe_generator.presentation.theme.spacing_4
import com.example.recipe_generator.presentation.theme.spacing_6

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectRecipeDialog(
    onDismiss: () -> Unit,
    onRecipeSelected: (String) -> Unit
) {
    val appContainer = (LocalContext.current.applicationContext as RecipeGeneratorApp).container
    val userId = remember(appContainer) { appContainer.requireAuthenticatedUserId() }
    var query by rememberSaveable { mutableStateOf("") }

    val myRecipes by remember(userId, query, appContainer.userRecipeRepository) {
        if (query.isBlank()) {
            appContainer.userRecipeRepository.getMyRecipes(userId)
        } else {
            appContainer.userRecipeRepository.searchRecipes(userId, query)
        }
    }.collectAsStateWithLifecycle(initialValue = emptyList())

    val catalogRecipes by remember(query, appContainer.recipeRepository) {
        if (query.isBlank()) {
            appContainer.recipeRepository.getAllRecipes()
        } else {
            appContainer.recipeRepository.searchRecipes(query)
        }
    }.collectAsStateWithLifecycle(initialValue = emptyList())

    ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier.padding(horizontal = spacing_6, vertical = spacing_4),
            verticalArrangement = Arrangement.spacedBy(spacing_4)
        ) {
            Text(
                text = "Seleccionar receta",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = OnSurface
            )
            Text(
                text = "Busca en tus recetas o en el catálogo general para llenar la celda del plan.",
                style = MaterialTheme.typography.bodyMedium,
                color = OnSurfaceVariant
            )
            AppTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = "Buscar receta"
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(spacing_4)
            ) {
                item {
                    RecipeSectionTitle("Mis recetas")
                }

                if (myRecipes.isEmpty()) {
                    item {
                        EmptySectionState("No hay recetas personales que coincidan con la búsqueda.")
                    }
                } else {
                    items(myRecipes, key = { it.id }) { recipe ->
                        UserRecipeOptionCard(
                            recipe = recipe,
                            onClick = { onRecipeSelected(recipe.id) }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(spacing_2))
                    RecipeSectionTitle("Catálogo general")
                }

                if (catalogRecipes.isEmpty()) {
                    item {
                        EmptySectionState("No hay recetas del catálogo para esta búsqueda.")
                    }
                } else {
                    items(catalogRecipes, key = { it.id }) { recipe ->
                        CatalogRecipeOptionCard(
                            recipe = recipe,
                            onClick = { onRecipeSelected(recipe.id) }
                        )
                    }
                }

                item { Spacer(modifier = Modifier.height(spacing_6)) }
            }
        }
    }
}

@Composable
private fun RecipeSectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.ExtraBold,
        color = Primary
    )
}

@Composable
private fun EmptySectionState(text: String) {
    EditorialCard {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = OnSurfaceVariant
        )
    }
}

@Composable
private fun UserRecipeOptionCard(
    recipe: UserRecipe,
    onClick: () -> Unit
) {
    RecipeOptionCard(
        title = recipe.title,
        subtitle = recipe.category.ifBlank { "Receta personal" },
        supporting = "${recipe.timeInMinutes} min · ${recipe.difficulty}",
        onClick = onClick
    )
}

@Composable
private fun CatalogRecipeOptionCard(
    recipe: Recipe,
    onClick: () -> Unit
) {
    RecipeOptionCard(
        title = recipe.title,
        subtitle = recipe.category,
        supporting = "${recipe.timeInMinutes} min · ${recipe.difficulty}",
        onClick = onClick
    )
}

@Composable
private fun RecipeOptionCard(
    title: String,
    subtitle: String,
    supporting: String,
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
            Row(
                modifier = Modifier
                    .background(PrimaryContainer.copy(alpha = 0.3f), RoundedCornerShape(rounded_full))
                    .padding(horizontal = spacing_3, vertical = spacing_3),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.RestaurantMenu,
                    contentDescription = null,
                    tint = Primary
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(spacing_2)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = OnSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurfaceVariant
                )
                Text(
                    text = supporting,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = Primary
                )
            }
        }
    }
}
