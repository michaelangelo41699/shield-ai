package com.mcfly.shield_ai.sync

import android.content.Context
import android.util.Log
import com.mcfly.shield_ai.coach.CoachPopupManager
import com.mcfly.shield_ai.data.goal.GoalProfileRepository
import com.mcfly.shield_ai.data.local.LogDatabase
import com.mcfly.shield_ai.data.local.dao.GuardianInsightDao
import com.mcfly.shield_ai.data.local.dao.LogDao
import com.mcfly.shield_ai.data.local.entities.LogEntryEntity
import com.mcfly.shield_ai.data.local.entities.toEntity
import com.mcfly.shield_ai.data.local.entities.toLogEntry
import com.mcfly.shield_ai.logic.AppClassification
import com.mcfly.shield_ai.model.influence.*
import com.mcfly.shield_ai.network.AnalyzeRequest
import com.mcfly.shield_ai.network.GuardianInsight
import com.mcfly.shield_ai.network.RetrofitClient
import com.mcfly.shield_ai.util.currentActivityOrNull
import com.mcfly.shield_ai.voice.GuardianVoiceClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit

class GuardianLogger private constructor(
    private val logDao: LogDao,
    private val insightDao: GuardianInsightDao
) {

    private var cachedInsights: List<GuardianInsight> = emptyList()

    companion object {
        @Volatile
        private var INSTANCE: GuardianLogger? = null

        fun initialize(context: Context): GuardianLogger {
            return INSTANCE ?: synchronized(this) {
                val db = LogDatabase.getInstance(context)
                INSTANCE ?: GuardianLogger(
                    db.logDao(),
                    db.guardianInsightDao()
                ).also { INSTANCE = it }
            }
        }

        fun getInstance(): GuardianLogger {
            return INSTANCE ?: throw IllegalStateException("GuardianLogger not initialized")
        }
    }

    fun store(entry: LogEntry) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val enriched = enrichWithClassification(entry)
                logDao.insert(enriched.toEntity())
                Log.d("GuardianLogger", "Stored single log entry")
            } catch (e: Exception) {
                Log.e("GuardianLogger", "Failed to store single log", e)
            }
        }
    }
    fun trackInsightFeedback(insightId: String, sentiment: String) {
        Log.d("GuardianFeedback", "Feedback [$sentiment] for insight $insightId")
        // Optionally persist to DB or send to server
    }

    fun store(entries: List<LogEntry>) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val enrichedList = entries.map { enrichWithClassification(it) }
                logDao.insertAll(enrichedList.map { it.toEntity() })
                Log.d("GuardianLogger", "Stored ${enrichedList.size} entries in Room")
            } catch (e: Exception) {
                Log.e("GuardianLogger", "Failed to store multiple logs", e)
            }
        }
    }

    private fun enrichWithClassification(entry: LogEntry): LogEntry {
        val pkg = entry.metadata.context["package"]
        val classification = pkg?.let { AppClassification.getAppInfo(it) }

        return if (classification != null) {
            val newContext = entry.metadata.context.toMutableMap().apply {
                put("classification_label", classification.label)
                put("classification_category", classification.category)
                put("classification_type", classification.type.name.lowercase())
                put("classification_risk", classification.riskLevel)
            }
            entry.copy(metadata = entry.metadata.copy(context = newContext))
        } else {
            entry
        }
    }

    suspend fun getPendingLogs(limit: Int = 100): List<LogEntryEntity> {
        return withContext(Dispatchers.IO) {
            logDao.getPendingLogs(limit)
        }
    }

    suspend fun markAsSynced(entries: List<LogEntryEntity>) {
        withContext(Dispatchers.IO) {
            val ids = entries.map { it.id }
            logDao.markAsSynced(ids)
            Log.d("GuardianLogger", "Marked ${ids.size} logs as synced")
        }
    }

    suspend fun syncWithBackend() {
        withContext(Dispatchers.IO) {
            try {
                val pending = getPendingLogs()
                if (pending.isEmpty()) {
                    Log.d("GuardianLogger", "No pending logs to sync")
                    return@withContext
                }

                val logs = pending.map { it.toLogEntry() }
                val request = AnalyzeRequest(logs)
                val response = RetrofitClient.instance.analyzeLogs(request)

                if (response.isSuccessful) {
                    val rawInsights = response.body()?.results ?: emptyList()
                    cachedInsights = rawInsights.mapNotNull { insight ->
                        try {
                            insight
                        } catch (e: Exception) {
                            Log.w("GuardianLogger", "Skipped invalid insight: ${e.message}")
                            null
                        }
                    }

                    val entities = cachedInsights.map { it.toEntity() }
                    insightDao.insertAll(entities)
                    Log.d("GuardianLogger", "Synced ${logs.size} logs; Saved ${entities.size} insights")

                    // 🚨 Detect the last important insight (for pop-up)
                    val lastInsight = cachedInsights.lastOrNull {
                        it.influenceType != InfluenceType.NONE &&
                                it.trigger != null &&
                                it.interruptionLevel != InterruptionLevel.LOW
                    }

                    if (lastInsight != null) {
                        val goalProfile = GoalProfileRepository.getInstance().getCurrentProfile()

                        val suggestion = try {
                            GuardianVoiceClient.getSuggestion(lastInsight, goalProfile)
                        } catch (e: Exception) {
                            Log.e("GuardianLogger", "GuardianVoice fallback: ${e.message}")
                            "Take a moment to reflect on whether this aligns with your deeper goals."
                        }

                        withContext(Dispatchers.Main) {
                            CoachPopupManager.showPopup(
                                currentActivityOrNull(),
                                insightText = lastInsight.label,
                                suggestion = suggestion
                            )
                        }
                    }

                    markAsSynced(pending)

                } else {
                    Log.e("GuardianLogger", "Backend error: ${response.errorBody()?.string()}")
                }

            } catch (e: Exception) {
                Log.e("GuardianLogger", "Sync failed: ${e.message}", e)
            }
        }
    }

    suspend fun getRecentInsights(): List<GuardianInsight> {
        return withContext(Dispatchers.IO) {
            val stored = insightDao.getRecent(100)
            cachedInsights = stored.map { it.toModel() }
            cachedInsights
        }
    }

    suspend fun getInsightsByDay(days: Int = 3): Map<String, List<GuardianInsight>> {
        return withContext(Dispatchers.IO) {
            val now = Instant.now()
            val cutoff = now.minus(days.toLong(), ChronoUnit.DAYS).toEpochMilli()
            val insights = insightDao.getSinceTimestamp(cutoff).map { it.toModel() }

            insights.groupBy {
                Instant.ofEpochMilli(it.timestamp.toEpochMilli())
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                    .toString()
            }
        }
    }

    fun createLocalInsight(logEntry: LogEntry): GuardianInsight {
        return GuardianInsight(
            label = "unknown",
            confidence = 0.0f,
            sourceText = logEntry.text,
            patternName = logEntry.metadata.context["source"] ?: "unspecified",
            category = "unspecified",
            metadata = logEntry.metadata.context,
            timestamp = Instant.now(),
            influenceType = InfluenceType.UNKNOWN,
            interruptionLevel = InterruptionLevel.LOW,
            deliveryMechanism = DeliveryMechanism.UNSPECIFIED,
            influenceCategory = InfluenceCategory.UNKNOWN,
            influenceSubCategory = InfluenceSubCategory.UNSPECIFIED,
            monetizationType = MonetizationType.NONE,
            logContext = LogContext.FRONTEND_MOBILE,
            userSegment = UserSegment.FREE_TIER,
            outcome = ActionOutcome.IGNORED,
            trigger = EventTrigger.USER_ACTION
        )
    }
}
