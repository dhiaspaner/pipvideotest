package com.graphicquest.ar.pipvideo.player

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.ui.PlayerView

@Composable
actual fun VideoPlayerSurface(
    controller: VideoPlayerController,
    modifier: Modifier
) {
    AndroidView(
        factory = { context ->
            PlayerView(context).apply {
                player = controller.getPlayer()
            }
        },
        modifier = modifier
    )
}
