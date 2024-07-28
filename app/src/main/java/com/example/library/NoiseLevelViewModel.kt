package com.example.library

import android.content.Context
import android.media.AudioManager
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NoiseLevelViewModel(private val context: Context) : ViewModel() {
    private val _noiseLevel = MutableStateFlow("Listening...")
    val noiseLevel: StateFlow<String> = _noiseLevel

    private val _isAutoVolumeEnabled = MutableStateFlow(true)
    val isAutoVolumeEnabled: StateFlow<Boolean> = _isAutoVolumeEnabled

    private val _currentVolumeLevel = MutableStateFlow("Volume: N/A")
    val currentVolumeLevel: StateFlow<String> = _currentVolumeLevel

    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private val handler = Handler(Looper.getMainLooper())
    private val volumeCheckRunnable = object : Runnable {
        override fun run() {
            updateCurrentVolumeLevel()
            handler.postDelayed(this, 1000) // Check volume every 1 second
        }
    }

    init {
        startVolumeChecking()
    }

    fun isAutoVolumeEnabled(): MutableStateFlow<Boolean> {
        return _isAutoVolumeEnabled
    }

    private fun startVolumeChecking() {
        handler.post(volumeCheckRunnable)
    }

    fun updateNoiseLevel(staLtaValue: Double) {
        viewModelScope.launch {
            _noiseLevel.value = "Noise level: $staLtaValue"
        }
    }

    private fun updateCurrentVolumeLevel() {
        val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val volumePercentage = (currentVolume * 100 / maxVolume)
        _currentVolumeLevel.value = "Volume: $volumePercentage%"
    }

    fun toggleAutoVolumeAdjustment() {
        viewModelScope.launch {
            _isAutoVolumeEnabled.value = !_isAutoVolumeEnabled.value
        }
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacks(volumeCheckRunnable)
    }
}
