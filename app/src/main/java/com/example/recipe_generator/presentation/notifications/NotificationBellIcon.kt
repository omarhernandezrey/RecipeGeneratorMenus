package com.example.recipe_generator.presentation.notifications

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.recipe_generator.presentation.theme.Primary

@Composable
fun NotificationBellIcon(
    unreadCount: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val rotation = remember { Animatable(0f) }

    // Wobble loop only when there are unread notifications
    LaunchedEffect(unreadCount) {
        if (unreadCount > 0) {
            rotation.animateTo(
                targetValue = 0f,
                animationSpec = infiniteRepeatable(
                    animation = keyframes {
                        durationMillis = 1200
                        0f   at 0    with FastOutSlowInEasing
                        18f  at 100  with FastOutSlowInEasing
                        -16f at 200  with FastOutSlowInEasing
                        14f  at 300  with FastOutSlowInEasing
                        -10f at 400  with FastOutSlowInEasing
                        6f   at 500  with FastOutSlowInEasing
                        -3f  at 600  with FastOutSlowInEasing
                        0f   at 700  with FastOutSlowInEasing
                        // silence until next cycle
                        0f   at 1200 with FastOutSlowInEasing
                    },
                    repeatMode = RepeatMode.Restart
                )
            )
        } else {
            rotation.animateTo(0f)
        }
    }

    Box(modifier = modifier) {
        IconButton(
            onClick = onClick,
            modifier = Modifier
                .size(44.dp)
                .shadow(elevation = 4.dp, shape = CircleShape)
                .background(Color.White.copy(alpha = 0.95f), CircleShape)
        ) {
            Icon(
                imageVector        = Icons.Filled.Notifications,
                contentDescription = "Notificaciones",
                tint               = if (unreadCount > 0) Primary else Color(0xFF888888),
                modifier           = Modifier
                    .size(24.dp)
                    .rotate(rotation.value)
            )
        }

        if (unreadCount > 0) {
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = 4.dp, y = (-2).dp)
                    .background(Color(0xFFE53935), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text       = if (unreadCount > 9) "9+" else unreadCount.toString(),
                    color      = Color.White,
                    fontSize   = 9.sp,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines   = 1
                )
            }
        }
    }
}
