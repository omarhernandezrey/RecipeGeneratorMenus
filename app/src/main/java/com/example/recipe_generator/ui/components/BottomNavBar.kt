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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.recipe_generator.ui.theme.*

data class NavItem(
    val label: String,
    val icon: ImageVector
)

@Composable
fun EditorialBottomNavBar(selectedItem: Int = 0, onItemSelected: (Int) -> Unit = {}) {
    val navItems = listOf(
        NavItem("Inicio", Icons.Filled.Home),
        NavItem("Favoritos", Icons.Filled.FavoriteBorder),
        NavItem("Generador", Icons.Filled.Star),
        NavItem("Ajustes", Icons.Filled.Settings)
    )

    // Matching Stitch: rounded-t-[2rem] bg-white/90 backdrop-blur shadow
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        color = Color.White.copy(alpha = 0.95f),
        shadowElevation = 8.dp,
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .padding(top = 12.dp, bottom = 28.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            navItems.forEachIndexed { index, item ->
                val isSelected = index == selectedItem
                // Each nav item as a column with proper touch target
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(rounded_full))
                        .then(
                            if (isSelected) {
                                Modifier.background(
                                    PrimaryFixedDim.copy(alpha = 0.25f),
                                    shape = RoundedCornerShape(rounded_full)
                                )
                            } else Modifier
                        )
                        .clickable { onItemSelected(index) }
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            modifier = Modifier.size(24.dp),
                            tint = if (isSelected) Primary else OnSurfaceVariant
                        )
                        Text(
                            text = item.label,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                            color = if (isSelected) Primary else OnSurfaceVariant,
                            letterSpacing = 0.3.sp,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}
