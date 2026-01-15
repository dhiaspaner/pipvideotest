package com.graphicquest.ar.pipvideo.player

import kotlinx.coroutines.flow.StateFlow

expect class VideoPlayerController {
    val state: StateFlow<VideoPlayerState>
    
    fun play(url: String)
    fun pause()
    fun resume()
    fun seekTo(positionMs: Long)
    fun release()
}
