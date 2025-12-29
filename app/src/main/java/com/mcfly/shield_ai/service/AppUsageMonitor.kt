package com.mcfly.shield_ai.service

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.util.Log
import androidx.work.*
import com.mcfly.shield_ai.sync.GuardianLogger
import com.mcfly.shield_ai.network.LogEntry
import java.util.concurrent.TimeUnit

class AppUsageMonitor(
    private val context: Context,
    private val workManager: WorkManager
) {
    private val usageStatsManager by lazy {
        context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
    }

    fun schedulePeriodicCollection() {
        val constraints = Constraints.Builder()
            .setRequiresCharging(false)
            .setRequiresBatteryNotLow(true)
            .build()

        val request = PeriodicWorkRequestBuilder<UsageStatsWorker>(
            15, TimeUnit.MINUTES
        ).setConstraints(constraints)
            .build()

        workManager.enqueueUniquePeriodicWork(
            "UsageStatsCollection",
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }

    inner class UsageStatsWorker(
        context: Context,
        workerParams: WorkerParameters
    ) : Worker(context, workerParams) {
        override fun doWork(): Result {
            val logs = collectUsageLogs()
            Log.d("GuardianAI", "Running background usage check at ${System.currentTimeMillis()}")
            GuardianLogger.getInstance().store(logs)
            return Result.success()
        }
    }

    private fun collectUsageLogs(): List<LogEntry> {
        val endTime = System.currentTimeMillis()
        val startTime = endTime - TimeUnit.HOURS.toMillis(1)

        return try {
            val usageEvents = usageStatsManager.queryEvents(startTime, endTime)
            val event = UsageEvents.Event()
            val logs = mutableListOf<LogEntry>()

            while (usageEvents.hasNextEvent()) {
                usageEvents.getNextEvent(event)
                when (event.eventType) {
                    UsageEvents.Event.MOVE_TO_FOREGROUND -> {
                        val appName = try {
                            context.packageManager.getApplicationLabel(
                                context.packageManager.getApplicationInfo(event.packageName, 0)
                            ).toString()
                        } catch (e: Exception) { event.packageName }

                        logs.add(
                            LogEntry(
                                text = "App foregrounded: $appName",
                                metadata = LogEntry.Metadata(
                                    app = appName,
                                    context = mapOf(
                                        "package" to event.packageName,
                                        "class" to (event.className ?: "")
                                    )
                                ),
                                _debugInfo = LogEntry.DebugInfo(source = "UsageStats")
                            )
                        )
                    }
                    UsageEvents.Event.USER_INTERACTION -> {
                        // Optional: Track this too
                    }
                }
            }
            logs
        } catch (e: SecurityException) {
            Log.e("GuardianAI", "Usage access permission not granted", e)
            emptyList()
        }
    }
}
