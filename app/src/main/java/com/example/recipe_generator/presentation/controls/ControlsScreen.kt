package com.example.recipe_generator.presentation.controls

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.CheckBox
import androidx.compose.material.icons.outlined.RadioButtonChecked
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.ToggleOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.recipe_generator.presentation.theme.OnSurface
import com.example.recipe_generator.presentation.theme.OnSurfaceVariant
import com.example.recipe_generator.presentation.theme.Primary
import com.example.recipe_generator.presentation.theme.PrimaryContainer
import com.example.recipe_generator.presentation.theme.Secondary
import com.example.recipe_generator.presentation.theme.SecondaryContainer
import com.example.recipe_generator.presentation.theme.Surface
import com.example.recipe_generator.presentation.theme.SurfaceContainerLow
import com.example.recipe_generator.presentation.theme.SurfaceContainerLowest
import com.example.recipe_generator.presentation.theme.Tertiary
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
 * ControlsScreen — Pantalla de demostración de controles LF8.
 *
 * Muestra todos los controles requeridos por LF8:
 *  - Button / ElevatedButton (equivalente Button XML)
 *  - IconButton con Icon (equivalente ImageButton XML)
 *  - Checkbox (equivalente CheckBox XML)
 *  - RadioButton group (equivalente RadioGroup XML)
 *  - Switch (equivalente ToggleButton/Switch XML)
 *  - ExposedDropdownMenuBox (equivalente Spinner XML)
 *  - Slider (equivalente SeekBar XML — también LF7)
 *  - Text resultado visible al confirmar
 *
 * Cubre LF8 completo. Estado con remember { mutableStateOf() } (LF4).
 *
 * Capa: Presentation
 */

private val dificultadOptions = listOf("Fácil", "Medio", "Difícil")
private val categoriaOptions = listOf("Desayuno", "Almuerzo", "Cena", "Snack", "Postre")
private val radioOpciones = listOf("Vegetariano", "Omnívoro", "Vegano")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ControlsScreen(modifier: Modifier = Modifier) {
    // LF4: estado local con remember { mutableStateOf() }
    var isSwitchOn by remember { mutableStateOf(false) }
    var isCheckbox1 by remember { mutableStateOf(false) }
    var isCheckbox2 by remember { mutableStateOf(true) }
    var isCheckbox3 by remember { mutableStateOf(false) }
    var selectedRadio by remember { mutableStateOf("Omnívoro") }
    var sliderValue by remember { mutableFloatStateOf(2f) }
    var spinnerExpanded by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf("Almuerzo") }
    var resultText by remember { mutableStateOf("") }
    var iconToggle by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Surface)
            .verticalScroll(rememberScrollState())
            .padding(spacing_6),
        verticalArrangement = Arrangement.spacedBy(spacing_6)
    ) {
        Spacer(modifier = Modifier.height(spacing_2))

        Text(
            text = "Panel de Controles",
            style = MaterialTheme.typography.headlineMedium,
            color = OnSurface,
            fontWeight = FontWeight.Bold
        )

        // Resultado visible (LF4: estado reactivo)
        if (resultText.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        SecondaryContainer,
                        shape = RoundedCornerShape(rounded_md)
                    )
                    .padding(spacing_6)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(spacing_2)) {
                    Text(
                        text = "RESULTADO",
                        style = MaterialTheme.typography.labelSmall,
                        color = Secondary,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    )
                    Text(
                        text = resultText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = OnSurface
                    )
                }
            }
        }

        // ── 1. Button / ElevatedButton ────────────────────────────────
        ControlSection(
            icon = Icons.Outlined.Star,
            title = "Botones",
            subtitle = "Botón estándar y botón elevado"
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing_4)
            ) {
                Button(
                    onClick = { resultText = "Button presionado" },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary)
                ) {
                    Text("Button", color = Color.White, fontWeight = FontWeight.Bold)
                }
                ElevatedButton(
                    onClick = { resultText = "ElevatedButton presionado" },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Elevated", fontWeight = FontWeight.Bold)
                }
            }
        }

        // ── 2. IconButton (ImageButton) ──────────────────────────────
        ControlSection(
            icon = Icons.Outlined.Star,
            title = "Botón de ícono",
            subtitle = "Toca el ícono para activar o desactivar"
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing_6),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        iconToggle = !iconToggle
                        resultText = if (iconToggle) "Favorito activado ❤" else "Favorito desactivado"
                    },
                    modifier = Modifier
                        .size(52.dp)
                        .background(
                            if (iconToggle) PrimaryContainer else SurfaceContainerLow,
                            shape = RoundedCornerShape(12.dp)
                        )
                ) {
                    Icon(
                        imageVector = if (iconToggle) Icons.Filled.Favorite else Icons.Outlined.Star,
                        contentDescription = "Favorito",
                        tint = if (iconToggle) Primary else OnSurfaceVariant
                    )
                }
                Text(
                    text = if (iconToggle) "Favorito activo" else "Toca el ícono",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (iconToggle) Primary else OnSurfaceVariant
                )
            }
        }

        // ── 3. Checkbox ──────────────────────────────────────────────
        ControlSection(
            icon = Icons.Outlined.CheckBox,
            title = "Casillas de verificación",
            subtitle = "Selecciona una o más opciones"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(spacing_2)) {
                CheckboxRow(
                    label = "Sin Gluten",
                    checked = isCheckbox1,
                    onCheckedChange = {
                        isCheckbox1 = it
                        resultText = buildCheckboxResult(isCheckbox1, isCheckbox2, isCheckbox3)
                    }
                )
                CheckboxRow(
                    label = "Vegetariano",
                    checked = isCheckbox2,
                    onCheckedChange = {
                        isCheckbox2 = it
                        resultText = buildCheckboxResult(isCheckbox1, isCheckbox2, isCheckbox3)
                    }
                )
                CheckboxRow(
                    label = "Bajo en calorías",
                    checked = isCheckbox3,
                    onCheckedChange = {
                        isCheckbox3 = it
                        resultText = buildCheckboxResult(isCheckbox1, isCheckbox2, isCheckbox3)
                    }
                )
            }
        }

        // ── 4. RadioButton Group ─────────────────────────────────────
        ControlSection(
            icon = Icons.Outlined.RadioButtonChecked,
            title = "Selección única",
            subtitle = "Elige solo una opción del grupo"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(spacing_2)) {
                radioOpciones.forEach { opcion ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(spacing_3)
                    ) {
                        RadioButton(
                            selected = selectedRadio == opcion,
                            onClick = {
                                selectedRadio = opcion
                                resultText = "Dieta seleccionada: $opcion"
                            },
                            colors = RadioButtonDefaults.colors(selectedColor = Primary)
                        )
                        Text(
                            text = opcion,
                            style = MaterialTheme.typography.bodyLarge,
                            color = if (selectedRadio == opcion) Primary else OnSurface,
                            fontWeight = if (selectedRadio == opcion) FontWeight.SemiBold else FontWeight.Normal
                        )
                    }
                }
            }
        }

        // ── 5. Switch (ToggleButton) ─────────────────────────────────
        ControlSection(
            icon = Icons.Outlined.ToggleOff,
            title = "Interruptor",
            subtitle = "Activa o desactiva una opción"
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Modo oscuro",
                        style = MaterialTheme.typography.bodyLarge,
                        color = OnSurface,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = if (isSwitchOn) "Activado" else "Desactivado",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isSwitchOn) Primary else OnSurfaceVariant
                    )
                }
                Switch(
                    checked = isSwitchOn,
                    onCheckedChange = {
                        isSwitchOn = it
                        resultText = "Switch: ${if (it) "ENCENDIDO" else "APAGADO"}"
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Primary
                    )
                )
            }
        }

        // ── 6. ExposedDropdownMenuBox (Spinner) ──────────────────────
        ControlSection(
            icon = Icons.Outlined.Star,
            title = "Menú desplegable",
            subtitle = "Selecciona una categoría de la lista"
        ) {
            ExposedDropdownMenuBox(
                expanded = spinnerExpanded,
                onExpandedChange = { spinnerExpanded = it }
            ) {
                OutlinedTextField(
                    value = selectedCategory,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoría de receta") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = spinnerExpanded)
                    },
                    modifier = Modifier
                        .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, enabled = true)
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = spinnerExpanded,
                    onDismissRequest = { spinnerExpanded = false }
                ) {
                    categoriaOptions.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat) },
                            onClick = {
                                selectedCategory = cat
                                spinnerExpanded = false
                                resultText = "Categoría seleccionada: $cat"
                            }
                        )
                    }
                }
            }
        }

        // ── 7. Slider (SeekBar) ──────────────────────────────────────
        ControlSection(
            icon = Icons.Outlined.Star,
            title = "Nivel de dificultad",
            subtitle = "Desliza para ajustar el nivel"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(spacing_4)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Dificultad",
                        style = MaterialTheme.typography.bodyLarge,
                        color = OnSurface,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = when (sliderValue.toInt()) {
                            1 -> "Fácil"
                            2 -> "Medio"
                            else -> "Difícil"
                        },
                        style = MaterialTheme.typography.labelLarge,
                        color = Primary,
                        fontWeight = FontWeight.Bold
                    )
                }
                Slider(
                    value = sliderValue,
                    onValueChange = {
                        sliderValue = it
                        resultText = "Slider: ${when (it.toInt()) { 1 -> "Fácil"; 2 -> "Medio"; else -> "Difícil" }}"
                    },
                    valueRange = 1f..3f,
                    steps = 1,
                    modifier = Modifier.fillMaxWidth(),
                    colors = SliderDefaults.colors(
                        thumbColor = Primary,
                        activeTrackColor = Primary
                    )
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    dificultadOptions.forEach { label ->
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall,
                            color = OnSurfaceVariant,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }

        // Botón CONFIRMAR
        Button(
            onClick = {
                resultText = buildSummary(
                    isSwitchOn, isCheckbox1, isCheckbox2, isCheckbox3,
                    selectedRadio, sliderValue, selectedCategory
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(rounded_md),
            colors = ButtonDefaults.buttonColors(containerColor = Primary)
        ) {
            Text(
                text = "CONFIRMAR Y VER RESULTADO",
                fontWeight = FontWeight.Bold,
                color = Color.White,
                letterSpacing = 0.5.sp
            )
        }

        val navBarPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
        Spacer(modifier = Modifier.height(spacing_12 + navBarPadding))
    }
}

@Composable
private fun ControlSection(
    icon: ImageVector,
    title: String,
    subtitle: String,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceContainerLowest, shape = RoundedCornerShape(rounded_lg))
            .padding(spacing_6)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(spacing_4)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(spacing_3)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Tertiary,
                    modifier = Modifier.size(18.dp)
                )
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        color = OnSurface,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = OnSurfaceVariant,
                        fontSize = 10.sp
                    )
                }
            }
            content()
        }
    }
}

@Composable
private fun CheckboxRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spacing_3)
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = Primary,
                uncheckedColor = OnSurfaceVariant
            )
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = if (checked) Primary else OnSurface,
            fontWeight = if (checked) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

private fun buildCheckboxResult(c1: Boolean, c2: Boolean, c3: Boolean): String {
    val selected = buildList {
        if (c1) add("Sin Gluten")
        if (c2) add("Vegetariano")
        if (c3) add("Bajo en calorías")
    }
    return if (selected.isEmpty()) "Sin filtros seleccionados"
    else "Filtros: ${selected.joinToString(", ")}"
}

private fun buildSummary(
    switchOn: Boolean,
    c1: Boolean, c2: Boolean, c3: Boolean,
    radio: String,
    slider: Float,
    spinner: String
): String {
    val difficulty = when (slider.toInt()) { 1 -> "Fácil"; 2 -> "Medio"; else -> "Difícil" }
    val checkboxes = buildList {
        if (c1) add("Sin Gluten")
        if (c2) add("Vegetariano")
        if (c3) add("Bajo cal")
    }
    return """
Modo oscuro: ${if (switchOn) "Sí" else "No"}
Filtros: ${if (checkboxes.isEmpty()) "Ninguno" else checkboxes.joinToString()}
Dieta: $radio
Dificultad: $difficulty
Categoría: $spinner
    """.trimIndent()
}
