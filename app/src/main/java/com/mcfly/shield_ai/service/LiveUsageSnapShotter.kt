package com.mcfly.shield_ai.service

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.util.Log
import com.mcfly.shield_ai.network.LogEntry
import java.util.concurrent.TimeUnit

object LiveUsageSnapshotter {

    fun collect(context: Context, rangeDays: Int = 1): List<LogEntry> {
        val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val now = System.currentTimeMillis()
        val start = now - TimeUnit.DAYS.toMillis(rangeDays.toLong())

        val usageEvents = usageStatsManager.queryEvents(start, now)
        val event = UsageEvents.Event()
        val logs = mutableListOf<LogEntry>()

        while (usageEvents.hasNextEvent()) {
            usageEvents.getNextEvent(event)
            if (event.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                val appName = try {
                    context.packageManager.getApplicationLabel(
                        context.packageManager.getApplicationInfo(event.packageName, 0)
                    ).toString()
                } catch (e: Exception) {
                    event.packageName ?: "Unknown"
                }

                logs.add(
                    LogEntry.create(
                        emotionalState = "Neutral",
                        packageName = event.packageName,
                        additionalContext = mapOf(
                            "source" to "LiveUsageSnapshotter",
                            "appName" to appName,
                            "class" to (event.className ?: "")
                        )
                    )
                )
            }
        }

        Log.d("GuardianAI", "LiveUsageSnapshotter collected ${logs.size} events")
        return logs
    }
}
