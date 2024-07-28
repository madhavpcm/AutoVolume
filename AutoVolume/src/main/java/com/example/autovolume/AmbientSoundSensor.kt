package com.example.autovolume

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.flow.MutableStateFlow

class AmbientSoundSensor(private val context: Context, private val listener: OnNoiseDetectedListener,  private val toggle: MutableStateFlow<Boolean>) {

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
            val autoVolumeCtx = AutoVolumeControl(context)
            val bufferQueue = BufferQueue(10)

            while (isRecording) {
                if (bufferQueue.size() >= 10) {
                    bufferQueue.dequeueBuffer()
                }
                if(toggle.value) {
                    val buffer = ShortArray(BUFFER_SIZE)

                    val statusCode = audioRecord.read(buffer, 0, BUFFER_SIZE)
                    if (statusCode != 0) {
                        bufferQueue.enqueueBuffer(buffer)
//                    autoVolumeCtx.adjustVolume(buffer)

                        autoVolumeCtx.adjustVolumeWithSTALTA(bufferQueue)
                    }

                }
            }

            audioRecord.stop()
            audioRecord.release()
        }.start()
    }

    fun stop() {
        isRecording = false
    }
}
