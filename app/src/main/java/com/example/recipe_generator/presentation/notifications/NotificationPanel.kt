package com.example.recipe_generator.presentation.notifications

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.recipe_generator.domain.model.AppNotification
import com.example.recipe_generator.presentation.theme.OnSurface
import com.example.recipe_generator.presentation.theme.OnSurfaceVariant
import com.example.recipe_generator.presentation.theme.Primary
import com.example.recipe_generator.presentation.theme.Surface
import com.example.recipe_generator.presentation.theme.SurfaceContainerLow
import com.example.recipe_generator.presentation.theme.rounded_full
import com.example.recipe_generator.presentation.theme.rounded_xl
import com.example.recipe_generator.presentation.theme.spacing_2
import com.example.recipe_generator.presentation.theme.spacing_3
import com.example.recipe_generator.presentation.theme.spacing_4
import com.example.recipe_generator.presentation.theme.spacing_6
import com.example.recipe_generator.presentation.theme.spacing_8
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

@Composable
fun NotificationPanel(
    notifications: List<AppNotification>,
    onDelete: (String) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false)
    ) {
        var scrimVisible by remember { mutableStateOf(false) }
        var cardVisible  by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            scrimVisible = true
            delay(60)
            cardVisible = true
        }

        Box(modifier = Modifier.fillMaxSize()) {

            // ── Scrim ────────────────────────────────────────────────
            AnimatedVisibility(
                visible = scrimVisible,
                enter   = fadeIn(tween(250))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.50f))
                        .clickable(
                            indication          = null,
                            interactionSource   = remember { MutableInteractionSource() }
                        ) { onDismiss() }
                )
            }

            // ── Panel ────────────────────────────────────────────────
            AnimatedVisibility(
                visible  = cardVisible,
                enter    = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec  = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow)
                ) + fadeIn(tween(180)),
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(topStart = rounded_xl, topEnd = rounded_xl))
                        .background(Surface)
                        .padding(bottom = spacing_8)
                        .clickable(
                            indication        = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) { /* absorb clicks */ }
                ) {
                    // Handle
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Spacer(modifier = Modifier.height(spacing_3))
                        Box(
                            modifier = Modifier
                                .padding(top = spacing_3)
                                .width(40.dp)
                                .height(4.dp)
                                .background(OnSurface.copy(alpha = 0.12f), RoundedCornerShape(2.dp))
                        )
                    }

                    Spacer(modifier = Modifier.height(spacing_4))

                    // ── Header row ───────────────────────────────────
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = spacing_6),
                        verticalAlignment      = Alignment.CenterVertically,
                        horizontalArrangement  = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text       = "Notificaciones",
                                fontSize   = 22.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color      = OnSurface
                            )
                            if (notifications.isNotEmpty()) {
                                Text(
                                    text     = "${notifications.size} mensaje${if (notifications.size == 1) "" else "s"}",
                                    fontSize = 13.sp,
                                    color    = OnSurfaceVariant.copy(alpha = 0.7f)
                                )
                            }
                        }
                        IconButton(onClick = onDismiss) {
                            Icon(
                                imageVector        = Icons.Filled.Close,
                                contentDescription = "Cerrar",
                                tint               = OnSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(spacing_3))
                    HorizontalDivider(color = OnSurface.copy(alpha = 0.07f))

                    if (notifications.isEmpty()) {
                        // ── Empty state ──────────────────────────────
                        Column(
                            modifier              = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 48.dp),
                            horizontalAlignment   = Alignment.CenterHorizontally,
                            verticalArrangement   = Arrangement.spacedBy(spacing_3)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .background(SurfaceContainerLow, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector        = Icons.Filled.Notifications,
                                    contentDescription = null,
                                    tint               = OnSurfaceVariant.copy(alpha = 0.4f),
                                    modifier           = Modifier.size(36.dp)
                                )
                            }
                            Text(
                                text      = "Sin notificaciones",
                                fontSize  = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color     = OnSurface
                            )
                            Text(
                                text      = "Genera un menú o crea una receta\npara ver notificaciones aquí",
                                fontSize  = 13.sp,
                                color     = OnSurfaceVariant.copy(alpha = 0.7f),
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        // ── Notification list ────────────────────────
                        LazyColumn(
                            modifier            = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 420.dp)
                                .padding(horizontal = spacing_4)
                                .padding(top = spacing_3),
                            verticalArrangement = Arrangement.spacedBy(spacing_2)
                        ) {
                            items(notifications, key = { it.id }) { notif ->
                                NotificationRow(
                                    notification = notif,
                                    onDelete     = { onDelete(notif.id) }
                                )
                            }
                            item { Spacer(modifier = Modifier.height(spacing_4)) }
                        }
                    }
                }
            }
        }
    }
}

// ── Single notification row ──────────────────────────────────────────────

@Composable
private fun NotificationRow(
    notification: AppNotification,
    onDelete: () -> Unit
) {
    val (iconEmoji, accentColor) = when (notification.type) {
        "plan_generated" -> "📅" to Color(0xFF5E35B1)
        "recipe_created" -> "🍳" to Color(0xFF2E7D32)
        else             -> "🔔" to Primary
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceContainerLow)
            .padding(horizontal = spacing_4, vertical = spacing_3),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spacing_3)
    ) {
        // Icon circle
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(
                    brush  = Brush.radialGradient(listOf(accentColor.copy(alpha = 0.18f), accentColor.copy(alpha = 0.06f))),
                    shape  = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(iconEmoji, fontSize = 20.sp)
        }

        // Text content
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text       = notification.title,
                fontSize   = 14.sp,
                fontWeight = FontWeight.Bold,
                color      = OnSurface
            )
            Text(
                text     = notification.body,
                fontSize = 12.sp,
                color    = OnSurfaceVariant,
                maxLines = 2
            )
            Text(
                text     = relativeTime(notification.createdAt),
                fontSize = 11.sp,
                color    = OnSurfaceVariant.copy(alpha = 0.55f)
            )
        }

        // Delete button
        IconButton(
            onClick  = onDelete,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector        = Icons.Filled.Delete,
                contentDescription = "Eliminar",
                tint               = OnSurfaceVariant.copy(alpha = 0.5f),
                modifier           = Modifier.size(18.dp)
            )
        }
    }
}

// ── Helper: human-readable relative time ────────────────────────────────

private fun relativeTime(timestampMs: Long): String {
    val diffMs = System.currentTimeMillis() - timestampMs
    return when {
        diffMs < TimeUnit.MINUTES.toMillis(1)  -> "Ahora mismo"
        diffMs < TimeUnit.HOURS.toMillis(1)    -> "${TimeUnit.MILLISECONDS.toMinutes(diffMs)} min"
        diffMs < TimeUnit.DAYS.toMillis(1)     -> "Hace ${TimeUnit.MILLISECONDS.toHours(diffMs)} h"
        else -> SimpleDateFormat("d MMM", Locale("es")).format(Date(timestampMs))
    }
}
