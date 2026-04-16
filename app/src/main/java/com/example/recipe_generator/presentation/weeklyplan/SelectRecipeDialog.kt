package com.example.recipe_generator.presentation.weeklyplan

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.RestaurantMenu
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.recipe_generator.RecipeGeneratorApp
import com.example.recipe_generator.domain.model.Recipe
import com.example.recipe_generator.domain.model.UserRecipe
import com.example.recipe_generator.presentation.components.AppTextField
import com.example.recipe_generator.presentation.components.EditorialCard
import com.example.recipe_generator.presentation.theme.OnSurface
import com.example.recipe_generator.presentation.theme.OnSurfaceVariant
import com.example.recipe_generator.presentation.theme.Primary
import com.example.recipe_generator.presentation.theme.PrimaryContainer
import com.example.recipe_generator.presentation.theme.rounded_full
import com.example.recipe_generator.presentation.theme.rounded_lg
import com.example.recipe_generator.presentation.theme.spacing_2
import com.example.recipe_generator.presentation.theme.spacing_3
import com.example.recipe_generator.presentation.theme.spacing_4
import com.example.recipe_generator.presentation.theme.spacing_6
import java.io.File

/**
 * BottomSheet para seleccionar una receta para el plan semanal.
 *
 * [mealType] filtra estrictamente: si es "Desayuno" solo muestra desayunos,
 * si es "Almuerzo" solo almuerzos, etc. Así el usuario nunca puede asignar
 * una cena en el slot de desayuno.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectRecipeDialog(
    mealType: String,
    onDismiss: () -> Unit,
    onRecipeSelected: (String) -> Unit
) {
    val appContainer = (LocalContext.current.applicationContext as RecipeGeneratorApp).container
    val userId = remember(appContainer) { appContainer.requireAuthenticatedUserId() }
    var query by rememberSaveable { mutableStateOf("") }

    // Recetas del usuario — filtramos por category == mealType
    val allMyRecipes by remember(userId, appContainer.userRecipeRepository) {
        appContainer.userRecipeRepository.getMyRecipes(userId)
    }.collectAsStateWithLifecycle(initialValue = emptyList())

    // Recetas del catálogo — filtramos por category == mealType
    val allCatalogRecipes by remember(appContainer.recipeRepository) {
        appContainer.recipeRepository.getAllRecipes()
    }.collectAsStateWithLifecycle(initialValue = emptyList())

    val filteredMyRecipes = remember(allMyRecipes, query) {
        if (query.isBlank()) allMyRecipes
        else allMyRecipes.filter { it.title.contains(query, ignoreCase = true) }
    }

    // En edición manual del calendario siempre se deben poder asignar recetas personales.
    // Las compatibles con el slot aparecen primero, el resto debajo.
    val myRecipes = remember(filteredMyRecipes, mealType) {
        filteredMyRecipes.filter { it.matchesMealType(mealType) }
    }

    val otherMyRecipes = remember(filteredMyRecipes, myRecipes) {
        val matchingIds = myRecipes.mapTo(mutableSetOf()) { it.id }
        filteredMyRecipes.filterNot { it.id in matchingIds }
    }

    val catalogRecipes = remember(allCatalogRecipes, query, mealType) {
        allCatalogRecipes
            .filter { it.category.equals(mealType, ignoreCase = true) }
            .let { list ->
                if (query.isBlank()) list
                else list.filter { it.title.contains(query, ignoreCase = true) }
            }
    }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier.padding(horizontal = spacing_6, vertical = spacing_4),
            verticalArrangement = Arrangement.spacedBy(spacing_4)
        ) {
            Column {
                Text(
                    text = "Seleccionar $mealType",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = OnSurface
                )
                Text(
                    text = "Solo se muestran recetas de $mealType.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurfaceVariant
                )
            }

            AppTextField(value = query, onValueChange = { query = it }, placeholder = "Buscar receta")

            LazyColumn(verticalArrangement = Arrangement.spacedBy(spacing_4)) {
                item { SectionTitle("Mis recetas · $mealType") }

                if (myRecipes.isEmpty()) {
                    item {
                        EmptySection(
                            "No tienes recetas personales clasificadas como $mealType."
                        )
                    }
                } else {
                    items(myRecipes, key = { it.id }) { recipe ->
                        UserRecipeCard(recipe = recipe, onClick = { onRecipeSelected(recipe.id) })
                    }
                }

                if (otherMyRecipes.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(spacing_2))
                        SectionTitle("Otras recetas personales")
                    }
                    items(otherMyRecipes, key = { it.id }) { recipe ->
                        UserRecipeCard(recipe = recipe, onClick = { onRecipeSelected(recipe.id) })
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(spacing_2))
                    SectionTitle("Catálogo · $mealType")
                }

                if (catalogRecipes.isEmpty()) {
                    item { EmptySection("No hay recetas del catálogo para $mealType.") }
                } else {
                    items(catalogRecipes, key = { it.id }) { recipe ->
                        CatalogRecipeCard(recipe = recipe, onClick = { onRecipeSelected(recipe.id) })
                    }
                }

                item { Spacer(modifier = Modifier.height(spacing_6)) }
            }
        }
    }
}

private fun UserRecipe.matchesMealType(mealType: String): Boolean =
    category.equals(mealType, ignoreCase = true) ||
        mealType.equals(this.mealType, ignoreCase = true)

// ─── Componentes privados ────────────────────────────────────────────────────

@Composable
private fun SectionTitle(title: String) {
    Text(text = title, style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.ExtraBold, color = Primary)
}

@Composable
private fun EmptySection(text: String) {
    EditorialCard {
        Text(text = text, style = MaterialTheme.typography.bodyMedium, color = OnSurfaceVariant)
    }
}

@Composable
private fun UserRecipeCard(recipe: UserRecipe, onClick: () -> Unit) {
    RecipeCard(
        title    = recipe.title,
        subtitle = recipe.category.ifBlank { "Receta personal" },
        info     = "${recipe.timeInMinutes} min · ${recipe.difficulty}",
        imageRes = recipe.imageRes,
        onClick  = onClick
    )
}

@Composable
private fun CatalogRecipeCard(recipe: Recipe, onClick: () -> Unit) {
    RecipeCard(
        title    = recipe.title,
        subtitle = recipe.category,
        info     = "${recipe.timeInMinutes} min · ${recipe.difficulty}",
        imageRes = "",
        onClick  = onClick
    )
}

@Composable
private fun RecipeCard(
    title: String,
    subtitle: String,
    info: String,
    imageRes: String,
    onClick: () -> Unit
) {
    EditorialCard(modifier = Modifier.clickable(onClick = onClick)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing_4),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Miniatura
            if (imageRes.isNotBlank()) {
                val model: Any = if (imageRes.startsWith("http")) imageRes else File(imageRes)
                AsyncImage(
                    model = model,
                    contentDescription = title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(rounded_lg))
                        .background(PrimaryContainer.copy(alpha = 0.15f))
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(rounded_lg))
                        .background(PrimaryContainer.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.RestaurantMenu, contentDescription = null,
                        tint = Primary, modifier = Modifier.size(24.dp))
                }
            }

            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(spacing_2)) {
                Text(text = title, style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold, color = OnSurface)
                Text(text = subtitle, style = MaterialTheme.typography.bodyMedium, color = OnSurfaceVariant)
                Text(text = info, style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold, color = Primary)
            }
        }
    }
}
