package com.example.autovolume

import kotlin.math.log10
import kotlin.math.sqrt
import android.media.AudioManager
import android.content.Context
import android.media.AudioRecord

class AutoVolumeControl(private val context: Context) {
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    // Function to calculate RMS value of a buffer
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
        val minRMS = 0.0001
        val maxRMS = 0.1
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

    // Function to calculate STA/LTA ratio from RMS values
    private fun  calculateSTALTA(rmsValues: List<Double>): Double {
        val shortWindow = rmsValues.size / 10 // 10% window
        val longWindow = rmsValues.size // 100% window

        val sta = rmsValues.take(shortWindow).average()
        val lta = rmsValues.take(longWindow).average()

        if (lta.isNaN() || sta.isNaN() || lta == 0.0) {
            return 0.0
        }

        return sta / lta
    }

    // Function to get RMS values from buffer with a sliding window
    private fun getRMSValues(bufferQueue: BufferQueue): List<Double> {
        val rmsValues = mutableListOf<Double>()

        for (buffer in bufferQueue) {
            rmsValues.add(calculateRMS(buffer))
        }

        return rmsValues
    }

    // Function to adjust volume based on RMS and STA/LTA ratio
    fun adjustVolumeWithSTALTA(bufferQueue: BufferQueue) {

        if (bufferQueue.isEmpty()) {
            return
        }


        val rmsValues = getRMSValues(bufferQueue)
        val stalta = calculateSTALTA(rmsValues)

        // Normalize the volume control based on STA/LTA ratio
        val volumeAdjustmentFactor = if (stalta > 1.0) 1.0 else 0.5

        if (rmsValues.isEmpty()) {
            return
        }

        val adjustedRMS = rmsValues.average() * volumeAdjustmentFactor

        val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        val newVolume = mapRMSValueToVolume(adjustedRMS)
        val smoothedVolume = smoothVolumeAdjustment(currentVolume, newVolume, 2)

        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, smoothedVolume, 0)
    }
}
