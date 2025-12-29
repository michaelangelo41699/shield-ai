package com.mcfly.shield_ai.sync

import android.content.Context
import androidx.work.*

import java.util.concurrent.TimeUnit

class SyncManager(private val context: Context) {

    private val workManager = WorkManager.getInstance(context)

    fun schedulePeriodicSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()

        val request = PeriodicWorkRequestBuilder<SyncWorker>(
            4, TimeUnit.HOURS
        ).setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                30, TimeUnit.MINUTES
            ).build()

        workManager.enqueueUniquePeriodicWork(
            "LogSync",
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }

    fun triggerImmediateSync() {
        val request = OneTimeWorkRequestBuilder<SyncWorker>().build()
        workManager.enqueue(request)
    }


    }
