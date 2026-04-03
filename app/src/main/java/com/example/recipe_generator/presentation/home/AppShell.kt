package com.example.recipe_generator.presentation.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.recipe_generator.presentation.leftmenu.MainScreen

/**
 * AppShell - Estructura completa de la app con NavigationBar
 *
 * Contiene:
 * - NavigationBar en la parte inferior (4 tabs)
 * - Contenido que cambia según la tab seleccionada
 * - FASE 3: Implementación completa del PLAN MAESTRO
 */
@Composable
fun AppShell(
    modifier: Modifier = Modifier,
    onLogout: () -> Unit
) {
    // Estado para trackear qué tab está seleccionada
    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                // Tab 1: Inicio (MainScreen con 2 paneles)
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = {
                        Icon(
                            Icons.Filled.Home,
                            contentDescription = "Inicio"
                        )
                    },
                    label = { Text("Inicio") }
                )

                // Tab 2: Favoritos (Placeholder)
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = {
                        Icon(
                            Icons.Outlined.FavoriteBorder,
                            contentDescription = "Favoritos"
                        )
                    },
                    label = { Text("Favoritos") }
                )

                // Tab 3: Generador de Menú (Placeholder)
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = {
                        Icon(
                            Icons.Filled.Star,
                            contentDescription = "Generador"
                        )
                    },
                    label = { Text("Generador") }
                )

                // Tab 4: Ajustes (Placeholder)
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = {
                        Icon(
                            Icons.Filled.Settings,
                            contentDescription = "Ajustes"
                        )
                    },
                    label = { Text("Ajustes") }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (selectedTab) {
                // Tab 0: Inicio con MainScreen (2 paneles: Perfil, Fotos, Video, Web, Controles)
                0 -> MainScreen()

                // Tab 1: Favoritos (Placeholder para ahora)
                1 -> androidx.compose.material3.Text("Favoritos - En desarrollo")

                // Tab 2: Generador de Menú (Placeholder para ahora)
                2 -> androidx.compose.material3.Text("Generador - En desarrollo")

                // Tab 3: Ajustes (Placeholder para ahora)
                3 -> androidx.compose.material3.Text("Ajustes - En desarrollo")
            }
        }
    }
}


