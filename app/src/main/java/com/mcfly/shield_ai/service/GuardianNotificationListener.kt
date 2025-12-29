package com.mcfly.shield_ai.service

import android.app.Notification
import android.os.Build
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.mcfly.shield_ai.network.LogEntry
import com.mcfly.shield_ai.sync.GuardianLogger

class GuardianNotificationListener : NotificationListenerService() {

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        try {
            val pm = packageManager
            val appInfo = pm.getApplicationInfo(sbn.packageName, 0)
            val appName = pm.getApplicationLabel(appInfo).toString()

            val extras = sbn.notification.extras

            // ✅ Safe extraction using .toString()
            val rawTitle = extras["android.title"]
            val rawText = extras["android.text"]
            val title = rawTitle?.toString() ?: "No Title"
            val text = rawText?.toString() ?: "No Text"

            val category = classifyNotification(title, text)
            val psychEffect = classifyPsychEffect(title, text)

            val log = LogEntry(
                text = "Notification: $title",
                metadata = LogEntry.Metadata(
                    app = appName,
                    context = buildMap<String, String> {
                        put("package", sbn.packageName)
                        put("title", title)
                        put("text", text)
                        put("category", category)
                        put("psychEffect", psychEffect)
                        put("when", sbn.notification.`when`.toString())
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            sbn.notification.group?.let { put("group", it) }
                        }
                    }
                ),
                _debugInfo = LogEntry.DebugInfo(source = "NotificationListener")
            )

            GuardianLogger.getInstance().store(log)

        } catch (e: Exception) {
            Log.e("GuardianAI", "Notification processing error", e)
        }
    }

    private fun classifyNotification(title: String, text: String): String {
        val content = "$title $text".lowercase()

        return when {
            Regex("(?i)(instagram|tiktok|follower|reel|snapchat|viral|story)").containsMatchIn(content) -> "Social"
            Regex("(?i)(election|vote|congress|debate|law)").containsMatchIn(content) -> "Political"
            Regex("(?i)(sale|discount|deal|ad|offer|promo)").containsMatchIn(content) -> "Promotional"
            Regex("(?i)(ubereats|food|menu|order|restaurant)").containsMatchIn(content) -> "Food/Consumption"
            Regex("(?i)(calendar|task|meeting|work|reminder)").containsMatchIn(content) -> "Work/Productivity"
            Regex("(?i)(mind-blowing|exposed|truth|caught)").containsMatchIn(content) -> "Emotional Bait"
            Regex("(?i)(poem|quote|breathe|healing|affirmation)").containsMatchIn(content) -> "Inspiration"
            Regex("(?i)(movie|stream|game|season|music)").containsMatchIn(content) -> "Entertainment"
            Regex("(?i)(doctor|clinic|health|fitness|workout)").containsMatchIn(content) -> "Health"
            Regex("(?i)(bank|payment|crypto|finance|loan)").containsMatchIn(content) -> "Finance"
            Regex("(?i)(flight|hotel|trip|travel|vacation)").containsMatchIn(content) -> "Travel"
            else -> "Uncategorized"
        }
    }

    private fun classifyPsychEffect(title: String, text: String): String {
        val content = "$title $text".lowercase()

        return when {
            content.contains("like") || content.contains("follower") || content.contains("reaction") -> "Dopamine Spike"
            content.contains("you won’t believe") || content.contains("shocking") -> "Emotional Bait"
            content.contains("only today") || content.contains("happening now") -> "FOMO"
            content.contains("quote") || content.contains("beautiful") || content.contains("breathe") -> "Inspiration"
            else -> "Neutral"
        }
    }
}
