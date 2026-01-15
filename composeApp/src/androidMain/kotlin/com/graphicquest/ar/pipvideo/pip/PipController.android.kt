package com.graphicquest.ar.pipvideo.pip

import android.app.Activity
import android.app.PictureInPictureParams
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Rational
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.lang.ref.WeakReference

actual class PipController(private val context: Context) {
    private val _isInPipMode = MutableStateFlow(false)
    actual val isInPipMode: StateFlow<Boolean> = _isInPipMode.asStateFlow()
    
    private val activityRef = WeakReference(context as? Activity)

    actual fun enterPip() {
        if (isPipSupported()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val activity = activityRef.get()
                val params = PictureInPictureParams.Builder()
                    .setAspectRatio(Rational(16, 9))
                    .build()
                activity?.enterPictureInPictureMode(params)
            }
        }
    }

    actual fun isPipSupported(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.packageManager.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE)
        } else {
            false
        }
    }
    
    fun onPipModeChanged(isInPip: Boolean) {
        _isInPipMode.value = isInPip
    }
}
