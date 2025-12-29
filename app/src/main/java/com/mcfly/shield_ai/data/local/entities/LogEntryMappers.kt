package com.mcfly.shield_ai.data.local.entities

import com.mcfly.shield_ai.data.local.entities.LogEntryEntity
import com.mcfly.shield_ai.network.LogEntry
import com.mcfly.shield_ai.network.LogEntry.Metadata
import com.mcfly.shield_ai.network.LogEntry.SyncStatus

fun LogEntryEntity.toLogEntry(): LogEntry {
    return LogEntry(
        text = this.text,
        metadata = Metadata(
            app = this.appPackage,
            timestamp = this.timestamp,
            context = this.metadata,
            deviceId = android.os.Build.ID
        ),
        syncStatus = this.syncStatus.toLogStatus()
    )
}

fun LogEntryEntity.SyncStatus.toLogStatus(): SyncStatus {
    return when (this) {
        LogEntryEntity.SyncStatus.PENDING -> SyncStatus.PENDING
        LogEntryEntity.SyncStatus.SYNCED -> SyncStatus.SYNCED
        LogEntryEntity.SyncStatus.FAILED -> SyncStatus.FAILED
    }
}
