package com.example.autovolume

import kotlin.math.log10
import kotlin.math.sqrt
import android.media.AudioManager
import android.content.Context

class AutoVolumeControl(private val context: Context) {
    // Function to calculate RMS value of a buffer
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private fun calculateRMS(buffer: ShortArray): Double {
        var sum = 0.0
        for (sample in buffer) {
            sum += sample * sample
        }
        return sqrt(sum / buffer.size)
    }
    fun getMaxVolume(streamType: Int = AudioManager.STREAM_MUSIC): Int {
        return audioManager.getStreamMaxVolume(streamType)
    }

    // Function to map RMS value to a volume level using a logarithmic scale
    private fun mapRMSValueToVolume(rms: Double): Int {
        // Define the min and max RMS values for the mapping
        val minRMS = 0.009
        val maxRMS = 0.26
        val maxVolume = getMaxVolume()

        // Calculate the logarithmic volume level
        val normalizedRMS = (rms - minRMS) / (maxRMS - minRMS)
        val volume = 20 * log10(normalizedRMS + 1.0)

        // Map the logarithmic volume to the desired range
        return (volume * maxVolume / 100).toInt().coerceIn(1, maxVolume)
    }

    // Function to adjust volume based on RMS value
    fun adjustVolume(buffer: ShortArray) {
        val rms = calculateRMS(buffer)
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mapRMSValueToVolume(rms), 0)
    }

    // Function to avoid frequent volume adjustments
    fun smoothVolumeAdjustment(currentVolume: Int, newVolume: Int, threshold: Int): Int {
        return if (Math.abs(currentVolume - newVolume) > threshold) {
            newVolume
        } else {
            currentVolume
        }
    }
}
