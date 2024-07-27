package com.example.autovolume

import android.content.Context
import android.media.AudioManager

class VolumeAdjuster(private val context: Context) {

    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    fun adjustVolume(staLtaValue: Double, threshold: Double, lowVolumeLevel: Int, normalVolumeLevel: Int) {
        if (staLtaValue > threshold) {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, lowVolumeLevel, 0)
        } else {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, normalVolumeLevel, 0)
        }
    }
}
