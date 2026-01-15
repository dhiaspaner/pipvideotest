package com.graphicquest.ar.pipvideo.player

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun VideoPlayerSurface(
    controller: VideoPlayerController,
    modifier: Modifier = Modifier
)
