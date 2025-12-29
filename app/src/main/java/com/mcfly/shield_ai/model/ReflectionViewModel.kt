package com.mcfly.shield_ai.viewmodel

import androidx.lifecycle.*
import com.mcfly.shield_ai.data.local.entities.ReflectionEntity
import com.mcfly.shield_ai.data.repository.ReflectionRepository
import kotlinx.coroutines.launch

class ReflectionViewModel(
    private val repository: ReflectionRepository
) : ViewModel() {

    // Live list of all reflections
    val reflections: LiveData<List<ReflectionEntity>> = repository.allReflections

    // Insert a new reflection
    fun insertReflection(reflection: ReflectionEntity) {
        viewModelScope.launch {
            repository.insertReflection(reflection)
        }
    }

    // Delete a specific reflection (future use)
    fun deleteReflection(reflection: ReflectionEntity) {
        viewModelScope.launch {
            repository.deleteReflection(reflection)
        }
    }

    // Clear all reflections (future use)
    fun clearAllReflections() {
        viewModelScope.launch {
            repository.clearAll()
        }
    }
}

class ReflectionViewModelFactory(
    private val repository: ReflectionRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReflectionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ReflectionViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
