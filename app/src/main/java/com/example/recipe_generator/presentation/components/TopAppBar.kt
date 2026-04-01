package com.example.recipe_generator.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.recipe_generator.presentation.theme.Background
import com.example.recipe_generator.presentation.theme.OnSurfaceVariant
import com.example.recipe_generator.presentation.theme.Primary
import com.example.recipe_generator.presentation.theme.PrimaryFixedDim
import com.example.recipe_generator.presentation.theme.spacing_3
import com.example.recipe_generator.presentation.theme.spacing_4
import com.example.recipe_generator.presentation.theme.spacing_6

private val TopBarSlotWidth = 84.dp
private val TopBarRowHeight = 64.dp

@Composable
fun editorialTopBarContentPadding(): Dp {
    return WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + TopBarRowHeight + spacing_4
}

@Composable
fun EditorialTopAppBar(
    title: String,
    modifier: Modifier = Modifier,
    leadingContent: @Composable (() -> Unit)? = null,
    trailingContent: @Composable (RowScope.() -> Unit)? = null,
    containerColor: Color = Background.copy(alpha = 0.92f)
) {
    val topInset = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(containerColor)
            .padding(top = topInset)
            .height(TopBarRowHeight)
            .padding(horizontal = spacing_4),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(TopBarSlotWidth)
                .height(TopBarRowHeight),
            contentAlignment = Alignment.CenterStart
        ) {
            leadingContent?.invoke()
        }

        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = Primary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Row(
            modifier = Modifier
                .width(TopBarSlotWidth)
                .height(TopBarRowHeight),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            trailingContent?.invoke(this)
        }
    }
}

@Composable
fun DefaultProfileAvatar(
    modifier: Modifier = Modifier
) {
    InitialProfileAvatar(initial = "U", modifier = modifier)
}

@Composable
fun InitialProfileAvatar(
    initial: String = "J",
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(PrimaryFixedDim.copy(alpha = 0.55f)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initial,
            style = MaterialTheme.typography.labelLarge,
            color = Primary,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun DefaultNotificationsButton(
    onClick: () -> Unit = {}
) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = Icons.Outlined.Notifications,
            contentDescription = "Notificaciones",
            tint = OnSurfaceVariant,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun HomeEditorialTopAppBar(
    title: String
) {
    EditorialTopAppBar(
        title = title,
        leadingContent = {
            DefaultProfileAvatar()
        },
        trailingContent = {
            DefaultNotificationsButton()
        }
    )
}

@Composable
fun DetailEditorialTopAppBar(
    title: String,
    leadingContent: @Composable (() -> Unit),
    onNotificationsClick: () -> Unit = {}
) {
    EditorialTopAppBar(
        title = title,
        leadingContent = leadingContent,
        trailingContent = {
            DefaultNotificationsButton(onClick = onNotificationsClick)
            Box(modifier = Modifier.width(spacing_3))
            InitialProfileAvatar()
        }
    )
}
