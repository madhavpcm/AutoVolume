package com.example.library

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class NoiseLevelViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NoiseLevelViewModel::class.java)) {
            return NoiseLevelViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
