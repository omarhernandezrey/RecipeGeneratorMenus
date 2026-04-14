package com.example.recipe_generator.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.recipe_generator.R

@Composable
fun YouTubeBrandIcon(
    modifier: Modifier = Modifier
) {
    Image(
        painter = painterResource(id = R.drawable.ic_youtube_play),
        contentDescription = null,
        modifier = modifier,
        contentScale = ContentScale.Fit
    )
}
