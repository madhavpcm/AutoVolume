package com.example.library

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

import com.example.autovolume.AmbientSoundSensor
import com.example.autovolume.VolumeAdjuster
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner

class MainActivity : ComponentActivity(), AmbientSoundSensor.OnNoiseDetectedListener {

    private lateinit var ambientSoundSensor: AmbientSoundSensor
    private lateinit var volumeAdjuster: VolumeAdjuster
    private val noiseLevelViewModel: NoiseLevelViewModel by viewModels {
        NoiseLevelViewModelFactory(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        volumeAdjuster = VolumeAdjuster(this)
        ambientSoundSensor = AmbientSoundSensor(this, this, noiseLevelViewModel.isAutoVolumeEnabled())
        setContent {
            MainScreen(viewModel = noiseLevelViewModel)
        }
    }

    override fun onResume() {
        super.onResume()
        ambientSoundSensor.start()
    }

    override fun onPause() {
        super.onPause()
        ambientSoundSensor.stop()
    }

    override fun onNoiseDetected(staLtaValue: Double) {
        if (noiseLevelViewModel.isAutoVolumeEnabled.value) {
            val threshold = 3.0
            val lowVolumeLevel = 2
            val normalVolumeLevel = 5
            volumeAdjuster.adjustVolume(staLtaValue, threshold, lowVolumeLevel, normalVolumeLevel)
        }
        noiseLevelViewModel.updateNoiseLevel(staLtaValue)
    }
}

@Composable
fun MainScreen(viewModel: NoiseLevelViewModel) {
    val noiseLevel by viewModel.noiseLevel.collectAsState()
    val isAutoVolumeEnabled by viewModel.isAutoVolumeEnabled.collectAsState()
    val currentVolumeLevel by viewModel.currentVolumeLevel.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Noise Detection", style = MaterialTheme.typography.headlineMedium)
        Text(text = noiseLevel, style = MaterialTheme.typography.bodyLarge)

        // Toggle for auto volume adjustment
        Row(modifier = Modifier.padding(vertical = 8.dp)) {
            Text(text = "Auto Volume Adjustment", modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
            Switch(
                checked = isAutoVolumeEnabled,
                onCheckedChange = { viewModel.toggleAutoVolumeAdjustment() }
            )
        }

        // Display current volume level
        Text(text = currentVolumeLevel, style = MaterialTheme.typography.bodyLarge)
    }
}