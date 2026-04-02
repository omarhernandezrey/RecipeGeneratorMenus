package com.example.recipe_generator.presentation.leftmenu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.recipe_generator.presentation.components.DetailEditorialTopAppBar
import com.example.recipe_generator.presentation.components.editorialTopBarContentPadding
import com.example.recipe_generator.presentation.navigation.ComposeScreenFragment
import com.example.recipe_generator.presentation.theme.OnSurfaceVariant
import com.example.recipe_generator.presentation.theme.Surface

/**
 * LeftMenuFragment — Fragment host del MainScreen (layout de dos paneles).
 *
 * Extiende ComposeScreenFragment para usar ComposeView como host.
 * Cumple LF6: Fragment con ComposeView.
 * Muestra la navegación de dos paneles: LeftMenu + Content.
 *
 * Capa: Presentation
 */
class LeftMenuFragment : ComposeScreenFragment() {

    @Composable
    override fun ScreenContent() {
        val topPadding = editorialTopBarContentPadding()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Surface)
        ) {
            // Panel de dos columnas debajo del TopAppBar
            MainScreen(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = topPadding)
            )

            // TopAppBar con botón de volver
            DetailEditorialTopAppBar(
                title = "Pantallas Demo",
                leadingContent = {
                    IconButton(onClick = { requireActivity().onBackPressedDispatcher.onBackPressed() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = OnSurfaceVariant
                        )
                    }
                }
            )
        }
    }
}
