package com.graphicquest.ar.pipvideo.player

data class VideoPlayerState(
    val isPlaying: Boolean = false,
    val currentPositionMs: Long = 0,
    val durationMs: Long = 0,
    val videoUrl: String = ""
)
