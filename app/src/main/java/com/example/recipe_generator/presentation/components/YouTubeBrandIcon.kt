package com.example.recipe_generator.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp

private val YouTubeRed = Color(0xFFFF0000)

@Composable
fun YouTubeBrandIcon(
    modifier: Modifier = Modifier,
    containerColor: Color = YouTubeRed
) {
    Box(
        modifier = modifier
            .aspectRatio(1.6f)
            .background(
                color = containerColor,
                shape = RoundedCornerShape(7.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize(0.55f)) {
            drawCircle(
                color = Color.White.copy(alpha = 0.08f),
                radius = size.minDimension * 0.42f,
                center = Offset(size.width * 0.52f, size.height * 0.5f)
            )
            val triangle = Path().apply {
                moveTo(size.width * 0.26f, size.height * 0.16f)
                lineTo(size.width * 0.26f, size.height * 0.84f)
                lineTo(size.width * 0.86f, size.height * 0.5f)
                close()
            }
            drawPath(path = triangle, color = Color.White)
        }
    }
}
