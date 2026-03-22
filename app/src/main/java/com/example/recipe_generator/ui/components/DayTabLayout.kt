package com.example.recipe_generator.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.recipe_generator.ui.theme.*

@Composable
fun DayTabLayout(selectedDay: String, onDaySelected: (String) -> Unit) {
    val days = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = spacing_6)
            .padding(bottom = spacing_6, top = spacing_2),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        days.forEach { day ->
            val isSelected = day == selectedDay
            // Matching Stitch: px-6 py-2.5 rounded-full font-semibold
            // Selected: bg-primary shadow-lg
            // Unselected: bg-surface-container-low
            Button(
                onClick = { onDaySelected(day) },
                shape = RoundedCornerShape(rounded_full),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSelected) Primary else SurfaceContainerLow,
                    contentColor = if (isSelected) Color.White else OnSurfaceVariant
                ),
                elevation = if (isSelected) {
                    ButtonDefaults.buttonElevation(
                        defaultElevation = 8.dp,
                        pressedElevation = 4.dp
                    )
                } else {
                    ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                },
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
                modifier = Modifier.heightIn(min = 44.dp)
            ) {
                Text(
                    text = day,
                    fontSize = 14.sp,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                    maxLines = 1
                )
            }
        }
    }
}
