package com.mcfly.shield_ai.service

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.util.Log
import com.mcfly.shield_ai.network.LogEntry
import com.mcfly.shield_ai.sync.GuardianLogger
import com.mcfly.shield_ai.utils.NotificationUtils // You’ll need to create this

class ScreenEventReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return

        val eventDescription = when (action) {
            Intent.ACTION_SCREEN_ON -> "Screen turned ON"
            Intent.ACTION_SCREEN_OFF -> "Screen turned OFF"
            Intent.ACTION_USER_PRESENT -> "Device UNLOCKED"
            else -> return
        }

        val log = LogEntry.create(
            emotionalState = "Neutral",
            packageName = "system.screen",
            additionalContext = mapOf("event" to eventDescription)
        )

        GuardianLogger.getInstance().store(log)
        Log.d("ScreenEventReceiver", "✅ Logged: $eventDescription")
    }
}

class MonitoringService : Service() {

    private val screenReceiver = ScreenEventReceiver()

    override fun onCreate() {
        super.onCreate()
        registerReceivers()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("MonitoringService", "🟢 Service started")
        startForeground(NOTIFICATION_ID, NotificationUtils.createServiceNotification(this))
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(screenReceiver)
        Log.d("MonitoringService", "🛑 Service stopped and receiver unregistered")
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun registerReceivers() {
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(Intent.ACTION_SCREEN_OFF)
            addAction(Intent.ACTION_USER_PRESENT)
        }
        registerReceiver(screenReceiver, filter)
    }

    companion object {
        const val NOTIFICATION_ID = 1001
    }
}
