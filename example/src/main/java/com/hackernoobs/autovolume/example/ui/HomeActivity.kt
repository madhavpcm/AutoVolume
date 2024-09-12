package com.hackernoobs.autovolume.example.ui

import android.app.Application
import android.content.Context
import android.media.AudioManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.hackernoobs.autovolume.AmbientSoundSensor
//import com.hackernoobs.autovolume.VolumeAdjuster
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

import androidx.compose.runtime.Composable
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@AndroidEntryPoint
 class HomeActivity: ComponentActivity() {

    private val noiseLevelViewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MainScreen(viewModel = noiseLevelViewModel)
        }
    }
    @Composable
    fun MainScreen(viewModel: MainActivityViewModel) {
        val isAutoVolumeEnabled by viewModel.isAutoVolumeEnabled.collectAsState()
        val currentVolumeLevel by viewModel.currentVolumeLevel.collectAsState()

        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Noise Detection", style = MaterialTheme.typography.headlineMedium)

            Row(modifier = Modifier.padding(vertical = 8.dp)) {
                Text(
                        text = "Auto Volume Adjustment",
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyLarge
                )
                Switch(
                        checked = isAutoVolumeEnabled,
                        onCheckedChange = { viewModel.toggleAutoVolumeAdjustment() }
                )
            }

            Text(text = "Callback volume value after adjustment: $currentVolumeLevel", style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@HiltViewModel
class MainActivityViewModel @Inject constructor (context: Application) : ViewModel() {
    private var ambientSoundSensor: AmbientSoundSensor? = null

    private val _isAutoVolumeEnabled = MutableStateFlow(false)
    val isAutoVolumeEnabled: StateFlow<Boolean> = _isAutoVolumeEnabled

    private var _currentVolumeLevel: MutableStateFlow<Int> = MutableStateFlow(0);
    val currentVolumeLevel: StateFlow<Int> = _currentVolumeLevel

    private val updateCurrentVolumeLevel: (Int) -> Unit = { newVolume: Int ->
        _currentVolumeLevel.value = newVolume
    }

    init {
        // Initialize the volume adjuster and sound sensor
//        volumeAdjuster = VolumeAdjuster(context)
        ambientSoundSensor = AmbientSoundSensor(context, updateCurrentVolumeLevel)
    }

    fun toggleAutoVolumeAdjustment() {
        _isAutoVolumeEnabled.value = !_isAutoVolumeEnabled.value

        if (_isAutoVolumeEnabled.value) {
            ambientSoundSensor?.start()
        } else {
            ambientSoundSensor?.stop()
        }
    }

    override fun onCleared() {
        super.onCleared()
        ambientSoundSensor?.stop()
    }
}
