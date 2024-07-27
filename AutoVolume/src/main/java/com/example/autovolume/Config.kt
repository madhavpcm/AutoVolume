package com.example.autovolume

import android.content.Context
import android.content.SharedPreferences

class Config(context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREF_NAME = "sound_detection_prefs"
        private const val KEY_THRESHOLD = "threshold"
        private const val KEY_REDUCE_ONLY = "reduce_only"
    }

    var threshold: Double
        get() = sharedPreferences.getFloat(KEY_THRESHOLD, 3.0f).toDouble()
        set(value) = sharedPreferences.edit().putFloat(KEY_THRESHOLD, value.toFloat()).apply()

    var reduceOnly: Boolean
        get() = sharedPreferences.getBoolean(KEY_REDUCE_ONLY, false)
        set(value) = sharedPreferences.edit().putBoolean(KEY_REDUCE_ONLY, value).apply()

    fun resetDefaults() {
        sharedPreferences.edit().clear().apply()
    }
}
