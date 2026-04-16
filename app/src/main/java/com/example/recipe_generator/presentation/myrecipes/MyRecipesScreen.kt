package com.example.recipe_generator.presentation.myrecipes

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
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.recipe_generator.RecipeGeneratorApp
import com.example.recipe_generator.domain.model.UserRecipe
import com.example.recipe_generator.presentation.components.EditorialCard
import com.example.recipe_generator.presentation.components.RecipeImage
import com.example.recipe_generator.presentation.components.editorialFabBottomPadding
import com.example.recipe_generator.presentation.theme.OnSurface
import com.example.recipe_generator.presentation.theme.OnSurfaceVariant
import com.example.recipe_generator.presentation.theme.Primary
import com.example.recipe_generator.presentation.theme.Surface
import com.example.recipe_generator.presentation.theme.SurfaceContainerLow
import com.example.recipe_generator.presentation.theme.rounded_full
import com.example.recipe_generator.presentation.theme.rounded_lg
import com.example.recipe_generator.presentation.theme.rounded_md
import com.example.recipe_generator.presentation.theme.spacing_10
import com.example.recipe_generator.presentation.theme.spacing_2
import com.example.recipe_generator.presentation.theme.spacing_3
import com.example.recipe_generator.presentation.theme.spacing_4
import com.example.recipe_generator.presentation.theme.spacing_6
import kotlinx.coroutines.launch

private val ErrorRed = Color(0xFFBA1A1A)

private sealed interface MyRecipesRoute {
    data object List   : MyRecipesRoute
    data object Create : MyRecipesRoute
    data object Search : MyRecipesRoute
    data class  Edit(val recipe: UserRecipe) : MyRecipesRoute
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

    when (val current = route) {
        MyRecipesRoute.Create -> CreateRecipeScreen(
            onBack  = { route = MyRecipesRoute.List },
            onSaved = { route = MyRecipesRoute.List }
        )
        is MyRecipesRoute.Edit -> EditRecipeScreen(
            recipe  = current.recipe,
            onBack  = { route = MyRecipesRoute.List },
            onSaved = { route = MyRecipesRoute.List }
        )
        MyRecipesRoute.Search -> RecipeSearchScreen(
            userId               = userId,
            userRecipeRepository = appContainer.userRecipeRepository,
            firestoreSyncService = appContainer.firestoreSyncService,
            resolveRecipeVideoUseCase = appContainer.resolveRecipeVideoUseCase,
            onBack               = { route = MyRecipesRoute.List },
            onImported           = { route = MyRecipesRoute.List }
        )
        MyRecipesRoute.List -> MyRecipesListContent(
            modifier        = modifier,
            onBack          = onBack,
            showHeader      = !embeddedMode,
            onCreateRecipe  = { route = MyRecipesRoute.Create },
            onSearchRecipes = { route = MyRecipesRoute.Search },
            onEditRecipe    = { recipe -> route = MyRecipesRoute.Edit(recipe) }
        )
    }
}

// ── List content ─────────────────────────────────────────────────────

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

    val fabBottomPadding = editorialFabBottomPadding()

    Box(modifier = modifier.fillMaxSize().background(Surface)) {

        Column(modifier = Modifier.fillMaxSize()) {

            // ── Header (standalone mode) ───────────────────────────
            if (showHeader) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = spacing_4, vertical = spacing_4),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Volver", tint = OnSurface)
                    }
                    Column(modifier = Modifier.padding(start = spacing_2).weight(1f)) {
                        Text("Mis recetas", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold, color = OnSurface)
                        Text("${recipes.size} recetas personales", style = MaterialTheme.typography.bodyMedium, color = OnSurfaceVariant)
                    }
                    IconButton(onClick = onSearchRecipes) {
                        Icon(Icons.Outlined.TravelExplore, "Buscar en internet", tint = Primary)
                    }
                }
            }

            // ── Mini info bar (embedded mode) ─────────────────────
            if (!showHeader) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = spacing_6, vertical = spacing_2),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (recipes.isEmpty()) "Sin recetas aún" else "${recipes.size} receta${if (recipes.size != 1) "s" else ""} personales",
                        style = MaterialTheme.typography.bodyMedium,
                        color = OnSurfaceVariant
                    )
                    IconButton(onClick = onSearchRecipes) {
                        Icon(Icons.Outlined.TravelExplore, "Buscar en internet", tint = Primary)
                    }
                }
            }

            // ── Content ──────────────────────────────────────────
            if (recipes.isEmpty()) {
                EmptyRecipesState(
                    modifier        = Modifier.fillMaxSize().padding(horizontal = spacing_6),
                    onCreateRecipe  = onCreateRecipe,
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
                            recipe   = recipe,
                            onEdit   = { onEditRecipe(recipe) },
                            onDelete = { pendingDelete = recipe }
                        )
                    }

                    // Espacio para que el último card no quede detrás del FAB + nav
                    item { Spacer(modifier = Modifier.height(fabBottomPadding + spacing_10)) }
                }
            }
        }

        // ── FAB (siempre por encima del nav bar) ──────────────────
        FloatingActionButton(
            onClick        = onCreateRecipe,
            modifier       = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = spacing_6, bottom = fabBottomPadding + spacing_4),
            containerColor = Primary,
            shape          = RoundedCornerShape(rounded_full)
        ) {
            Icon(Icons.Outlined.Add, "Crear receta", tint = Color.White)
        }
    }

    // ── Delete dialog ─────────────────────────────────────────────
    if (pendingDelete != null) {
        AlertDialog(
            onDismissRequest = { pendingDelete = null },
            title   = { Text("Eliminar receta") },
            text    = { Text("La receta desaparecerá de Mis Recetas y del plan semanal si estaba asignada.") },
            confirmButton = {
                TextButton(onClick = {
                    val recipe = pendingDelete ?: return@TextButton
                    pendingDelete = null
                    coroutineScope.launch {
                        appContainer.userRecipeRepository.deleteRecipe(recipe)
                        appContainer.firestoreSyncService.deleteRecipeFromCloud(recipe.userId, recipe.id)
                    }
                }) { Text("Eliminar", color = ErrorRed) }
            },
            dismissButton = {
                TextButton(onClick = { pendingDelete = null }) { Text("Cancelar") }
            }
        )
    }
}

// ── Recipe card ──────────────────────────────────────────────────────

@Composable
private fun RecipeSummaryCard(
    recipe: UserRecipe,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    EditorialCard(modifier = Modifier.padding(horizontal = spacing_6)) {

        // Imagen
        if (recipe.imageRes.isNotBlank()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(rounded_lg))
            ) {
                RecipeImage(
                    recipeTitle = recipe.title,
                    imageRes = recipe.imageRes,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Spacer(modifier = Modifier.height(spacing_4))
        }

        // Chips de metadata
        val chips = buildList {
            if (recipe.category.isNotBlank()) add(recipe.category)
            if (recipe.mealType.isNotBlank())  add(recipe.mealType)
            if (recipe.dayOfWeek.isNotBlank()) add(recipe.dayOfWeek)
        }
        if (chips.isNotEmpty()) {
            Row(horizontalArrangement = Arrangement.spacedBy(spacing_2)) {
                chips.forEach { MetaChip(it) }
            }
            Spacer(modifier = Modifier.height(spacing_2))
        }

        // Título
        Text(
            text       = recipe.title,
            style      = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            color      = OnSurface
        )

        // Stats
        if (recipe.timeInMinutes > 0 || recipe.calories > 0) {
            Spacer(modifier = Modifier.height(spacing_3))
            Row(horizontalArrangement = Arrangement.spacedBy(spacing_6), verticalAlignment = Alignment.CenterVertically) {
                if (recipe.timeInMinutes > 0) StatItem("⏱", "${recipe.timeInMinutes} min")
                if (recipe.calories > 0)      StatItem("🔥", "${recipe.calories} cal")
            }
        }

        Spacer(modifier = Modifier.height(spacing_4))

        // Divider
        HorizontalDivider(color = OnSurface.copy(alpha = 0.08f), thickness = 1.dp)

        Spacer(modifier = Modifier.height(spacing_3))

        // Botones de acción — dos columnas iguales
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing_3)
        ) {
            CardActionButton(
                label          = "Editar",
                icon           = Icons.Outlined.Edit,
                containerColor = Primary.copy(alpha = 0.08f),
                contentColor   = Primary,
                onClick        = onEdit,
                modifier       = Modifier.weight(1f)
            )
            CardActionButton(
                label          = "Eliminar",
                icon           = Icons.Outlined.DeleteOutline,
                containerColor = ErrorRed.copy(alpha = 0.07f),
                contentColor   = ErrorRed,
                onClick        = onDelete,
                modifier       = Modifier.weight(1f)
            )
        }
    }
}

// ── Card helpers ─────────────────────────────────────────────────────

@Composable
private fun MetaChip(text: String) {
    Box(
        modifier = Modifier
            .background(Primary.copy(alpha = 0.08f), RoundedCornerShape(rounded_full))
            .padding(horizontal = spacing_3, vertical = 4.dp)
    ) {
        Text(text, fontSize = 11.sp, color = Primary, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun StatItem(emoji: String, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(emoji, fontSize = 14.sp)
        Text(label, fontSize = 13.sp, color = OnSurfaceVariant, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun CardActionButton(
    label: String,
    icon: ImageVector,
    containerColor: Color,
    contentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .height(40.dp)
            .clip(RoundedCornerShape(rounded_md))
            .background(containerColor)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(icon, null, tint = contentColor, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(label, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = contentColor)
    }
}

// ── Empty state ──────────────────────────────────────────────────────

@Composable
private fun EmptyRecipesState(
    modifier: Modifier,
    onCreateRecipe: () -> Unit,
    onSearchRecipes: () -> Unit
) {
    Column(
        modifier                = modifier,
        horizontalAlignment     = Alignment.CenterHorizontally,
        verticalArrangement     = Arrangement.Center
    ) {
        Box(
            modifier            = Modifier
                .size(80.dp)
                .background(Primary.copy(alpha = 0.08f), RoundedCornerShape(rounded_lg)),
            contentAlignment    = Alignment.Center
        ) {
            Icon(Icons.Outlined.RestaurantMenu, null, tint = Primary, modifier = Modifier.size(40.dp))
        }

        Spacer(modifier = Modifier.height(spacing_4))

        Text(
            text       = "Aún no tienes recetas",
            style      = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            color      = OnSurface
        )
        Spacer(modifier = Modifier.height(spacing_2))
        Text(
            text  = "Crea tu propia receta o importa una receta internacional.",
            style = MaterialTheme.typography.bodyMedium,
            color = OnSurfaceVariant
        )

        Spacer(modifier = Modifier.height(spacing_6))

        // Botón primario
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(rounded_full))
                .background(Primary)
                .clickable(onClick = onCreateRecipe),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Outlined.Add, null, tint = Color.White, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(spacing_2))
            Text("Crear receta", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }

        Spacer(modifier = Modifier.height(spacing_3))

        // Botón secundario
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(rounded_full))
                .background(SurfaceContainerLow)
                .clickable(onClick = onSearchRecipes),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Outlined.TravelExplore, null, tint = Primary, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(spacing_2))
            Text("Buscar en internet", color = Primary, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
        }
    }
}
