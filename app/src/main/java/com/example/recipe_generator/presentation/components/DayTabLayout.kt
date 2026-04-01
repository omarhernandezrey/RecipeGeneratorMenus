package com.example.recipe_generator.presentation.components

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
import com.example.recipe_generator.presentation.theme.*

@Composable
fun DayTabLayout(selectedDay: String, onDaySelected: (String) -> Unit) {
    val days = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = spacing_6)
            .padding(bottom = spacing_4),
        horizontalArrangement = Arrangement.spacedBy(spacing_3)
    ) {
        days.forEach { day ->
            val isSelected = day == selectedDay
            Button(
                onClick = { onDaySelected(day) },
                shape = RoundedCornerShape(rounded_full),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSelected) Primary else SurfaceContainerLow,
                    contentColor = if (isSelected) Color.White else OnSurfaceVariant
                ),
                elevation = if (isSelected) {
                    ButtonDefaults.buttonElevation(
                        defaultElevation = 3.dp,
                        pressedElevation = 1.dp
                    )
                } else {
                    ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                },
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
                modifier = Modifier.heightIn(min = 40.dp)
            ) {
                Text(
                    text = day,
                    fontSize = 13.sp,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                    maxLines = 1
                )
            }
        }
    }
}
