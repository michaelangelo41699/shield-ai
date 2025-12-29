package com.mcfly.shield_ai.utils

import android.content.SharedPreferences
import java.text.SimpleDateFormat
import java.util.*

object LogUtils {

    fun groupNotificationEffectsByDate(prefs: SharedPreferences): Map<String, Map<String, Int>> {
        val rawLogs = prefs.getStringSet("notification_log", emptySet()) ?: return emptyMap()
        val result = mutableMapOf<String, MutableMap<String, Int>>()

        for (entry in rawLogs) {
            val parts = entry.split("|")
            if (parts.size >= 6) {
                val timestamp = parts[0].toLongOrNull() ?: continue
                val effect = parts[5]
                val date = formatDate(timestamp)
                val dailyMap = result.getOrPut(date) { mutableMapOf() }
                dailyMap[effect] = dailyMap.getOrDefault(effect, 0) + 1
            }
        }

        return result
    }

    fun formatDate(timestamp: Long): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return formatter.format(Date(timestamp))
    }
}
