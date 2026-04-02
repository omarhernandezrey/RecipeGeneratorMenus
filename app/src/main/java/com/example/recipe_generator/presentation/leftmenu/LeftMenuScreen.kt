package com.example.recipe_generator.presentation.leftmenu

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.recipe_generator.presentation.theme.OnPrimary
import com.example.recipe_generator.presentation.theme.OnSurface
import com.example.recipe_generator.presentation.theme.OnSurfaceVariant
import com.example.recipe_generator.presentation.theme.Primary
import com.example.recipe_generator.presentation.theme.PrimaryContainer
import com.example.recipe_generator.presentation.theme.Surface
import com.example.recipe_generator.presentation.theme.SurfaceContainerLow
import com.example.recipe_generator.presentation.theme.SurfaceContainerLowest
import com.example.recipe_generator.presentation.theme.rounded_lg
import com.example.recipe_generator.presentation.theme.rounded_md
import com.example.recipe_generator.presentation.theme.spacing_2
import com.example.recipe_generator.presentation.theme.spacing_3
import com.example.recipe_generator.presentation.theme.spacing_4
import com.example.recipe_generator.presentation.theme.spacing_6

/**
 * LeftMenuScreen — Panel izquierdo de navegación (30% del ancho).
 *
 * LazyColumn con 5 ítems del menú izquierdo.
 * Click en ítem → actualiza selectedPanel → cambia contenido del panel derecho.
 * Cubre LF6: Fragment con ComposeView como host.
 * Cubre LF8: LazyColumn (ListView), click items.
 *
 * Capa: Presentation
 */

enum class LeftMenuPanel(
    val label: String,
    val icon: ImageVector,
    val tag: String
) {
    Profile("Perfil", Icons.Outlined.Person, "LF7"),
    Photos("Fotos", Icons.Outlined.PhotoLibrary, "LF7"),
    Video("Video", Icons.Outlined.PlayCircle, "LF7"),
    Web("Web", Icons.Outlined.Language, "LF7"),
    Controls("Controles", Icons.Outlined.Build, "LF8")
}

@Composable
fun LeftMenuScreen(
    selectedPanel: LeftMenuPanel,
    onPanelSelected: (LeftMenuPanel) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .background(SurfaceContainerLowest)
    ) {
        // Encabezado del menú
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(PrimaryContainer.copy(alpha = 0.4f))
                .padding(vertical = spacing_6, horizontal = spacing_4)
        ) {
            Text(
                text = "Demos",
                style = MaterialTheme.typography.titleSmall,
                color = Primary,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        // LF8: LazyColumn — equivalente de ListView
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            itemsIndexed(LeftMenuPanel.entries) { _, panel ->
                LeftMenuItemRow(
                    panel = panel,
                    isSelected = panel == selectedPanel,
                    onClick = { onPanelSelected(panel) }
                )
            }
        }
    }
}

@Composable
private fun LeftMenuItemRow(
    panel: LeftMenuPanel,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) Primary else Color.Transparent,
        animationSpec = tween(200),
        label = "menuItemBg"
    )
    val textColor by animateColorAsState(
        targetValue = if (isSelected) OnPrimary else OnSurface,
        animationSpec = tween(200),
        label = "menuItemText"
    )
    val iconColor by animateColorAsState(
        targetValue = if (isSelected) OnPrimary else OnSurfaceVariant,
        animationSpec = tween(200),
        label = "menuItemIcon"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = spacing_3, vertical = 2.dp)
            .clip(RoundedCornerShape(rounded_md))
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(horizontal = spacing_4, vertical = spacing_4)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(spacing_2)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(
                        if (isSelected) OnPrimary.copy(alpha = 0.2f)
                        else SurfaceContainerLow,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = panel.icon,
                    contentDescription = panel.label,
                    tint = iconColor,
                    modifier = Modifier.size(18.dp)
                )
            }
            Text(
                text = panel.label,
                style = MaterialTheme.typography.labelSmall,
                color = textColor,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 10.sp
            )
            // Badge LF
            Box(
                modifier = Modifier
                    .background(
                        if (isSelected) OnPrimary.copy(alpha = 0.25f)
                        else PrimaryContainer.copy(alpha = 0.5f),
                        RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 4.dp, vertical = 1.dp)
            ) {
                Text(
                    text = panel.tag,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isSelected) OnPrimary else Primary,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
