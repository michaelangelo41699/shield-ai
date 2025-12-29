package com.mcfly.shield_ai

import android.app.Application
import android.content.Intent
import android.os.Build
import android.provider.Settings
import com.mcfly.shield_ai.data.local.LogDatabase
import com.mcfly.shield_ai.sync.GuardianLogger
import com.mcfly.shield_ai.sync.SyncManager
import com.mcfly.shield_ai.service.GuardianNotificationListener
import com.mcfly.shield_ai.service.MonitoringService
import java.util.concurrent.TimeUnit

class GuardianApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // 1. Initialize Room and GuardianLogger
        initializeDatabase()
        initializeLogger()

        // 2. Start monitoring services
        //startMonitoringServices() <--- has been moved to mainActivity.kt

        // 3. Schedule background log syncs
        scheduleBackgroundTasks()
    }

    private fun initializeDatabase() {
        LogDatabase.getInstance(this) // Preload DB
    }

    private fun initializeLogger() {
        GuardianLogger.initialize(this)
    }

    private fun startMonitoringServices() {
        val serviceIntent = Intent(this, MonitoringService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }

        if (isNotificationAccessEnabled()) {
            startService(Intent(this, GuardianNotificationListener::class.java))
        }
    }

    private fun scheduleBackgroundTasks() {
        val syncManager = SyncManager(context = this)

        // Periodic sync
        syncManager.schedulePeriodicSync()

        // Optional immediate sync in debug builds
        if (BuildConfig.DEBUG) {
            syncManager.triggerImmediateSync()
        }
    }

    private fun isNotificationAccessEnabled(): Boolean {
        val enabledListeners = Settings.Secure.getString(
            contentResolver,
            "enabled_notification_listeners"
        )
        return enabledListeners?.contains(packageName) == true
    }
}
