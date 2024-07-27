package com.example.autovolume

class STALTAAlgorithm {

    fun calculateSTALTA(buffer: ShortArray, sampleRate: Int): Double {
        val shortWindow = sampleRate / 10 // 100ms window
        val longWindow = sampleRate // 1 second window

        var shortSum = 0.0
        var longSum = 0.0

        for (i in buffer.indices) {
            longSum += Math.abs(buffer[i].toDouble())
            if (i < shortWindow) {
                shortSum += Math.abs(buffer[i].toDouble())
            }
        }

        val sta = shortSum / shortWindow
        val lta = longSum / longWindow

        return sta / lta
    }
}
