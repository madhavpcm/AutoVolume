package com.example.library

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
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

class MainActivity : ComponentActivity(), AmbientSoundSensor.OnNoiseDetectedListener {

    private lateinit var ambientSoundSensor: AmbientSoundSensor
    private lateinit var volumeAdjuster: VolumeAdjuster
    private val noiseLevelViewModel: NoiseLevelViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        volumeAdjuster = VolumeAdjuster(this)
        ambientSoundSensor = AmbientSoundSensor(this, this)

        setContent {
                // Pass the ViewModel to the composable
                MainScreen(noiseLevelViewModel)
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
        val threshold = 3.0
        val lowVolumeLevel = 2
        val normalVolumeLevel = 5
        volumeAdjuster.adjustVolume(staLtaValue, threshold, lowVolumeLevel, normalVolumeLevel)
        noiseLevelViewModel.updateNoiseLevel(staLtaValue)
    }
}

class NoiseLevelViewModel : ViewModel() {
    private val _noiseLevel = MutableStateFlow("Listening...")
    val noiseLevel: StateFlow<String> = _noiseLevel

    fun updateNoiseLevel(staLtaValue: Double) {
        viewModelScope.launch {
            _noiseLevel.value = "Noise level: $staLtaValue"
        }
    }
}

@Composable
fun MainScreen(viewModel: NoiseLevelViewModel) {
    val noiseLevel by viewModel.noiseLevel.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Noise Detection", style = MaterialTheme.typography.headlineMedium)
        Text(text = noiseLevel, style = MaterialTheme.typography.bodyLarge)
    }
}