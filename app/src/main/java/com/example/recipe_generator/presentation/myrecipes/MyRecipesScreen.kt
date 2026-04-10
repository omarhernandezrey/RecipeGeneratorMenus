package com.example.recipe_generator.presentation.myrecipes

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.RestaurantMenu
import androidx.compose.material.icons.outlined.TravelExplore
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.recipe_generator.RecipeGeneratorApp
import com.example.recipe_generator.domain.model.UserRecipe
import com.example.recipe_generator.presentation.components.EditorialCard
import com.example.recipe_generator.presentation.profile.rememberProfileImage
import com.example.recipe_generator.presentation.theme.OnSurface
import com.example.recipe_generator.presentation.theme.OnSurfaceVariant
import com.example.recipe_generator.presentation.theme.Primary
import com.example.recipe_generator.presentation.theme.PrimaryContainer
import com.example.recipe_generator.presentation.theme.Surface
import com.example.recipe_generator.presentation.theme.rounded_full
import com.example.recipe_generator.presentation.theme.rounded_lg
import com.example.recipe_generator.presentation.theme.spacing_10
import com.example.recipe_generator.presentation.theme.spacing_2
import com.example.recipe_generator.presentation.theme.spacing_3
import com.example.recipe_generator.presentation.theme.spacing_4
import com.example.recipe_generator.presentation.theme.spacing_6
import kotlinx.coroutines.launch

private sealed interface MyRecipesRoute {
    data object List : MyRecipesRoute
    data object Create : MyRecipesRoute
    data object Search : MyRecipesRoute
    data class Edit(val recipe: UserRecipe) : MyRecipesRoute
}

@Composable
fun MyRecipesScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    embeddedMode: Boolean = false
) {
    val appContainer = (LocalContext.current.applicationContext as RecipeGeneratorApp).container
    val userId = remember(appContainer) { appContainer.requireAuthenticatedUserId() }
    var route by remember { mutableStateOf<MyRecipesRoute>(MyRecipesRoute.List) }

    when (val currentRoute = route) {
        MyRecipesRoute.Create -> CreateRecipeScreen(
            onBack = { route = MyRecipesRoute.List },
            onSaved = { route = MyRecipesRoute.List }
        )

        is MyRecipesRoute.Edit -> EditRecipeScreen(
            recipe = currentRoute.recipe,
            onBack = { route = MyRecipesRoute.List },
            onSaved = { route = MyRecipesRoute.List }
        )

        MyRecipesRoute.Search -> RecipeSearchScreen(
            userId = userId,
            userRecipeRepository = appContainer.userRecipeRepository,
            firestoreSyncService = appContainer.firestoreSyncService,
            onBack = { route = MyRecipesRoute.List },
            onImported = { route = MyRecipesRoute.List }
        )

        MyRecipesRoute.List -> MyRecipesListContent(
            modifier = modifier,
            onBack = onBack,
            showHeader = !embeddedMode,
            onCreateRecipe = { route = MyRecipesRoute.Create },
            onSearchRecipes = { route = MyRecipesRoute.Search },
            onEditRecipe = { recipe -> route = MyRecipesRoute.Edit(recipe) }
        )
    }
}

@Composable
private fun MyRecipesListContent(
    modifier: Modifier,
    onBack: () -> Unit,
    showHeader: Boolean = true,
    onCreateRecipe: () -> Unit,
    onSearchRecipes: () -> Unit,
    onEditRecipe: (UserRecipe) -> Unit
) {
    val appContainer = (LocalContext.current.applicationContext as RecipeGeneratorApp).container
    val userId = remember(appContainer) { appContainer.requireAuthenticatedUserId() }
    val recipes by appContainer.userRecipeRepository
        .getMyRecipes(userId)
        .collectAsStateWithLifecycle(initialValue = emptyList())

    val coroutineScope = rememberCoroutineScope()
    var pendingDelete by remember { mutableStateOf<UserRecipe?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Surface)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            if (showHeader) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = spacing_4, vertical = spacing_4),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Volver",
                            tint = OnSurface
                        )
                    }
                    Column(
                        modifier = Modifier
                            .padding(start = spacing_2)
                            .weight(1f)
                    ) {
                        Text(
                            text = "Mis recetas",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = OnSurface
                        )
                        Text(
                            text = "${recipes.size} recetas creadas por el usuario",
                            style = MaterialTheme.typography.bodyMedium,
                            color = OnSurfaceVariant
                        )
                    }
                    IconButton(onClick = onSearchRecipes) {
                        Icon(
                            imageVector = Icons.Outlined.TravelExplore,
                            contentDescription = "Buscar recetas en internet",
                            tint = Primary
                        )
                    }
                }
            }

            if (recipes.isEmpty()) {
                EmptyRecipesState(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = spacing_6),
                    onCreateRecipe = onCreateRecipe,
                    onSearchRecipes = onSearchRecipes
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(spacing_4)
                ) {
                    item { Spacer(modifier = Modifier.height(spacing_2)) }

                    items(recipes, key = { it.id }) { recipe ->
                        RecipeSummaryCard(
                            recipe = recipe,
                            onEdit = { onEditRecipe(recipe) },
                            onDelete = { pendingDelete = recipe }
                        )
                    }

                    item { Spacer(modifier = Modifier.height(spacing_10 * 2)) }
                }
            }
        }

        FloatingActionButton(
            onClick = onCreateRecipe,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(spacing_6),
            containerColor = Primary,
            shape = RoundedCornerShape(rounded_full)
        ) {
            Icon(
                imageVector = Icons.Outlined.Add,
                contentDescription = "Crear receta"
            )
        }
    }

    if (pendingDelete != null) {
        AlertDialog(
            onDismissRequest = { pendingDelete = null },
            title = { Text("Eliminar receta") },
            text = {
                Text("La receta desaparecerá de Mis Recetas y del plan semanal si estaba asignada.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val recipe = pendingDelete ?: return@TextButton
                        pendingDelete = null
                        coroutineScope.launch {
                            appContainer.userRecipeRepository.deleteRecipe(recipe)
                            appContainer.firestoreSyncService.deleteRecipeFromCloud(recipe.userId, recipe.id)
                        }
                    }
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingDelete = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun RecipeSummaryCard(
    recipe: UserRecipe,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val image = rememberProfileImage(recipe.imageRes.takeIf { it.isNotBlank() })

    EditorialCard(
        modifier = Modifier.padding(horizontal = spacing_6)
    ) {
        // Thumbnail si hay imagen
        if (image != null) {
            Image(
                bitmap = image,
                contentDescription = "Imagen de ${recipe.title}",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(rounded_lg)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(spacing_4))
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(spacing_3)
        ) {
            Text(
                text = recipe.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = OnSurface
            )

            Text(
                text = buildString {
                    append(recipe.category.ifBlank { "Sin categoría" })
                    if (recipe.dayOfWeek.isNotBlank()) {
                        append(" · ")
                        append(recipe.dayOfWeek)
                    }
                    if (recipe.mealType.isNotBlank()) {
                        append(" · ")
                        append(recipe.mealType)
                    }
                },
                style = MaterialTheme.typography.bodyMedium,
                color = OnSurfaceVariant
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${recipe.timeInMinutes} min · ${recipe.calories} cal",
                    style = MaterialTheme.typography.labelLarge,
                    color = Primary,
                    fontWeight = FontWeight.SemiBold
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(spacing_2)
                ) {
                    ActionPill(
                        label = "Editar",
                        icon = Icons.Outlined.Edit,
                        onClick = onEdit
                    )
                    ActionPill(
                        label = "Eliminar",
                        icon = Icons.Outlined.DeleteOutline,
                        onClick = onDelete
                    )
                }
            }
        }
    }
}

@Composable
private fun ActionPill(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .background(
                color = Primary.copy(alpha = 0.08f),
                shape = RoundedCornerShape(rounded_full)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = spacing_3, vertical = spacing_2),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spacing_2)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Primary,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = Primary
        )
    }
}

@Composable
private fun EmptyRecipesState(
    modifier: Modifier,
    onCreateRecipe: () -> Unit,
    onSearchRecipes: () -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.RestaurantMenu,
            contentDescription = null,
            tint = Primary,
            modifier = Modifier.size(56.dp)
        )
        Spacer(modifier = Modifier.height(spacing_4))
        Text(
            text = "Todavía no hay recetas personales",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            color = OnSurface
        )
        Spacer(modifier = Modifier.height(spacing_2))
        Text(
            text = "Crea tu propia receta o busca e importa una receta internacional.",
            style = MaterialTheme.typography.bodyLarge,
            color = OnSurfaceVariant
        )
        Spacer(modifier = Modifier.height(spacing_6))
        Row(horizontalArrangement = Arrangement.spacedBy(spacing_3)) {
            TextButton(onClick = onCreateRecipe) {
                Text("Crear receta")
            }
            TextButton(onClick = onSearchRecipes) {
                Icon(
                    imageVector = Icons.Outlined.TravelExplore,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(spacing_2))
                Text("Buscar receta")
            }
        }
    }
}
