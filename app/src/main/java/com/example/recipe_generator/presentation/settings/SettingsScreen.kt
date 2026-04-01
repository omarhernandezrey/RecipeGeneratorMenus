package com.example.recipe_generator.presentation.settings

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.recipe_generator.presentation.components.EditorialBottomNavBar
import com.example.recipe_generator.presentation.components.HomeEditorialTopAppBar
import com.example.recipe_generator.presentation.components.editorialBottomBarContentPadding
import com.example.recipe_generator.presentation.components.editorialTopBarContentPadding
import com.example.recipe_generator.presentation.theme.Error
import com.example.recipe_generator.presentation.theme.ErrorContainer
import com.example.recipe_generator.presentation.theme.OnSecondaryContainer
import com.example.recipe_generator.presentation.theme.OnSurface
import com.example.recipe_generator.presentation.theme.OnSurfaceVariant
import com.example.recipe_generator.presentation.theme.Outline
import com.example.recipe_generator.presentation.theme.OutlineVariant
import com.example.recipe_generator.presentation.theme.Primary
import com.example.recipe_generator.presentation.theme.SecondaryContainer
import com.example.recipe_generator.presentation.theme.SurfaceContainerHighest
import com.example.recipe_generator.presentation.theme.SurfaceContainerLow
import com.example.recipe_generator.presentation.theme.SurfaceContainerLowest
import com.example.recipe_generator.presentation.theme.rounded_full
import com.example.recipe_generator.presentation.theme.rounded_lg
import com.example.recipe_generator.presentation.theme.rounded_md
import com.example.recipe_generator.presentation.theme.rounded_sm
import com.example.recipe_generator.presentation.theme.spacing_1
import com.example.recipe_generator.presentation.theme.spacing_10
import com.example.recipe_generator.presentation.theme.spacing_2
import com.example.recipe_generator.presentation.theme.spacing_3
import com.example.recipe_generator.presentation.theme.spacing_4
import com.example.recipe_generator.presentation.theme.spacing_6

private val dietLabels = listOf("Vegetariano", "Vegano", "Sin Gluten", "Keto", "Paleo")
private val themeOptions = listOf("Claro", "Oscuro", "Sistema")
private val languageOptions = listOf("Español", "Inglés", "Portugués")

@Composable
fun SettingsScreen(
    selectedDiets: Set<String> = emptySet(),
    onToggleDiet: (String) -> Unit = {},
    defaultPortions: Int = 2,
    onPortionsChange: (Int) -> Unit = {},
    selectedTheme: String = "Claro",
    onThemeSelect: (String) -> Unit = {},
    selectedLanguage: String = "Español",
    onLanguageSelect: (String) -> Unit = {},
    selectedNavItem: Int = 3,
    onNavItemSelected: (Int) -> Unit = {}
) {
    val surfaceBg = com.example.recipe_generator.presentation.theme.Surface

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(surfaceBg)
    ) {
        SettingsContent(
            selectedDiets = selectedDiets,
            onToggleDiet = onToggleDiet,
            defaultPortions = defaultPortions,
            onPortionsChange = onPortionsChange,
            selectedTheme = selectedTheme,
            onThemeSelect = onThemeSelect,
            selectedLanguage = selectedLanguage,
            onLanguageSelect = onLanguageSelect
        )

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
        ) {
            EditorialBottomNavBar(
                selectedItem = selectedNavItem,
                onItemSelected = onNavItemSelected
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .background(surfaceBg)
        ) {
            HomeEditorialTopAppBar(title = "Menú Semanal")
        }
    }
}

@Composable
private fun SettingsContent(
    selectedDiets: Set<String>,
    onToggleDiet: (String) -> Unit,
    defaultPortions: Int,
    onPortionsChange: (Int) -> Unit,
    selectedTheme: String,
    onThemeSelect: (String) -> Unit,
    selectedLanguage: String,
    onLanguageSelect: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = editorialTopBarContentPadding(), bottom = editorialBottomBarContentPadding())
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(spacing_10)
    ) {
        // Section 1: Preferencias de Cocina
        Column(
            modifier = Modifier.padding(horizontal = spacing_6),
            verticalArrangement = Arrangement.spacedBy(spacing_6)
        ) {
            SectionHeader(icon = Icons.Outlined.Tune, title = "Preferencias de Cocina")

            // Diet MultiSelect card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(rounded_lg))
                    .background(SurfaceContainerLow)
                    .padding(spacing_6)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(spacing_4)) {
                    Text(
                        text = "Preferencias dietéticas",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = OnSurfaceVariant,
                        letterSpacing = 1.2.sp
                    )
                    DietChips(
                        selected = selectedDiets,
                        onToggle = onToggleDiet
                    )
                }
            }

            // Default Portions card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(rounded_lg))
                    .background(SurfaceContainerLow)
                    .padding(spacing_6)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Porciones por defecto",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = OnSurface
                        )
                        Spacer(modifier = Modifier.height(spacing_1))
                        Text(
                            text = "Ajuste automático para cada receta",
                            style = MaterialTheme.typography.bodySmall,
                            color = OnSurfaceVariant,
                            fontSize = 12.sp
                        )
                    }
                    PortionsStepper(
                        count = defaultPortions,
                        onChange = onPortionsChange
                    )
                }
            }
        }

        // Section 2: Aplicación
        Column(
            modifier = Modifier.padding(horizontal = spacing_6),
            verticalArrangement = Arrangement.spacedBy(spacing_6)
        ) {
            SectionHeader(icon = Icons.Outlined.Settings, title = "Aplicación")

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(spacing_6)
            ) {
                ThemeSelector(
                    selected = selectedTheme,
                    onSelect = onThemeSelect,
                    modifier = Modifier.fillMaxWidth()
                )
                LanguageSelector(
                    selected = selectedLanguage,
                    onSelect = onLanguageSelect,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Section 3: Danger & Support
        Column(
            modifier = Modifier
                .padding(horizontal = spacing_6)
                .padding(top = spacing_4),
            verticalArrangement = Arrangement.spacedBy(spacing_4)
        ) {
            // Support button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(rounded_md))
                    .background(SurfaceContainerHighest.copy(alpha = 0.50f))
                    .clickable { }
                    .padding(horizontal = spacing_6, vertical = spacing_4)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(spacing_3)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.HelpOutline,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = OnSurfaceVariant
                        )
                        Text(
                            text = "Soporte y Comentarios",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = OnSurface
                        )
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = OnSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Delete button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(rounded_md))
                    .background(ErrorContainer.copy(alpha = 0.20f))
                    .border(
                        width = 1.dp,
                        color = Error.copy(alpha = 0.10f),
                        shape = RoundedCornerShape(rounded_md)
                    )
                    .clickable { }
                    .padding(horizontal = spacing_6, vertical = spacing_4)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = Error
                    )
                    Spacer(modifier = Modifier.width(spacing_2))
                    Text(
                        text = "Limpiar Base de Datos",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = Error
                    )
                }
            }
        }

        // Footer
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = spacing_10),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(spacing_2)
        ) {
            Text(
                text = "Versión 1.0.0",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = OnSurfaceVariant
            )
            Text(
                text = "Editorial Gastronomy © 2024",
                fontSize = 11.sp,
                color = Outline.copy(alpha = 0.60f)
            )
        }
    }
}

@Composable
private fun SectionHeader(icon: ImageVector, title: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spacing_2),
        modifier = Modifier.padding(horizontal = spacing_2)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = Primary
        )
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = OnSurface,
            letterSpacing = (-0.3).sp
        )
    }
}

@Composable
private fun DietChips(
    selected: Set<String>,
    onToggle: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(spacing_4)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing_4)
        ) {
            repeat(2) { index ->
                if (index < dietLabels.size) {
                    val label = dietLabels[index]
                    DietChipItem(
                        label = label,
                        isSelected = label in selected,
                        onToggle = { onToggle(label) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        if (dietLabels.size > 2) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing_4)
            ) {
                repeat(2) { index ->
                    val actualIndex = 2 + index
                    if (actualIndex < dietLabels.size) {
                        val label = dietLabels[actualIndex]
                        DietChipItem(
                            label = label,
                            isSelected = label in selected,
                            onToggle = { onToggle(label) },
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        if (dietLabels.size > 4) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing_4)
            ) {
                val label = dietLabels[4]
                DietChipItem(
                    label = label,
                    isSelected = label in selected,
                    onToggle = { onToggle(label) },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun DietChipItem(
    label: String,
    isSelected: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bg by animateColorAsState(
        if (isSelected) SecondaryContainer else SurfaceContainerHighest,
        tween(250), label = "dietBg_$label"
    )
    val fg by animateColorAsState(
        if (isSelected) OnSecondaryContainer else OnSurfaceVariant,
        tween(250), label = "dietFg_$label"
    )

    Box(
        modifier = modifier
            .height(44.dp)
            .clip(RoundedCornerShape(rounded_full))
            .background(bg)
            .clickable { onToggle() }
            .padding(horizontal = spacing_3, vertical = spacing_1)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = fg
                )
                Spacer(modifier = Modifier.width(spacing_1))
            }
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
                color = fg,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun PortionsStepper(
    count: Int,
    onChange: (Int) -> Unit
) {
    Box(
        modifier = Modifier
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(rounded_full)
            )
            .clip(RoundedCornerShape(rounded_full))
            .background(SurfaceContainerLowest)
            .padding(4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .clickable { if (count > 1) onChange(count - 1) },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Remove,
                    contentDescription = "Disminuir",
                    tint = Primary,
                    modifier = Modifier.size(24.dp)
                )
            }
            Text(
                text = "$count",
                modifier = Modifier.width(48.dp),
                textAlign = TextAlign.Center,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = OnSurface
            )
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .clickable { onChange(count + 1) },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Aumentar",
                    tint = Primary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
private fun ThemeSelector(
    selected: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(rounded_lg))
            .background(SurfaceContainerLow)
            .padding(spacing_6)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(spacing_4)) {
            Text(
                text = "Tema",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = OnSurfaceVariant
            )
            Column(verticalArrangement = Arrangement.spacedBy(spacing_2)) {
                themeOptions.forEach { option ->
                    val isSelected = option == selected
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(rounded_md))
                            .then(
                                if (isSelected) Modifier.background(SurfaceContainerLowest)
                                else Modifier
                            )
                            .clickable { onSelect(option) }
                            .padding(spacing_3)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = option,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = OnSurface
                            )
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .border(
                                        width = 2.dp,
                                        color = if (isSelected) Primary else OutlineVariant,
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .clip(CircleShape)
                                            .background(Primary)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LanguageSelector(
    selected: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(rounded_lg))
            .background(SurfaceContainerLow)
            .padding(spacing_6)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(spacing_4)) {
            Text(
                text = "Idioma",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = OnSurfaceVariant
            )
            Column(verticalArrangement = Arrangement.spacedBy(spacing_2)) {
                languageOptions.forEach { option ->
                    val isSelected = option == selected
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(rounded_sm))
                            .background(
                                if (isSelected) SurfaceContainerLowest else Color.Transparent
                            )
                            .clickable { onSelect(option) }
                            .padding(horizontal = spacing_4, vertical = spacing_3)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = option,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = OnSurface
                            )
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                    tint = Primary
                                )
                            }
                        }
                    }
                }
            }
            Text(
                text = "Requiere reinicio para aplicar cambios globales.",
                fontSize = 10.sp,
                fontStyle = FontStyle.Italic,
                color = OnSurfaceVariant,
                modifier = Modifier.padding(start = spacing_1)
            )
        }
    }
}
