package com.mcfly.shield_ai.sync

import android.content.Context
import android.util.Log
import com.mcfly.shield_ai.data.local.entities.toLogEntry
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.mcfly.shield_ai.network.AnalyzeRequest
import com.mcfly.shield_ai.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SyncWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            GuardianLogger.initialize(applicationContext)
            val pending = GuardianLogger.getInstance().getPendingLogs()

            if (pending.isNotEmpty()) {
                val request = AnalyzeRequest(pending.map { it.toLogEntry() })
                val response = RetrofitClient.instance.analyzeLogs(request)

                return@withContext if (response.isSuccessful) {
                    GuardianLogger.getInstance().markAsSynced(pending)
                    Log.d("SyncWorker", "Synced ${pending.size} logs.")
                    Result.success()
                } else {
                    Log.e("SyncWorker", "Backend failed: ${response.code()}")
                    Result.retry()
                }
            } else {
                Log.d("SyncWorker", "No pending logs.")
                return@withContext Result.success()
            }

        } catch (e: Exception) {
            Log.e("SyncWorker", "Exception during sync", e)
            Result.retry()
        }
    }
}
