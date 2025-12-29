package com.mcfly.shield_ai.ui

import androidx.lifecycle.*
import com.mcfly.shield_ai.data.local.LogRepository
import com.mcfly.shield_ai.data.local.dto.AppUsageStat
import com.mcfly.shield_ai.data.local.dto.HourlyUsageStat
import kotlinx.coroutines.launch

class LogViewModel(private val repository: LogRepository) : ViewModel() {

    private val _topApps = MutableLiveData<List<AppUsageStat>>()
    val topApps: LiveData<List<AppUsageStat>> get() = _topApps

    private val _hourlyUsage = MutableLiveData<List<HourlyUsageStat>>()
    val hourlyUsage: LiveData<List<HourlyUsageStat>> get() = _hourlyUsage

    fun loadTopApps(start: Long, end: Long) {
        viewModelScope.launch {
            _topApps.value = repository.getTopAppsByUsage(start, end)
        }
    }
    fun loadInsights(start: Long, end: Long) {
        loadTopApps(start, end)
        loadHourlyUsage(start, end)
    }

    fun loadHourlyUsage(start: Long, end: Long) {
        viewModelScope.launch {
            _hourlyUsage.value = repository.getUsageByHour(start, end)
        }
    }
}
