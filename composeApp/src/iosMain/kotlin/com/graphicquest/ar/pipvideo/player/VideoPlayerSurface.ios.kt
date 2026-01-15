package com.graphicquest.ar.pipvideo.player

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import platform.AVFoundation.AVPlayerLayer
import platform.UIKit.UIView
import platform.QuartzCore.CATransaction

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun VideoPlayerSurface(
    controller: VideoPlayerController,
    modifier: Modifier
) {
    UIKitView(
        factory = {
            val view = UIView()
            controller.getPlayerLayer()?.let { layer ->
                layer.frame = view.bounds
                view.layer.addSublayer(layer)
            }
            view
        },
        modifier = modifier,
        update = { view ->
            CATransaction.begin()
            CATransaction.setDisableActions(true)
            controller.getPlayerLayer()?.frame = view.bounds
            CATransaction.commit()
        }
    )
}
