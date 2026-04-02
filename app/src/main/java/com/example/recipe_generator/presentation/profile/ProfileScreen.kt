package com.example.recipe_generator.presentation.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.recipe_generator.R
import com.example.recipe_generator.presentation.theme.OnSurface
import com.example.recipe_generator.presentation.theme.OnSurfaceVariant
import com.example.recipe_generator.presentation.theme.Primary
import com.example.recipe_generator.presentation.theme.PrimaryContainer
import com.example.recipe_generator.presentation.theme.Secondary
import com.example.recipe_generator.presentation.theme.SecondaryContainer
import com.example.recipe_generator.presentation.theme.Surface
import com.example.recipe_generator.presentation.theme.SurfaceContainerLow
import com.example.recipe_generator.presentation.theme.SurfaceContainerLowest
import com.example.recipe_generator.presentation.theme.rounded_full
import com.example.recipe_generator.presentation.theme.rounded_lg
import com.example.recipe_generator.presentation.theme.rounded_md
import com.example.recipe_generator.presentation.theme.spacing_10
import com.example.recipe_generator.presentation.theme.spacing_12
import com.example.recipe_generator.presentation.theme.spacing_2
import com.example.recipe_generator.presentation.theme.spacing_3
import com.example.recipe_generator.presentation.theme.spacing_4
import com.example.recipe_generator.presentation.theme.spacing_6
import com.example.recipe_generator.presentation.theme.spacing_8

/**
 * ProfileScreen — Pantalla de Perfil del usuario.
 *
 * Usa Image() con drawable local (painterResource) — equivalente Compose de ImageView.
 * Cubre LF7: Image (equivalente de ImageView).
 * Cubre LF3: Column, Row, Box (layouts Compose).
 *
 * Capa: Presentation
 */
@Composable
fun ProfileScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Surface)
            .verticalScroll(rememberScrollState())
            .padding(spacing_6),
        verticalArrangement = Arrangement.spacedBy(spacing_6)
    ) {
        Spacer(modifier = Modifier.height(spacing_4))

        // Sección de foto de perfil + nombre (LF7: Image con drawable local)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(rounded_lg),
            colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(spacing_8),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(spacing_4)
            ) {
                // LF7: Image() — equivalente Compose de ImageView
                // Usa painterResource con drawable local (sin Coil, sin URLs externas)
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(PrimaryContainer)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_profile),
                        contentDescription = "Foto de perfil",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                Text(
                    text = "Omar Hernández Rey",
                    style = MaterialTheme.typography.headlineMedium,
                    color = OnSurface,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Desarrollador Android",
                    style = MaterialTheme.typography.bodyLarge,
                    color = OnSurfaceVariant
                )

                // Chip de nivel
                Box(
                    modifier = Modifier
                        .background(
                            SecondaryContainer,
                            shape = RoundedCornerShape(rounded_full)
                        )
                        .padding(horizontal = spacing_6, vertical = spacing_2)
                ) {
                    Text(
                        text = "⭐ Nivel Experto",
                        style = MaterialTheme.typography.labelLarge,
                        color = OnSurface,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Sección de información personal
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(rounded_lg),
            colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier.padding(spacing_6),
                verticalArrangement = Arrangement.spacedBy(spacing_4)
            ) {
                Text(
                    text = "INFORMACIÓN PERSONAL",
                    style = MaterialTheme.typography.labelSmall,
                    color = Primary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                )

                ProfileInfoRow(
                    icon = Icons.Outlined.Person,
                    label = "Nombre",
                    value = "Omar Hernández Rey"
                )
                ProfileInfoRow(
                    icon = Icons.Outlined.School,
                    label = "Institución",
                    value = "Politécnico Grancolombiano"
                )
                ProfileInfoRow(
                    icon = Icons.Outlined.Email,
                    label = "Grupo",
                    value = "B03 — Herramientas Móvil I"
                )
                ProfileInfoRow(
                    icon = Icons.Outlined.Star,
                    label = "Recetas favoritas",
                    value = "21 recetas en el menú"
                )
            }
        }

        // Estadísticas
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing_4)
        ) {
            StatCard(
                modifier = Modifier.weight(1f),
                value = "21",
                label = "Recetas"
            )
            StatCard(
                modifier = Modifier.weight(1f),
                value = "7",
                label = "Días"
            )
            StatCard(
                modifier = Modifier.weight(1f),
                value = "3",
                label = "Comidas/día"
            )
        }

        Spacer(modifier = Modifier.height(spacing_12))
    }
}

@Composable
private fun ProfileInfoRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = spacing_3),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spacing_4)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Secondary,
            modifier = Modifier.size(20.dp)
        )
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = OnSurfaceVariant,
                fontSize = 11.sp
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = OnSurface,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    value: String,
    label: String
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(rounded_md),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = spacing_6, horizontal = spacing_4),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(spacing_2)
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineLarge,
                color = Primary,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = OnSurfaceVariant,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
