package com.example.autovolume

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.core.app.ActivityCompat

class AmbientSoundSensor(private val context: Context, private val listener: OnNoiseDetectedListener) {

    interface OnNoiseDetectedListener {
        fun onNoiseDetected(staLtaValue: Double)
    }

    companion object {
        private const val SAMPLE_RATE = 44100
        private val BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT)
    }

    private var isRecording = false
    private lateinit var audioRecord: AudioRecord

    fun start() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            // Handle permission request
            return
        }

        audioRecord = AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, BUFFER_SIZE)
        audioRecord.startRecording()
        isRecording = true

        Thread {
            val buffer = ShortArray(BUFFER_SIZE)
            val staLtaAlgorithm = STALTAAlgorithm()

            while (isRecording) {
                audioRecord.read(buffer, 0, BUFFER_SIZE)
                val staLtaValue = staLtaAlgorithm.calculateSTALTA(buffer, SAMPLE_RATE)
                listener.onNoiseDetected(staLtaValue)
            }

            audioRecord.stop()
            audioRecord.release()
        }.start()
    }

    fun stop() {
        isRecording = false
    }
}
