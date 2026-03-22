package com.example.recipe_generator.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.recipe_generator.ui.theme.OnSurfaceVariant

private const val PROFILE_IMAGE_URL = "https://lh3.googleusercontent.com/aida-public/AB6AXuD8lJ-Oz3xs3SZdgWSLkYOiPc1x33ZYaVWB2xzR9rGZPUElRBv1ihN67N__6CLEMKgl6up8-ZdxruNxcvwWMVWoP1NbJf_p11iXX3KpgTNHDwOTRABvIGMsS8NEBw-KuecXKPidoqVJbddiN7PZRzqTok0gbnt_82oK4MSy0doQuCQuwk8V13_OF0Q3CMYxeUee3oAdWW6nPR930AkMI5NG8JJf0kuB7HnEcYz0A6KlrCve2n2oPudvLBkA1FaXrs2VjKqjERixFuk"

@Composable
fun EditorialTopAppBar() {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Profile Image
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(PROFILE_IMAGE_URL)
                .crossfade(true)
                .build(),
            contentDescription = "Profile",
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        // Title
        Text(
            text = "Gastronomía Editorial",
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary,
            letterSpacing = (-0.5).sp
        )

        // Notifications
        IconButton(onClick = {}) {
            Icon(
                imageVector = Icons.Outlined.Notifications,
                contentDescription = "Notifications",
                tint = OnSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
