package com.mcfly.shield_ai.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.mcfly.shield_ai.R

object NotificationUtils {

    private const val CHANNEL_ID = "shieldai_foreground"
    private const val CHANNEL_NAME = "ShieldAI Background Monitoring"
    private const val CHANNEL_DESC = "Used for screen and device activity tracking"

    fun createServiceNotification(context: Context): Notification {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW // No heads-up, no sound
            ).apply {
                description = CHANNEL_DESC
                setShowBadge(false)
            }

            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }

        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("ShieldAI Active")
            .setContentText("Monitoring screen events for your wellbeing.")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            // replace with your own
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }
}
