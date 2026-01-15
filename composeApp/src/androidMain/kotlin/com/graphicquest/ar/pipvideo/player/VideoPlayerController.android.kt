package com.graphicquest.ar.pipvideo.player

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

actual class VideoPlayerController(private val context: Context) {
    private val _state = MutableStateFlow(VideoPlayerState())
    actual val state: StateFlow<VideoPlayerState> = _state.asStateFlow()
    
    private var exoPlayer: ExoPlayer? = null
    private val scope = CoroutineScope(Dispatchers.Main)
    private var progressJob: Job? = null

    init {
        initializePlayer()
    }

    private fun initializePlayer() {
        exoPlayer = ExoPlayer.Builder(context).build().apply {
            addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    updateState { it.copy(isPlaying = isPlaying) }
                    if (isPlaying) startProgressUpdates() else stopProgressUpdates()
                }

                override fun onPlaybackStateChanged(playbackState: Int) {
                    if (playbackState == Player.STATE_READY) {
                        updateState { it.copy(durationMs = duration) }
                    }
                }
            })
        }
    }

    actual fun play(url: String) {
        exoPlayer?.apply {
            setMediaItem(MediaItem.fromUri(url))
            prepare()
            play()
        }
        updateState { it.copy(videoUrl = url) }
    }

    actual fun pause() {
        exoPlayer?.pause()
    }

    actual fun resume() {
        exoPlayer?.play()
    }

    actual fun seekTo(positionMs: Long) {
        exoPlayer?.seekTo(positionMs)
    }

    actual fun release() {
        exoPlayer?.release()
        exoPlayer = null
        stopProgressUpdates()
    }
    
    fun getPlayer(): ExoPlayer? = exoPlayer

    private fun startProgressUpdates() {
        progressJob?.cancel()
        progressJob = scope.launch {
            while (isActive) {
                exoPlayer?.let { player ->
                    updateState { it.copy(currentPositionMs = player.currentPosition) }
                }
                delay(1000) // Update every second
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
