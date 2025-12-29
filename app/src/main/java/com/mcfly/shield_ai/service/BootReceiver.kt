package com.mcfly.shield_ai.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.WorkManager

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            try {
                val monitor = AppUsageMonitor(context, WorkManager.getInstance(context))
                monitor.schedulePeriodicCollection()
                Log.d("BootReceiver", "✅ AppUsageMonitor rescheduled after boot")
            } catch (e: Exception) {
                Log.e("BootReceiver", "❌ Failed to reschedule monitor on boot", e)
            }
        }
    }
}
