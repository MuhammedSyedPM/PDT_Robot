package com.syed.jetpacktwo.util

import android.media.AudioManager
import android.media.ToneGenerator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Beeper {
    private var toneGenerator: ToneGenerator? = null
    private val scope = CoroutineScope(Dispatchers.Default + Job())
    private var lastBeepTime = 0L

    init {
        try {
            // Stream music with 100% volume
            toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun playConnectionSound() {
        scope.launch {
            try {
                // Play an ascending confirm tone
                toneGenerator?.startTone(ToneGenerator.TONE_SUP_CONFIRM, 200)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun playReadSound() {
        // Throttle the read sounds to prevent overlapping audio glitches if scanning 100 tags/sec
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastBeepTime > 30) { 
            lastBeepTime = currentTime
            scope.launch {
                try {
                    // Short high-pitched beep
                    toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 30)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun release() {
        try {
            toneGenerator?.release()
            toneGenerator = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
