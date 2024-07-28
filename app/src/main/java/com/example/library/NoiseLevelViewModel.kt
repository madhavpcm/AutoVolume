package com.example.library

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
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

    init {
        updateCurrentVolumeLevel()
        registerVolumeChangeReceiver()
    }

    fun isAutoVolumeEnabled(): MutableStateFlow<Boolean> {
        return _isAutoVolumeEnabled;
    }

    private fun registerVolumeChangeReceiver() {
        val filter = IntentFilter()
        filter.addAction("android.media.VOLUME_CHANGED_ACTION")
        this.context.registerReceiver(volumeChangeReceiver, filter)
    }

    private val volumeChangeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            updateCurrentVolumeLevel()
        }
    }

    fun updateNoiseLevel(staLtaValue: Double) {
        viewModelScope.launch {
            _noiseLevel.value = "Noise level: $staLtaValue"
            updateCurrentVolumeLevel()
        }
    }

    private fun updateCurrentVolumeLevel() {
        val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val volumePercentage = (currentVolume * 100 / maxVolume).toInt()
        _currentVolumeLevel.value = "Volume: $currentVolume%"
    }

    fun toggleAutoVolumeAdjustment() {
        viewModelScope.launch {
            _isAutoVolumeEnabled.value = !_isAutoVolumeEnabled.value
        }
    }

    override fun onCleared() {
        super.onCleared()
        context.unregisterReceiver(volumeChangeReceiver)
    }
}
