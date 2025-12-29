package com.mcfly.shield_ai.data.local.entities

import androidx.room.*
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.mcfly.shield_ai.model.influence.*
import com.mcfly.shield_ai.model.influence.InterruptionLevel
import com.mcfly.shield_ai.network.LogEntry
import com.mcfly.shield_ai.data.local.converters.TypeConverters

@Entity(
    tableName = "log_entries",
    indices = [
        Index(value = ["appPackage"]),
        Index(value = ["timestamp"]),
        Index(value = ["syncStatus"]),
        Index(value = ["eventType"])
    ]
)
data class LogEntryEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0L,

    @ColumnInfo(name = "text")
    val text: String,

    @ColumnInfo(name = "appPackage")
    val appPackage: String,

    @ColumnInfo(name = "timestamp")
    val timestamp: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "syncStatus")
    val syncStatus: SyncStatus = SyncStatus.PENDING,

    @ColumnInfo(name = "eventType")
    val eventType: EventType = EventType.NOTIFICATION,

    @ColumnInfo(name = "contextData")
    val contextData: String? = null,


    @ColumnInfo(name = "metadata")
    val metadata: Map<String, String> = emptyMap(),

    @ColumnInfo(name = "influenceType")
    val influenceType: InfluenceType = InfluenceType.UNKNOWN,

    @ColumnInfo(name = "interruptionLevel")
    val interruptionLevel: InterruptionLevel = InterruptionLevel.UNSPECIFIED,

    @ColumnInfo(name = "deliveryMechanism")
    val deliveryMechanism: DeliveryMechanism = DeliveryMechanism.UNSPECIFIED,

    @ColumnInfo(name = "influenceCategory")
    val influenceCategory: InfluenceCategory = InfluenceCategory.UNKNOWN,

    @ColumnInfo(name = "influenceSubCategory")
    val influenceSubCategory: InfluenceSubCategory = InfluenceSubCategory.UNSPECIFIED,

    @ColumnInfo(name = "monetizationType")
    val monetizationType: MonetizationType = MonetizationType.NONE,

    @ColumnInfo(name = "logContext")
    val logContext: LogContext = LogContext.FRONTEND_MOBILE,

    @ColumnInfo(name = "userSegment")
    val userSegment: UserSegment = UserSegment.FREE_TIER,

    @ColumnInfo(name = "outcome")
    val outcome: ActionOutcome = ActionOutcome.SUCCESS,

    @ColumnInfo(name = "trigger")
    val trigger: EventTrigger = EventTrigger.USER_ACTION
) {
    enum class SyncStatus {
        @SerializedName("pending") PENDING,
        @SerializedName("synced") SYNCED,
        @SerializedName("failed") FAILED
    }

    enum class EventType {
        @SerializedName("notification") NOTIFICATION,
        @SerializedName("app_usage") APP_USAGE,
        @SerializedName("screen_event") SCREEN_EVENT,
        @SerializedName("device_event") DEVICE_EVENT
    }

    companion object {
        private val gson = Gson()
        fun fromJson(json: String): LogEntryEntity? {
            return try {
                gson.fromJson(json, LogEntryEntity::class.java)
            } catch (e: Exception) {
                null
            }
        }
    }

    fun toJson(): String {
        return gson.toJson(this)
    }
}
