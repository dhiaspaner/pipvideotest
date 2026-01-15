package com.graphicquest.ar.pipvideo.player

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import platform.AVFoundation.AVPlayer
import platform.AVFoundation.AVPlayerItem
import platform.AVFoundation.AVPlayerLayer
import platform.AVFoundation.currentItem
import platform.AVFoundation.currentTime
import platform.AVFoundation.pause
import platform.AVFoundation.play
import platform.AVFoundation.seekToTime
import platform.CoreMedia.CMTimeMake
import platform.Foundation.NSURL

actual class VideoPlayerController {
    private val _state = MutableStateFlow(VideoPlayerState())
    actual val state: StateFlow<VideoPlayerState> = _state.asStateFlow()
    
    private var avPlayer: AVPlayer? = null
    private var playerLayer: AVPlayerLayer? = null
    private val scope = CoroutineScope(Dispatchers.Main)
    private var progressJob: Job? = null

    init {
        avPlayer = AVPlayer()
        playerLayer = AVPlayerLayer.playerLayerWithPlayer(avPlayer)
    }

    actual fun play(url: String) {
        val nsUrl = NSURL.URLWithString(url) ?: return
        val item = AVPlayerItem(uRL = nsUrl)
        avPlayer?.replaceCurrentItemWithPlayerItem(item)
        avPlayer?.play()
        
        updateState { it.copy(videoUrl = url, isPlaying = true) }
        startProgressUpdates()
    }

    actual fun pause() {
        avPlayer?.pause()
        updateState { it.copy(isPlaying = false) }
        stopProgressUpdates()
    }

    actual fun resume() {
        avPlayer?.play()
        updateState { it.copy(isPlaying = true) }
        startProgressUpdates()
    }

    actual fun seekTo(positionMs: Long) {
        val time = CMTimeMake(value = positionMs, timescale = 1000)
        avPlayer?.seekToTime(time)
    }

    actual fun release() {
        avPlayer?.pause()
        avPlayer = null
        playerLayer = null
        stopProgressUpdates()
    }
    
    fun getPlayerLayer(): AVPlayerLayer? = playerLayer

    private fun startProgressUpdates() {
        progressJob?.cancel()
        progressJob = scope.launch {
            while (isActive) {
                avPlayer?.let { player ->
                    val currentSeconds = player.currentTime().value.toDouble() / player.currentTime().timescale.toDouble()
                    val durationSeconds = player.currentItem?.duration?.value?.toDouble()?.div(player.currentItem?.duration?.timescale?.toDouble() ?: 1.0) ?: 0.0
                    
                    updateState { 
                        it.copy(
                            currentPositionMs = (currentSeconds * 1000).toLong(),
                            durationMs = (durationSeconds * 1000).toLong()
                        ) 
                    }
                }
                delay(1000)
            }
        }
    }

    private fun stopProgressUpdates() {
        progressJob?.cancel()
    }

    private fun updateState(update: (VideoPlayerState) -> VideoPlayerState) {
        _state.value = update(_state.value)
    }
}
