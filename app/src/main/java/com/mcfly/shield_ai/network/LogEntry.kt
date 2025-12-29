package com.mcfly.shield_ai.network

import android.os.Build
import androidx.annotation.Keep
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.mcfly.shield_ai.data.local.entities.LogEntryEntity
import com.mcfly.shield_ai.data.local.entities.LogEntryEntity.EventType
import com.mcfly.shield_ai.data.local.entities.LogEntryEntity.SyncStatus

@Keep
data class LogEntry(
    @SerializedName("text")
    val text: String,

    @SerializedName("metadata")
    val metadata: Metadata,

    @Transient
    val _debugInfo: DebugInfo? = null,

    @SerializedName("syncStatus")
    val syncStatus: SyncStatus = SyncStatus.PENDING
) {
    @Keep
    data class Metadata(
        @SerializedName("app")
        val app: String,

        @SerializedName("timestamp")
        val timestamp: Long = System.currentTimeMillis(),

        @SerializedName("context")
        val context: Map<String, String> = emptyMap(),

        @SerializedName("deviceId")
        val deviceId: String = Build.ID
    )

    @Keep
    data class DebugInfo(
        val legacyContent: String? = null,
        val logVersion: String = "2.1",
        val source: String? = null,
        val osVersion: Int = Build.VERSION.SDK_INT,
        val stackTrace: String? = null
    )

    @Keep
    enum class SyncStatus {
        @SerializedName("pending") PENDING,
        @SerializedName("synced") SYNCED,
        @SerializedName("failed") FAILED
    }

    companion object {
        private const val MAX_TEXT_LENGTH = 500
        private const val MIN_TEXT_LENGTH = 10

        @JvmStatic
        fun create(
            emotionalState: String,
            packageName: String,
            additionalContext: Map<String, String> = emptyMap()
        ): LogEntry {
            require(emotionalState.isNotBlank()) { "Emotional state cannot be blank" }

            val normalizedText = buildString {
                append("Felt ${emotionalState.trim().lowercase()}")
                if (packageName.isNotEmpty()) {
                    append(" after using ${packageName.substringAfterLast('.')}")
                }
                additionalContext["duration"]?.let { duration ->
                    append(" for $duration")
                }
            }.take(MAX_TEXT_LENGTH)
                .takeIf { it.length >= MIN_TEXT_LENGTH }
                ?: throw IllegalArgumentException("Text too short after normalization")

            return LogEntry(
                text = normalizedText,
                metadata = Metadata(
                    app = packageName,
                    context = additionalContext
                ),
                _debugInfo = DebugInfo(legacyContent = emotionalState)
            )
        }

        @JvmStatic
        fun createSystemLog(
            source: String,
            eventText: String,
            packageName: String = "system",
            context: Map<String, String> = emptyMap()
        ): LogEntry {
            return LogEntry(
                text = eventText,
                metadata = Metadata(
                    app = packageName,
                    context = context + mapOf("source" to source)
                ),
                _debugInfo = DebugInfo(
                    source = source,
                    logVersion = "3.0"
                )
            )
        }

        @JvmStatic
        fun fromLegacy(content: String, app: String): LogEntry {
            return create(
                emotionalState = content,
                packageName = app
            )
        }
    }
}

/**
 * Extension function to convert a LogEntry into a Room-storable LogEntryEntity
 */
fun LogEntry.toEntity(): LogEntryEntity {
    return LogEntryEntity(
        text = this.text,
        appPackage = this.metadata.app,
        timestamp = this.metadata.timestamp,
        syncStatus = LogEntryEntity.SyncStatus.valueOf(this.syncStatus.name),

        eventType = when {
            metadata.context["event"]?.contains("screen", ignoreCase = true) == true -> EventType.SCREEN_EVENT
            metadata.context["source"]?.contains("Usage", ignoreCase = true) == true -> EventType.APP_USAGE
            metadata.context["source"]?.contains("Notification", ignoreCase = true) == true -> EventType.NOTIFICATION
            else -> EventType.DEVICE_EVENT
        },
        contextData = Gson().toJson(this.metadata.context),
        metadata = this.metadata.context
    )
}
