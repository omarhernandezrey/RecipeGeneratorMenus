package com.example.recipe_generator.presentation.components

import android.annotation.SuppressLint
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.recipe_generator.R
import com.example.recipe_generator.data.remote.YouTubeVideoUrlUtils
import com.example.recipe_generator.presentation.theme.Background
import com.example.recipe_generator.presentation.theme.OnSurface
import com.example.recipe_generator.presentation.theme.OnSurfaceVariant
import com.example.recipe_generator.presentation.theme.SurfaceContainerHigh
import com.example.recipe_generator.presentation.theme.rounded_lg
import com.example.recipe_generator.presentation.theme.spacing_2
import com.example.recipe_generator.presentation.theme.spacing_3
import com.example.recipe_generator.presentation.theme.spacing_4
import com.example.recipe_generator.presentation.theme.spacing_6

@Composable
@SuppressLint("SetJavaScriptEnabled")
fun RecipeVideoPlayerDialog(
    recipeTitle: String,
    videoUrl: String,
    onDismiss: () -> Unit
) {
    BackHandler(onBack = onDismiss)

    val normalizedTitle = recipeTitle.trim().ifBlank {
        stringResource(R.string.recipe_video_title)
    }
    val videoId = remember(videoUrl) { YouTubeVideoUrlUtils.extractVideoId(videoUrl) }
    val playableUrl = remember(videoUrl, videoId) {
        videoId?.let(YouTubeVideoUrlUtils::buildEmbedUrl) ?: videoUrl
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        var webViewRef by remember { mutableStateOf<WebView?>(null) }
        var isLoading by remember(playableUrl) { mutableStateOf(true) }

        DisposableEffect(webViewRef) {
            onDispose {
                webViewRef?.apply {
                    onPause()
                    stopLoading()
                    loadUrl("about:blank")
                    removeAllViews()
                    destroy()
                }
                webViewRef = null
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.74f))
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .systemBarsPadding(),
                color = Background
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = spacing_4, vertical = spacing_3)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(spacing_3)
                    ) {
                        YouTubeBrandIcon(modifier = Modifier.size(width = 36.dp, height = 24.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = normalizedTitle,
                                style = MaterialTheme.typography.titleLarge,
                                color = OnSurface,
                                maxLines = 2
                            )
                            if (videoId == null) {
                                Text(
                                    text = stringResource(R.string.recipe_video_searching_inside_youtube),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = OnSurfaceVariant
                                )
                            }
                        }
                        IconButton(onClick = onDismiss) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = stringResource(R.string.recipe_video_close),
                                tint = OnSurface
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(spacing_3))

                    BoxWithConstraints(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        val isLandscape = maxWidth > maxHeight
                        val playerModifier = when {
                            videoId == null -> Modifier.fillMaxSize()
                            isLandscape -> Modifier.fillMaxHeight().aspectRatio(16f / 9f)
                            else -> Modifier.fillMaxWidth().aspectRatio(16f / 9f)
                        }

                        Surface(
                            modifier = playerModifier,
                            shape = RoundedCornerShape(rounded_lg),
                            color = SurfaceContainerHigh
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                AndroidView(
                                    modifier = Modifier.fillMaxSize(),
                                    factory = { context ->
                                        WebView(context).apply {
                                            webViewRef = this
                                            overScrollMode = WebView.OVER_SCROLL_NEVER
                                            setBackgroundColor(android.graphics.Color.BLACK)
                                            settings.apply {
                                                javaScriptEnabled = true
                                                domStorageEnabled = true
                                                mediaPlaybackRequiresUserGesture = false
                                                loadsImagesAutomatically = true
                                                loadWithOverviewMode = true
                                                useWideViewPort = true
                                                cacheMode = WebSettings.LOAD_DEFAULT
                                            }
                                            webChromeClient = WebChromeClient()
                                            webViewClient = object : WebViewClient() {
                                                override fun onPageFinished(view: WebView?, url: String?) {
                                                    isLoading = false
                                                }
                                            }
                                            loadUrl(playableUrl)
                                        }
                                    },
                                    update = { webView ->
                                        webViewRef = webView
                                        if (webView.url != playableUrl) {
                                            isLoading = true
                                            webView.loadUrl(playableUrl)
                                        }
                                    }
                                )

                                if (isLoading) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(Color.Black.copy(alpha = 0.22f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.spacedBy(spacing_2)
                                        ) {
                                            CircularProgressIndicator(color = Color.White)
                                            Text(
                                                text = stringResource(R.string.recipe_video_loading),
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = Color.White
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
    }
}
