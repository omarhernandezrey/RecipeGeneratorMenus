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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
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
import com.example.recipe_generator.presentation.theme.Primary
import com.example.recipe_generator.presentation.theme.SecondaryContainer
import com.example.recipe_generator.presentation.theme.SurfaceContainerHighest
import com.example.recipe_generator.presentation.theme.SurfaceContainerLow
import com.example.recipe_generator.presentation.theme.rounded_full
import com.example.recipe_generator.presentation.theme.rounded_lg
import com.example.recipe_generator.presentation.theme.rounded_md
import com.example.recipe_generator.presentation.theme.spacing_1
import com.example.recipe_generator.presentation.theme.spacing_10
import com.example.recipe_generator.presentation.theme.spacing_2
import com.example.recipe_generator.presentation.theme.spacing_3
import com.example.recipe_generator.presentation.theme.spacing_4
import com.example.recipe_generator.presentation.theme.spacing_6

private val dietLabels = listOf("Vegetariano", "Vegano", "Sin Gluten", "Keto", "Paleo")
private val languageOptions = listOf("Español", "Inglés", "Portugués")
private val portionOptions = (1..10).map { it.toString() }

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
    onNavItemSelected: (Int) -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val surfaceBg = com.example.recipe_generator.presentation.theme.Surface
    var showLogoutDialog by remember { mutableStateOf(false) }

    // AlertDialog de confirmación para cerrar sesión
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Cerrar sesión", fontWeight = FontWeight.Bold) },
            text = { Text("¿Estás seguro de que deseas cerrar sesión?") },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                    onLogout()
                }) {
                    Text("Cerrar sesión", color = Error, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

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
            onLanguageSelect = onLanguageSelect,
            onLogoutClick = { showLogoutDialog = true }
        )

        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
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
            HomeEditorialTopAppBar(title = "Ajustes")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsContent(
    selectedDiets: Set<String>,
    onToggleDiet: (String) -> Unit,
    defaultPortions: Int,
    onPortionsChange: (Int) -> Unit,
    selectedTheme: String,
    onThemeSelect: (String) -> Unit,
    selectedLanguage: String,
    onLanguageSelect: (String) -> Unit,
    onLogoutClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = editorialTopBarContentPadding(), bottom = editorialBottomBarContentPadding())
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(spacing_10)
    ) {
        // ── Sección 1: Preferencias de Cocina ─────────────────────────
        Column(
            modifier = Modifier.padding(horizontal = spacing_6),
            verticalArrangement = Arrangement.spacedBy(spacing_6)
        ) {
            SectionHeader(icon = Icons.Outlined.Tune, title = "Preferencias de Cocina")

            // Dietas: chips multi-selección
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
                    DietChips(selected = selectedDiets, onToggle = onToggleDiet)
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(rounded_lg))
                    .background(SurfaceContainerLow)
                    .padding(spacing_6)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(spacing_4)) {
                    Text(
                        text = "Porciones por defecto",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = OnSurfaceVariant,
                        letterSpacing = 1.2.sp
                    )
                    Text(
                        text = "Ajuste automático para cada receta",
                        style = MaterialTheme.typography.bodySmall,
                        color = OnSurfaceVariant,
                        fontSize = 12.sp
                    )
                    // ExposedDropdownMenuBox — equivalente M3 de Spinner (LF8 — F3-20)
                    PortionsDropdown(
                        selected = defaultPortions,
                        onChange = onPortionsChange
                    )
                }
            }
        }

        // ── Sección 2: Aplicación ──────────────────────────────────────
        Column(
            modifier = Modifier.padding(horizontal = spacing_6),
            verticalArrangement = Arrangement.spacedBy(spacing_6)
        ) {
            SectionHeader(icon = Icons.Outlined.Settings, title = "Aplicación")

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(spacing_6)
            ) {
                // F3-18: Switch M3 para modo oscuro (LF8: Switch/ToggleButton)
                DarkModeSwitch(
                    isDarkMode = selectedTheme == "Oscuro",
                    onToggle = { isDark ->
                        onThemeSelect(if (isDark) "Oscuro" else "Claro")
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                // F3-19: RadioButton group para idioma (LF8: RadioGroup equiv Compose)
                LanguageRadioGroup(
                    selected = selectedLanguage,
                    onSelect = onLanguageSelect,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // ── Sección 3: Cuenta ─────────────────────────────────────────
        Column(
            modifier = Modifier
                .padding(horizontal = spacing_6)
                .padding(top = spacing_4),
            verticalArrangement = Arrangement.spacedBy(spacing_4)
        ) {
            SectionHeader(icon = Icons.AutoMirrored.Outlined.HelpOutline, title = "Cuenta")

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(rounded_md))
                    .background(ErrorContainer.copy(alpha = 0.15f))
                    .border(1.dp, Error.copy(alpha = 0.15f), RoundedCornerShape(rounded_md))
                    .clickable { onLogoutClick() }
                    .padding(horizontal = spacing_6, vertical = spacing_4 + spacing_2)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Logout,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = Error
                    )
                    Spacer(modifier = Modifier.width(spacing_3))
                    Text(
                        text = "Cerrar sesión",
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

// ── F3-18: Switch M3 para modo oscuro — LF8: Switch/ToggleButton ─────

@Composable
private fun DarkModeSwitch(
    isDarkMode: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
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
                    text = "Modo Oscuro",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = OnSurface
                )
                Spacer(modifier = Modifier.height(spacing_1))
                Text(
                    text = if (isDarkMode) "Tema oscuro activo" else "Tema claro activo",
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurfaceVariant,
                    fontSize = 12.sp
                )
            }
            // Switch M3 — LF8: Switch/ToggleButton (Compose equiv de Switch XML)
            Switch(
                checked = isDarkMode,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = com.example.recipe_generator.presentation.theme.Surface,
                    checkedTrackColor = Primary,
                    uncheckedThumbColor = OnSurfaceVariant,
                    uncheckedTrackColor = SurfaceContainerHighest
                )
            )
        }
    }
}

// ── F3-19: RadioButton group para idioma — LF8: RadioGroup (Compose) ─

@Composable
private fun LanguageRadioGroup(
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
        Column(verticalArrangement = Arrangement.spacedBy(spacing_2)) {
            Text(
                text = "Idioma",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = OnSurfaceVariant,
                letterSpacing = 1.2.sp
            )
            // RadioButton group — LF8: RadioGroup equiv Compose (F3-19)
            languageOptions.forEach { option ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(rounded_md))
                        .clickable { onSelect(option) }
                        .padding(vertical = spacing_2),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = option == selected,
                        onClick = { onSelect(option) },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = Primary,
                            unselectedColor = OnSurfaceVariant
                        )
                    )
                    Spacer(modifier = Modifier.width(spacing_3))
                    Text(
                        text = option,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = OnSurface
                    )
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

// ── F3-20: ExposedDropdownMenuBox para porciones — LF8: Spinner M3 ───

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PortionsDropdown(
    selected: Int,
    onChange: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = "$selected ${if (selected == 1) "porción" else "porciones"}",
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            label = { Text("Porciones por receta") },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            portionOptions.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = "$option ${if (option == "1") "porción" else "porciones"}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (option.toInt() == selected) Primary else OnSurface,
                            fontWeight = if (option.toInt() == selected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    onClick = {
                        onChange(option.toInt())
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    trailingIcon = if (option.toInt() == selected) {
                        { Icon(Icons.Filled.Check, contentDescription = null, tint = Primary, modifier = Modifier.size(18.dp)) }
                    } else null
                )
            }
        }
    }
}

// ── Controles reutilizables ────────────────────────────────────────────

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
