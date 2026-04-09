package com.example.recipe_generator.presentation.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.recipe_generator.presentation.myrecipes.MyRecipesScreen
import com.example.recipe_generator.presentation.weeklyplan.MyWeeklyPlanScreen

private enum class ProfileHubRoute {
    Overview,
    EditProfile,
    MyRecipes,
    WeeklyPlan
}

@Composable
fun ProfileHubScreen(
    modifier: Modifier = Modifier,
    onClose: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    var route by remember { mutableStateOf(ProfileHubRoute.Overview) }

    when (route) {
        ProfileHubRoute.Overview -> ProfileScreen(
            modifier = modifier,
            onBack = onClose,
            onEditProfileClick = { route = ProfileHubRoute.EditProfile },
            onMyRecipesClick = { route = ProfileHubRoute.MyRecipes },
            onWeeklyPlanClick = { route = ProfileHubRoute.WeeklyPlan },
            onLogout = onLogout
        )

        ProfileHubRoute.EditProfile -> EditProfileScreen(
            modifier = modifier,
            onBack = { route = ProfileHubRoute.Overview },
            onSaved = { route = ProfileHubRoute.Overview }
        )

        ProfileHubRoute.MyRecipes -> MyRecipesScreen(
            modifier = modifier,
            onBack = { route = ProfileHubRoute.Overview }
        )

        ProfileHubRoute.WeeklyPlan -> MyWeeklyPlanScreen(
            modifier = modifier,
            onBack = { route = ProfileHubRoute.Overview }
        )
    }
}
