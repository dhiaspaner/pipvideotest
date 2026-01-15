package com.graphicquest.ar.pipvideo.pip

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import platform.AVKit.AVPictureInPictureController
import platform.AVKit.AVPictureInPictureControllerDelegateProtocol
import platform.AVFoundation.AVPlayerLayer
import platform.darwin.NSObject

actual class PipController(private val playerLayer: AVPlayerLayer?) {
    private val _isInPipMode = MutableStateFlow(false)
    actual val isInPipMode: StateFlow<Boolean> = _isInPipMode.asStateFlow()
    
    private var pipController: AVPictureInPictureController? = null
    
    init {
        if (playerLayer != null && AVPictureInPictureController.isPictureInPictureSupported()) {
            pipController = AVPictureInPictureController(playerLayer)
            pipController?.delegate = object : NSObject(), AVPictureInPictureControllerDelegateProtocol {
                override fun pictureInPictureControllerDidStartPictureInPicture(pictureInPictureController: AVPictureInPictureController) {
                    _isInPipMode.value = true
                }
                
                override fun pictureInPictureControllerDidStopPictureInPicture(pictureInPictureController: AVPictureInPictureController) {
                    _isInPipMode.value = false
                }
            }
        }
    }

    actual fun enterPip() {
        if (pipController?.isPictureInPicturePossible() == true) {
            pipController?.startPictureInPicture()
        }
    }

    actual fun isPipSupported(): Boolean {
        return AVPictureInPictureController.isPictureInPictureSupported()
    }
}
