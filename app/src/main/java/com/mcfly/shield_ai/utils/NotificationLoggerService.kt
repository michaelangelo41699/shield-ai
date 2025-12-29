package com.mcfly.shield_ai.utils

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log

class NotificationLoggerService : NotificationListenerService() {
    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val packageName = sbn.packageName
        val extras = sbn.notification.extras
        val title = extras.getString("android.title") ?: "No Title"
        val text = extras.getCharSequence("android.text")?.toString() ?: "No Text"
        val timestamp = System.currentTimeMillis()

        val category = classifyNotification(title, text)
        val psychEffect = classifyPsychEffect(title, text) // 👈 We'll write this next

        val logEntry = "$timestamp|$packageName|$title|$text|$category|$psychEffect"
        // 🧠 Add category to the log

        val prefs = getSharedPreferences("ShieldAI", MODE_PRIVATE)
        val currentLogs = prefs.getStringSet("notification_log", mutableSetOf())?.toMutableSet() ?: mutableSetOf()
        currentLogs.add(logEntry)
        prefs.edit().putStringSet("notification_log", currentLogs).apply()

        Log.d("ShieldAI", "📥 Notification logged: $logEntry")
    }

    private fun classifyNotification(title: String, text: String): String {
        val content = "$title $text".lowercase()

        return when {
            // 🌍 SOCIAL
            Regex("(?i)(instagram|reel|tiktok|like|follower|facebook|twitter|x\\.com|snapchat|story|dm|direct message|viral|trending|hashtag)").containsMatchIn(content) -> "Social"

// 🏛️ POLITICAL
            Regex("(?i)(election|debate|policy|government|senator|congress|vote|democrat|republican|bill|law|protest|rally|campaign)").containsMatchIn(content) -> "Political"

// 🛍️ PROMOTIONAL
            Regex("(?i)(sale|deal|discount|buy now|ad|limited time|offer|promo|coupon|flash sale|bundle|only \\$|clearance|sponsored)").containsMatchIn(content) -> "Promotional"

// 🍔 FOOD / CONSUMPTION
            Regex("(?i)(ubereats|doordash|grubhub|your order|delivery|restaurant|menu|food|dinner|lunch|breakfast|recipe|takeout|fast food)").containsMatchIn(content) -> "Food/Consumption"

// 💼 PRODUCTIVITY
            Regex("(?i)(calendar|task|meeting|event reminder|deadline|schedule|zoom|conference|work|office|presentation|report|due|reminder)").containsMatchIn(content) -> "Work/Productivity"

// 🧠 EMOTIONAL BAIT
            Regex("(?i)(you won't believe|shocking|secret exposed|gone wrong|they hid this|what happens next|will surprise you|mind-blowing|exposed|the truth about|they don't want you to know|caught on camera|once you see this)").containsMatchIn(content) -> "Emotional Bait"

// ✨ INSPIRATION
            Regex("(?i)(poem|quote|you are enough|breathe|reflection|peace|affirmation|soul|spirit|beautiful|art|healing|mindfulness|you're not alone|gratitude|meditation|self-care|positive vibes|kindness|motivation|wisdom)").containsMatchIn(content) -> "Inspiration"

// 🎮 ENTERTAINMENT
            Regex("(?i)(movie|netflix|disney\\+|stream|trailer|game|playstation|xbox|nintendo|episode|season|premiere|concert|music)").containsMatchIn(content) -> "Entertainment"

// 🏥 HEALTH
            Regex("(?i)(doctor|appointment|pharmacy|prescription|hospital|clinic|medical|health|fitness|workout|yoga|diet|weight|exercise)").containsMatchIn(content) -> "Health"

// 💰 FINANCE
            Regex("(?i)(bank|account|payment|invoice|bill|credit|debit|loan|mortgage|invest|stock|crypto|bitcoin|finance)").containsMatchIn(content) -> "Finance"

// 🚗 TRAVEL
            Regex("(?i)(flight|airport|hotel|reservation|trip|vacation|travel|destination|itinerary|boarding pass|airline|luggage|road trip|cruise)").containsMatchIn(content) -> "Travel"

            else -> "Uncategorized"
        }
    }
        private fun classifyPsychEffect(title: String, text: String): String {
            val content = "$title $text".lowercase()

            return when {
                // Dopamine-driven patterns
                content.contains("like") || content.contains("follower") || content.contains("live now") ||
                        content.contains("just posted") || content.contains("reaction") -> "Dopamine Spike"

                // Emotional bait
                content.contains("you won’t believe") || content.contains("shocking") || content.contains(
                    "outrage"
                ) -> "Emotional Bait"

                // FOMO
                content.contains("happening now") || content.contains("only today") || content.contains(
                    "limited time"
                ) -> "FOMO"

                // Inspiration
                content.contains("quote") || content.contains("poem") || content.contains("beautiful") ||
                        content.contains("you are enough") || content.contains("breathe") -> "Inspiration"

                else -> "Neutral"


    }
        }
    }