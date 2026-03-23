package com.example.recipe_generator.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.recipe_generator.ui.theme.Background
import com.example.recipe_generator.ui.theme.OnSurfaceVariant
import com.example.recipe_generator.ui.theme.Primary
import com.example.recipe_generator.ui.theme.PrimaryFixedDim
import com.example.recipe_generator.ui.theme.SurfaceContainer
import com.example.recipe_generator.ui.theme.spacing_3
import com.example.recipe_generator.ui.theme.spacing_4
import com.example.recipe_generator.ui.theme.spacing_6

private const val PROFILE_IMAGE_URL = "https://lh3.googleusercontent.com/aida-public/AB6AXuB2xtj_r97gsEUurBmuOwkrxnpW7yFeqbQN49f2Q79dIXXT3KFVXeIrQYLSYkUT_TrcscsTFavakiUZ_SKEOnTS-t8yDUZ5Nk2sh8TR1sSgmFlPphMmtbSvy4Gs81b8aaCXpo_JpPWBRZIWe6CNJ4d0rMGaUqI1arpd0k-UxOq2s8N1yD8P_bYtik4H5hSLeTp7dSRrkcOVhoQlK3CNBReGwEWVL741RcR7k91urFLVQXuqDRlBUmY9T6jfUa37KCR9ePusAJ272j4"
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
    modifier: Modifier = Modifier,
    imageUrl: String = PROFILE_IMAGE_URL
) {
    val context = LocalContext.current

    Box(
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(SurfaceContainer)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(imageUrl)
                .crossfade(true)
                .build(),
            contentDescription = "Perfil",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
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
