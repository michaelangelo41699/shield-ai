package com.mcfly.shield_ai.service

import android.app.Notification
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.util.Log
import com.mcfly.shield_ai.network.LogEntry
import com.mcfly.shield_ai.sync.GuardianLogger
import com.mcfly.shield_ai.utils.NotificationUtils // make sure this exists

class ScreenLoggingService : Service() {

    private lateinit var screenReceiver: BroadcastReceiver

    override fun onCreate() {
        super.onCreate()

        // Set up the receiver
        screenReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val action = intent?.action ?: return

                val description = when (action) {
                    Intent.ACTION_SCREEN_ON -> "Screen turned ON"
                    Intent.ACTION_SCREEN_OFF -> "Screen turned OFF"
                    Intent.ACTION_USER_PRESENT -> "Device UNLOCKED"
                    else -> return
                }

                val log = LogEntry.create(
                    emotionalState = "Neutral",
                    packageName = "system.screen",
                    additionalContext = mapOf("event" to description)
                )

                GuardianLogger.getInstance().store(log)
                Log.d("ScreenLoggingService", "📲 Logged screen event: $description")
            }
        }

        // Register screen intent filters
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(Intent.ACTION_SCREEN_OFF)
            addAction(Intent.ACTION_USER_PRESENT)
        }

        registerReceiver(screenReceiver, filter)
        startForeground(NOTIFICATION_ID, NotificationUtils.createServiceNotification(this))
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(screenReceiver)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("ScreenLoggingService", "🟢 Service started")
        return START_STICKY
    }

    companion object {
        const val NOTIFICATION_ID = 2025
    }
}
