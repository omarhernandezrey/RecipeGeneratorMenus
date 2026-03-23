package com.example.recipe_generator.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.recipe_generator.ui.theme.*

// ── Design System Components ────────────────────────────────────────
// Reglas:
// Sin bordes (outlines) - solo color y capas de superficie
// Jerarquia de superficies (Lowest - Low - Container - High)
// Gradientes en CTA (Primary - PrimaryContainer)
// Tipografia editorial (Plus Jakarta Sans headlines + Work Sans body)
// Bordes grandes (16dp cards, 24dp containers, 50dp pills)
// Nada de negro puro (usar OnSurface #1B1B1E)

/**
 * Boton principal con gradiente (Primary - PrimaryContainer)
 */
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val gradient = Brush.horizontalGradient(
        colors = listOf(Primary, PrimaryContainer)
    )
    Button(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        shape = RoundedCornerShape(rounded_full),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        contentPadding = PaddingValues(0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient, shape = RoundedCornerShape(rounded_full))
                .padding(horizontal = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = Color.White,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

/**
 * Card estilo editorial: fondo blanco, sin bordes, corners 24dp
 */
@Composable
fun EditorialCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(rounded_lg),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(spacing_6),
            content = content
        )
    }
}

/**
 * Chip de ingrediente con fondo SecondaryContainer
 */
@Composable
fun IngredientChip(
    text: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(rounded_full),
        color = SecondaryContainer
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = OnSecondaryContainer,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/**
 * Info chip generico (tiempo, calorias, etc.)
 * Matches Stitch: px-3 py-1 gap-1.5 text-sm
 */
@Composable
fun InfoChip(
    icon: ImageVector,
    text: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(rounded_full),
        color = SurfaceContainerLow
    ) {
        Row(
            modifier = Modifier.padding(horizontal = spacing_3, vertical = spacing_2),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing_1)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = Primary
            )
            Text(
                text = text,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = OnSurface,
                maxLines = 1
            )
        }
    }
}

/**
 * Chip de dificultad con color condicional
 */
@Composable
fun DifficultyChip(
    difficulty: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(rounded_full),
        color = if (difficulty == "Fácil") {
            SecondaryContainer.copy(alpha = 0.55f)
        } else {
            SurfaceContainerHigh
        }
    ) {
        Text(
            text = difficulty.uppercase(),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = if (difficulty == "Fácil") OnSecondaryContainer else OnSurfaceVariant,
            maxLines = 1
        )
    }
}

/**
 * TextField sin linea inferior (estilo editorial)
 */
@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = ""
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = {
            Text(
                text = placeholder,
                style = MaterialTheme.typography.bodyLarge,
                color = OnSurfaceVariant
            )
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = SurfaceContainerLowest,
            unfocusedContainerColor = SurfaceContainerLow,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = Primary
        ),
        shape = RoundedCornerShape(rounded_sm),
        textStyle = MaterialTheme.typography.bodyLarge
    )
}
