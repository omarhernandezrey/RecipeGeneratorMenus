package com.example.recipe_generator.presentation.leftmenu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.recipe_generator.presentation.controls.ControlsScreen
import com.example.recipe_generator.presentation.photos.PhotosScreen
import com.example.recipe_generator.presentation.profile.ProfileHubScreen
import com.example.recipe_generator.presentation.theme.OutlineVariant
import com.example.recipe_generator.presentation.video.VideoScreen
import com.example.recipe_generator.presentation.web.WebScreen

/**
 * MainScreen — Layout de dos paneles.
 *
 * Row: LeftMenuPanel (weight 0.25) | Divider | RightPanel (weight 0.75).
 * Selección del panel izquierdo cambia el contenido del panel derecho.
 * Cubre F2-04: MainScreen con layout de dos paneles.
 * Cubre F2-06: estado de navegación del panel derecho.
 * Cubre LF6: Fragment host con ComposeView.
 *
 * Capa: Presentation
 */
@Composable
fun MainScreen(modifier: Modifier = Modifier, onBack: (() -> Unit)? = null) {
    // F2-06: estado local de selección del panel — remember { mutableStateOf() } (LF4)
    var selectedPanel by remember { mutableStateOf(LeftMenuPanel.Profile) }

    Box(modifier = modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxSize()) {
            // Panel izquierdo — 25% del ancho
            LeftMenuScreen(
                selectedPanel = selectedPanel,
                onPanelSelected = { selectedPanel = it },
                modifier = Modifier
                    .weight(0.25f)
                    .fillMaxHeight()
            )

            // Divisor vertical
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .fillMaxHeight()
                    .background(OutlineVariant.copy(alpha = 0.3f))
            )

            // Panel derecho — 75% del ancho (contenido cambia según selección)
            Box(
                modifier = Modifier
                    .weight(0.75f)
                    .fillMaxHeight()
            ) {
                when (selectedPanel) {
                    // F3-08: ProfileHubScreen completo (Foto, Estudios, Experiencia…)
                    LeftMenuPanel.Profile -> ProfileHubScreen(
                        modifier = Modifier.fillMaxSize(),
                        onClose = { onBack?.invoke() }
                    )

                    // F3-09: PhotosScreen con LazyColumn (LF8: ListView equiv)
                    LeftMenuPanel.Photos -> PhotosScreen(modifier = Modifier.fillMaxSize())

                    // F3-10: VideoScreen con AndroidView { VideoView } (LF7)
                    LeftMenuPanel.Video -> VideoScreen(modifier = Modifier.fillMaxSize())

                    // F3-11: WebScreen con AndroidView { WebView } (LF7)
                    LeftMenuPanel.Web -> WebScreen(modifier = Modifier.fillMaxSize())

                    // F3-12: ControlsScreen con todos los controles (LF8)
                    LeftMenuPanel.Controls -> ControlsScreen(modifier = Modifier.fillMaxSize())
                }
            }
        }

    }
}
