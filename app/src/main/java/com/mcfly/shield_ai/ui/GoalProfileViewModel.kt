package com.mcfly.shield_ai.ui

import android.app.Application
import androidx.lifecycle.*
import com.mcfly.shield_ai.data.local.entities.GoalProfileEntity
import com.mcfly.shield_ai.data.local.entities.GuardianInsightEntity
import com.mcfly.shield_ai.data.local.repository.GoalProfileRepository
import com.mcfly.shield_ai.logic.AlignmentStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// ✅ Alignment summary data model
data class AlignmentSummary(
    val status: AlignmentStatus,
    val topDriftedCategory: String?, // e.g., "confidence"
    val timeRange: String            // e.g., "Last 3 Days"
)

class GoalProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = GoalProfileRepository.getInstance(application)
    private val userId = "default_user" // Replace if dynamic IDs later

    val goalProfile: LiveData<GoalProfileEntity?> = repository.getProfileLive(userId)

    // ✅ Alignment status (simple enum state)
    private val _goalAlignmentStatus = MutableStateFlow(AlignmentStatus.UNKNOWN)
    val goalAlignmentStatus: StateFlow<AlignmentStatus> = _goalAlignmentStatus

    fun updateAlignmentStatus(status: AlignmentStatus) {
        _goalAlignmentStatus.value = status
    }

    // ✅ Alignment summary (enriched)
    private val _alignmentSummary = MutableLiveData<AlignmentSummary>()
    val alignmentSummary: LiveData<AlignmentSummary> = _alignmentSummary

    fun computeAlignmentSummary(insights: List<GuardianInsightEntity>, timeRange: String) {
        if (insights.isEmpty()) {
            _alignmentSummary.value = AlignmentSummary(
                status = AlignmentStatus.UNKNOWN,
                topDriftedCategory = null,
                timeRange = timeRange
            )
            return
        }

        val driftingCategories = insights
            .filter { it.label.equals("drifted", ignoreCase = true) }
            .mapNotNull { it.patternName }

        val topCategory = driftingCategories
            .groupingBy { it }
            .eachCount()
            .maxByOrNull { it.value }
            ?.key

        val alignmentStatus = when {
            driftingCategories.isNotEmpty() -> AlignmentStatus.DRIFTING
            else -> AlignmentStatus.ALIGNED
        }

        _alignmentSummary.value = AlignmentSummary(
            status = alignmentStatus,
            topDriftedCategory = topCategory,
            timeRange = timeRange
        )
    }

    fun saveProfile(profile: GoalProfileEntity) {
        viewModelScope.launch {
            repository.saveProfile(profile)
        }
    }

    fun deleteProfile() {
        viewModelScope.launch {
            repository.deleteProfile(userId)
        }
    }

    suspend fun getProfileOnce(): GoalProfileEntity? {
        return repository.getProfile(userId)
    }
}
