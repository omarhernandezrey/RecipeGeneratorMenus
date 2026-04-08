package com.example.recipe_generator.presentation.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.AlternateEmail
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.LocalDining
import androidx.compose.material.icons.outlined.RestaurantMenu
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Stars
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.recipe_generator.RecipeGeneratorApp
import com.example.recipe_generator.domain.model.User
import com.example.recipe_generator.domain.model.UserProfile
import com.example.recipe_generator.domain.model.WeeklyPlan
import com.example.recipe_generator.presentation.theme.OnPrimary
import com.example.recipe_generator.presentation.theme.OnSurface
import com.example.recipe_generator.presentation.theme.OnSurfaceVariant
import com.example.recipe_generator.presentation.theme.Primary
import com.example.recipe_generator.presentation.theme.PrimaryContainer
import com.example.recipe_generator.presentation.theme.Secondary
import com.example.recipe_generator.presentation.theme.SecondaryContainer
import com.example.recipe_generator.presentation.theme.Surface
import com.example.recipe_generator.presentation.theme.SurfaceContainerLow
import com.example.recipe_generator.presentation.theme.SurfaceContainerLowest
import com.example.recipe_generator.presentation.theme.rounded_full
import com.example.recipe_generator.presentation.theme.rounded_lg
import com.example.recipe_generator.presentation.theme.rounded_md
import com.example.recipe_generator.presentation.theme.spacing_10
import com.example.recipe_generator.presentation.theme.spacing_12
import com.example.recipe_generator.presentation.theme.spacing_2
import com.example.recipe_generator.presentation.theme.spacing_3
import com.example.recipe_generator.presentation.theme.spacing_4
import com.example.recipe_generator.presentation.theme.spacing_6
import com.example.recipe_generator.presentation.theme.spacing_8
import kotlinx.coroutines.flow.flowOf

/**
 * ProfileScreen — Perfil real del usuario autenticado.
 *
 * Muestra nombre, correo y foto reales del usuario, además de estadísticas
 * calculadas desde los repositorios locales: recetas creadas, favoritos y
 * días del plan semanal con al menos una comida asignada.
 *
 * D-01
 * Capa: Presentation
 */
@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    onEditProfileClick: () -> Unit = {},
    onMyRecipesClick: () -> Unit = {},
    onWeeklyPlanClick: () -> Unit = {}
) {
    val appContainer = (LocalContext.current.applicationContext as RecipeGeneratorApp).container

    val currentUserFlow = remember(appContainer.authRepository) {
        appContainer.authRepository.getCurrentUser()
    }
    val currentUser by currentUserFlow.collectAsStateWithLifecycle(initialValue = null)

    val userId = currentUser?.uid

    val profileFlow = remember(userId, appContainer.userProfileRepository) {
        if (userId != null) appContainer.userProfileRepository.getProfile(userId) else flowOf(null)
    }
    val recipesFlow = remember(userId, appContainer.userRecipeRepository) {
        if (userId != null) appContainer.userRecipeRepository.getMyRecipes(userId) else flowOf(emptyList())
    }
    val weeklyPlanFlow = remember(userId, appContainer.weeklyPlanRepository) {
        if (userId != null) appContainer.weeklyPlanRepository.getWeeklyPlan(userId) else flowOf(emptyList())
    }
    val favoritesFlow = remember(userId, appContainer.favoritesRepository) {
        if (userId != null) appContainer.favoritesRepository.getFavoriteIds(userId) else flowOf(emptySet())
    }

    val profile by profileFlow.collectAsStateWithLifecycle(initialValue = null)
    val userRecipes by recipesFlow.collectAsStateWithLifecycle(initialValue = emptyList())
    val weeklyPlan by weeklyPlanFlow.collectAsStateWithLifecycle(initialValue = emptyList())
    val favoriteIds by favoritesFlow.collectAsStateWithLifecycle(initialValue = emptySet())

    val displayName = remember(profile, currentUser) {
        resolveDisplayName(profile, currentUser)
    }
    val email = remember(profile, currentUser) {
        resolveEmail(profile, currentUser)
    }
    val photoUrl = remember(profile, currentUser) {
        profile?.photoUrl?.takeIf { it.isNotBlank() } ?: currentUser?.photoUrl
    }
    val preferredDiets = remember(profile) { profile?.preferredDiets.orEmpty() }
    val defaultPortions = remember(profile) { profile?.defaultPortions ?: 2 }
    val plannedDaysCount = remember(weeklyPlan) {
        weeklyPlan.map(WeeklyPlan::dayOfWeek).distinct().size
    }

    ProfileScreenContent(
        modifier = modifier,
        displayName = displayName,
        email = email,
        photoUrl = photoUrl,
        preferredDiets = preferredDiets,
        defaultPortions = defaultPortions,
        recipesCount = userRecipes.size,
        favoritesCount = favoriteIds.size,
        plannedDaysCount = plannedDaysCount,
        onBack = onBack,
        onEditProfileClick = onEditProfileClick,
        onMyRecipesClick = onMyRecipesClick,
        onWeeklyPlanClick = onWeeklyPlanClick
    )
}

@Composable
private fun ProfileScreenContent(
    modifier: Modifier = Modifier,
    displayName: String,
    email: String,
    photoUrl: String?,
    preferredDiets: List<String>,
    defaultPortions: Int,
    recipesCount: Int,
    favoritesCount: Int,
    plannedDaysCount: Int,
    onBack: (() -> Unit)?,
    onEditProfileClick: () -> Unit,
    onMyRecipesClick: () -> Unit,
    onWeeklyPlanClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Surface)
            .verticalScroll(rememberScrollState())
            .padding(spacing_6),
        verticalArrangement = Arrangement.spacedBy(spacing_6)
    ) {
        Spacer(modifier = Modifier.height(spacing_2))

        if (onBack != null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
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
                    modifier = Modifier.padding(start = spacing_2)
                ) {
                    Text(
                        text = "Perfil",
                        style = MaterialTheme.typography.headlineSmall,
                        color = OnSurface,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "Accede a tu cuenta, recetas y plan semanal.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = OnSurfaceVariant
                    )
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(rounded_lg),
            colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(spacing_8),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(spacing_4)
            ) {
                ProfileAvatar(
                    photoUrl = photoUrl,
                    displayName = displayName
                )

                Text(
                    text = displayName,
                    style = MaterialTheme.typography.headlineMedium,
                    color = OnSurface,
                    fontWeight = FontWeight.ExtraBold
                )

                Text(
                    text = email,
                    style = MaterialTheme.typography.bodyLarge,
                    color = OnSurfaceVariant
                )

                Button(
                    onClick = onEditProfileClick,
                    shape = RoundedCornerShape(rounded_full),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Primary,
                        contentColor = OnPrimary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(spacing_3))
                    Text(
                        text = "Editar perfil",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing_4)
        ) {
            StatCard(
                modifier = Modifier.weight(1f),
                value = recipesCount.toString(),
                label = "Mis recetas",
                icon = Icons.Outlined.RestaurantMenu
            )
            StatCard(
                modifier = Modifier.weight(1f),
                value = favoritesCount.toString(),
                label = "Favoritos",
                icon = Icons.Outlined.Stars
            )
            StatCard(
                modifier = Modifier.weight(1f),
                value = plannedDaysCount.toString(),
                label = "Días con plan",
                icon = Icons.Outlined.Schedule
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing_4)
        ) {
            QuickActionCard(
                modifier = Modifier.weight(1f),
                title = "Mis recetas",
                subtitle = "Crear, editar y eliminar recetas personales",
                icon = Icons.Outlined.RestaurantMenu,
                onClick = onMyRecipesClick
            )
            QuickActionCard(
                modifier = Modifier.weight(1f),
                title = "Plan semanal",
                subtitle = "Asignar comidas para cada día de la semana",
                icon = Icons.Outlined.Schedule,
                onClick = onWeeklyPlanClick
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(rounded_lg),
            colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(spacing_6),
                verticalArrangement = Arrangement.spacedBy(spacing_4)
            ) {
                Text(
                    text = "CUENTA",
                    style = MaterialTheme.typography.labelSmall,
                    color = Primary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                )

                ProfileInfoRow(
                    icon = Icons.Outlined.AlternateEmail,
                    label = "Correo",
                    value = email
                )
                ProfileInfoRow(
                    icon = Icons.Outlined.Tune,
                    label = "Porciones por defecto",
                    value = "$defaultPortions porciones"
                )
                ProfileInfoRow(
                    icon = Icons.Outlined.LocalDining,
                    label = "Dietas preferidas",
                    value = preferredDiets.joinToString(", ").ifBlank { "Sin preferencias configuradas" }
                )
            }
        }
    }
}

@Composable
private fun QuickActionCard(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(rounded_md),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing_4, vertical = spacing_6),
            verticalArrangement = Arrangement.spacedBy(spacing_3)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(PrimaryContainer.copy(alpha = 0.16f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Primary,
                    modifier = Modifier.size(18.dp)
                )
            }
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = OnSurface,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = OnSurfaceVariant
            )
        }
    }
}

@Composable
private fun ProfileAvatar(
    photoUrl: String?,
    displayName: String
) {
    val remoteImage = rememberProfileImage(photoUrl)

    Box(
        modifier = Modifier
            .size(128.dp)
            .clip(CircleShape)
            .background(PrimaryContainer),
        contentAlignment = Alignment.Center
    ) {
        if (remoteImage != null) {
            Image(
                bitmap = remoteImage!!,
                contentDescription = "Foto de perfil",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Text(
                text = displayName.initials(),
                style = MaterialTheme.typography.displaySmall,
                color = OnPrimary,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Composable
private fun ProfileInfoRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = spacing_3),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spacing_4)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(SecondaryContainer.copy(alpha = 0.5f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Secondary,
                modifier = Modifier.size(20.dp)
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = OnSurfaceVariant,
                letterSpacing = 0.4.sp
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = OnSurface,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    value: String,
    label: String,
    icon: ImageVector
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(rounded_md),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing_4, vertical = spacing_6),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(spacing_2)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(PrimaryContainer.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Primary,
                    modifier = Modifier.size(18.dp)
                )
            }

            Text(
                text = value,
                style = MaterialTheme.typography.headlineLarge,
                color = Primary,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = OnSurfaceVariant,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

private fun resolveDisplayName(profile: UserProfile?, currentUser: User?): String =
    profile?.displayName?.takeIf { it.isNotBlank() }
        ?: currentUser?.displayName?.takeIf { it.isNotBlank() }
        ?: currentUser?.email?.substringBefore("@")
        ?: "Usuario"

private fun resolveEmail(profile: UserProfile?, currentUser: User?): String =
    profile?.email?.takeIf { it.isNotBlank() }
        ?: currentUser?.email
        ?: "Sin correo registrado"

private fun String.initials(): String =
    trim()
        .split(Regex("\\s+"))
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") { it.take(1) }
        .uppercase()
        .ifBlank { "U" }
