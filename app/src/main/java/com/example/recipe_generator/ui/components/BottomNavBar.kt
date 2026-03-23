package com.example.recipe_generator.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.recipe_generator.ui.theme.*

data class NavItem(
    val label: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector = icon
)

private val BottomBarCoreHeight = 56.dp
private val BottomBarTopCorner = 28.dp

@Composable
fun editorialBottomBarContentPadding(): Dp {
    val navigationInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    return BottomBarCoreHeight + (navigationInset * 0.35f)
}

@Composable
fun editorialFabBottomPadding(): Dp {
    return editorialBottomBarContentPadding() + 14.dp
}

@Composable
fun EditorialBottomNavBar(selectedItem: Int = 0, onItemSelected: (Int) -> Unit = {}) {
    val navItems = listOf(
        NavItem("Inicio", Icons.Filled.Home),
        NavItem("Favoritos", Icons.Filled.FavoriteBorder, Icons.Filled.Favorite),
        NavItem("Generador", Icons.Filled.Star),
        NavItem("Ajustes", Icons.Filled.Settings)
    )
    val navigationInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val containerHeight = BottomBarCoreHeight + navigationInset

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(containerHeight),
        shape = RoundedCornerShape(topStart = BottomBarTopCorner, topEnd = BottomBarTopCorner),
        color = Color.White.copy(alpha = 0.98f),
        shadowElevation = 12.dp,
        tonalElevation = 0.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                navItems.forEachIndexed { index, item ->
                    val isSelected = index == selectedItem

                    Box(
                        modifier = Modifier
                            .size(height = 42.dp, width = 54.dp)
                            .clip(RoundedCornerShape(rounded_full))
                            .then(
                                if (isSelected) {
                                    Modifier.background(
                                        PrimaryFixedDim.copy(alpha = 0.16f)
                                    )
                                } else Modifier
                            )
                            .clickable { onItemSelected(index) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isSelected) item.selectedIcon else item.icon,
                            contentDescription = item.label,
                            modifier = Modifier.size(24.dp),
                            tint = if (isSelected) Primary else OnSurfaceVariant.copy(alpha = 0.74f)
                        )
                    }
                }
            }
        }
    }
}
