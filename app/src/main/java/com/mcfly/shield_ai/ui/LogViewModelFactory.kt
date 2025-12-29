package com.mcfly.shield_ai.ui


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mcfly.shield_ai.data.local.LogRepository

class LogViewModelFactory(private val repository: LogRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LogViewModel::class.java)) {
            return LogViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
