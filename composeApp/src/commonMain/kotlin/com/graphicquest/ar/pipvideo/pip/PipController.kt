package com.graphicquest.ar.pipvideo.pip

import kotlinx.coroutines.flow.StateFlow

expect class PipController {
    val isInPipMode: StateFlow<Boolean>
    
    fun enterPip()
    fun isPipSupported(): Boolean
}
