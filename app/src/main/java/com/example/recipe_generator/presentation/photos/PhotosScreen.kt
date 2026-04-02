package com.example.recipe_generator.presentation.photos

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.recipe_generator.R
import com.example.recipe_generator.presentation.theme.OnSurface
import com.example.recipe_generator.presentation.theme.OnSurfaceVariant
import com.example.recipe_generator.presentation.theme.Primary
import com.example.recipe_generator.presentation.theme.PrimaryContainer
import com.example.recipe_generator.presentation.theme.Secondary
import com.example.recipe_generator.presentation.theme.SecondaryContainer
import com.example.recipe_generator.presentation.theme.Surface
import com.example.recipe_generator.presentation.theme.SurfaceContainerLowest
import com.example.recipe_generator.presentation.theme.rounded_lg
import com.example.recipe_generator.presentation.theme.rounded_md
import com.example.recipe_generator.presentation.theme.spacing_2
import com.example.recipe_generator.presentation.theme.spacing_4
import com.example.recipe_generator.presentation.theme.spacing_6
import com.example.recipe_generator.presentation.theme.spacing_8

/**
 * PhotosScreen — Galería de fotos de recetas.
 *
 * Usa LazyColumn con imágenes locales (painterResource) — LF8: LazyColumn (equiv. ListView).
 * Click en imagen muestra descripción — LF4: estado local con remember.
 * Cubre LF7: Image() con drawable local + LF8: LazyColumn.
 *
 * Capa: Presentation
 */

private data class PhotoItem(
    val drawableRes: Int,
    val title: String,
    val description: String
)

private val photoGallery = listOf(
    PhotoItem(
        R.drawable.img_food_1,
        "Ensalada de Quinoa",
        "Una ensalada nutritiva de quinoa con vegetales frescos rostizados. Rica en proteínas y perfecta para el almuerzo."
    ),
    PhotoItem(
        R.drawable.img_food_2,
        "Sopa de Verduras",
        "Reconfortante sopa de verduras de temporada con hierbas aromáticas. Ideal para los días fríos."
    ),
    PhotoItem(
        R.drawable.img_food_3,
        "Postre Saludable",
        "Delicioso postre elaborado con ingredientes naturales. Bajo en azúcar y alto en nutrientes."
    ),
    PhotoItem(
        R.drawable.img_food_4,
        "Plato Principal",
        "Plato equilibrado con proteínas, carbohidratos complejos y vegetales de temporada."
    ),
    PhotoItem(
        R.drawable.img_food_5,
        "Desayuno Energético",
        "Desayuno completo y energético para comenzar el día con vitalidad. Rico en fibra y antioxidantes."
    ),
    PhotoItem(
        R.drawable.img_placeholder,
        "Menú del Chef",
        "Receta especial de la semana creada por nuestro chef. Fusión de sabores mediterráneos y latinoamericanos."
    )
)

@Composable
fun PhotosScreen(modifier: Modifier = Modifier) {
    // LF4: estado local con remember { mutableStateOf() }
    var selectedPhotoTitle by remember { mutableStateOf<String?>(null) }
    var selectedDescription by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Surface)
    ) {
        // Descripción seleccionada (LF4: estado reactivo)
        val descBg by animateColorAsState(
            if (selectedPhotoTitle != null) SecondaryContainer else Surface,
            animationSpec = tween(300),
            label = "descBg"
        )

        if (selectedPhotoTitle != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(descBg)
                    .padding(spacing_6)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(spacing_2)) {
                    Text(
                        text = selectedPhotoTitle!!,
                        style = MaterialTheme.typography.titleMedium,
                        color = Primary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = selectedDescription,
                        style = MaterialTheme.typography.bodyMedium,
                        color = OnSurface
                    )
                }
            }
        }

        // LF8: LazyColumn (equivalente Compose de ListView)
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = spacing_6),
            verticalArrangement = Arrangement.spacedBy(spacing_6)
        ) {
            item { Spacer(modifier = Modifier.height(spacing_4)) }

            items(photoGallery) { photo ->
                PhotoCard(
                    photo = photo,
                    isSelected = photo.title == selectedPhotoTitle,
                    onClick = {
                        if (selectedPhotoTitle == photo.title) {
                            // Deseleccionar si ya estaba seleccionada
                            selectedPhotoTitle = null
                            selectedDescription = ""
                        } else {
                            // Seleccionar y mostrar descripción
                            selectedPhotoTitle = photo.title
                            selectedDescription = photo.description
                        }
                    }
                )
            }

            item { Spacer(modifier = Modifier.height(spacing_8)) }
        }
    }
}

@Composable
private fun PhotoCard(
    photo: PhotoItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor by animateColorAsState(
        if (isSelected) Primary else SurfaceContainerLowest,
        animationSpec = tween(200),
        label = "borderAnim"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(rounded_lg),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) PrimaryContainer.copy(alpha = 0.12f)
            else SurfaceContainerLowest
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 6.dp else 2.dp
        )
    ) {
        Column {
            // LF7: Image() — equivalente Compose de ImageView con drawable local
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .clip(RoundedCornerShape(topStart = rounded_md, topEnd = rounded_md))
            ) {
                Image(
                    painter = painterResource(id = photo.drawableRes),
                    contentDescription = photo.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(borderColor.copy(alpha = 0.20f))
                    )
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(spacing_4)
                            .background(
                                Primary,
                                shape = RoundedCornerShape(rounded_md)
                            )
                            .padding(horizontal = spacing_4, vertical = spacing_2)
                    ) {
                        Text(
                            text = "✓ Seleccionada",
                            style = MaterialTheme.typography.labelSmall,
                            color = androidx.compose.ui.graphics.Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.padding(spacing_6),
                verticalArrangement = Arrangement.spacedBy(spacing_2)
            ) {
                Text(
                    text = photo.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isSelected) Primary else OnSurface,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = if (isSelected) "Toca para deseleccionar" else "Toca para ver descripción",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isSelected) Secondary else OnSurfaceVariant
                )
            }
        }
    }
}
