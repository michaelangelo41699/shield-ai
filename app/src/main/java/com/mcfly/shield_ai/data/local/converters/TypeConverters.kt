package com.mcfly.shield_ai.data.local.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mcfly.shield_ai.model.influence.*
import java.time.Instant
import java.util.*

object TypeConverters {

    private val gson = Gson()

    @TypeConverter
    @JvmStatic
    fun fromMap(value: Map<String, String>?): String {
        return gson.toJson(value)
    }

    @TypeConverter
    @JvmStatic
    fun toMap(value: String): Map<String, String> {
        val mapType = object : TypeToken<Map<String, String>>() {}.type
        return gson.fromJson(value, mapType) ?: emptyMap()
    }

    @TypeConverter
    @JvmStatic
    fun fromInstant(value: Instant?): Long? {
        return value?.toEpochMilli()
    }

    @TypeConverter
    @JvmStatic
    fun toInstant(value: Long?): Instant? {
        return value?.let { Instant.ofEpochMilli(it) }
    }

    @TypeConverter
    @JvmStatic
    fun fromDeliveryMechanism(value: DeliveryMechanism?): String? = value?.name

    @TypeConverter
    @JvmStatic
    fun toDeliveryMechanism(value: String?): DeliveryMechanism? = value?.let { DeliveryMechanism.valueOf(it) }

    @TypeConverter
    @JvmStatic
    fun fromInfluenceCategory(value: InfluenceCategory?): String? = value?.name

    @TypeConverter
    @JvmStatic
    fun toInfluenceCategory(value: String?): InfluenceCategory? = value?.let { InfluenceCategory.valueOf(it) }

    @TypeConverter
    @JvmStatic
    fun fromInfluenceSubCategory(value: InfluenceSubCategory?): String? = value?.name

    @TypeConverter
    @JvmStatic
    fun toInfluenceSubCategory(value: String?): InfluenceSubCategory? = value?.let { InfluenceSubCategory.valueOf(it) }

    @TypeConverter
    @JvmStatic
    fun fromMonetizationType(value: MonetizationType?): String? = value?.name

    @TypeConverter
    @JvmStatic
    fun toMonetizationType(value: String?): MonetizationType? = value?.let { MonetizationType.valueOf(it) }

    @TypeConverter
    @JvmStatic
    fun fromAuditoryFeedbackType(value: AuditoryFeedbackType?): String? = value?.name

    @TypeConverter
    @JvmStatic
    fun toAuditoryFeedbackType(value: String?): AuditoryFeedbackType? = value?.let { AuditoryFeedbackType.valueOf(it) }

    @TypeConverter
    @JvmStatic
    fun fromHapticFeedbackType(value: HapticFeedbackType?): String? = value?.name

    @TypeConverter
    @JvmStatic
    fun toHapticFeedbackType(value: String?): HapticFeedbackType? = value?.let { HapticFeedbackType.valueOf(it) }

    @TypeConverter
    @JvmStatic
    fun fromLogContext(value: LogContext?): String? = value?.name

    @TypeConverter
    @JvmStatic
    fun toLogContext(value: String?): LogContext? = value?.let { LogContext.valueOf(it) }

    @TypeConverter
    @JvmStatic
    fun fromUserSegment(value: UserSegment?): String? = value?.name

    @TypeConverter
    @JvmStatic
    fun toUserSegment(value: String?): UserSegment? = value?.let { UserSegment.valueOf(it) }

    @TypeConverter
    @JvmStatic
    fun fromActionOutcome(value: ActionOutcome?): String? = value?.name

    @TypeConverter
    @JvmStatic
    fun toActionOutcome(value: String?): ActionOutcome? = value?.let { ActionOutcome.valueOf(it) }

    @TypeConverter
    @JvmStatic
    fun fromEventTrigger(value: EventTrigger?): String? = value?.name

    @TypeConverter
    @JvmStatic
    fun toEventTrigger(value: String?): EventTrigger? = value?.let { EventTrigger.valueOf(it) }

    @TypeConverter
    @JvmStatic
    fun fromInfluenceType(value: InfluenceType?): String? = value?.name

    @TypeConverter
    @JvmStatic
    fun toInfluenceType(value: String?): InfluenceType? = value?.let { InfluenceType.valueOf(it) }

    @TypeConverter
    @JvmStatic
    fun fromInterruptionLevel(value: InterruptionLevel?): String? = value?.name

    @TypeConverter
    @JvmStatic
    fun toInterruptionLevel(value: String?): InterruptionLevel? = value?.let { InterruptionLevel.valueOf(it) }

    @TypeConverter
    @JvmStatic
    fun fromSyncStatus(value: com.mcfly.shield_ai.data.local.entities.LogEntryEntity.SyncStatus?): String? = value?.name

    @TypeConverter
    @JvmStatic
    fun toSyncStatus(value: String?): com.mcfly.shield_ai.data.local.entities.LogEntryEntity.SyncStatus? =
        value?.let { com.mcfly.shield_ai.data.local.entities.LogEntryEntity.SyncStatus.valueOf(it) }

    @TypeConverter
    @JvmStatic
    fun fromEventType(value: com.mcfly.shield_ai.data.local.entities.LogEntryEntity.EventType?): String? = value?.name

    @TypeConverter
    @JvmStatic
    fun toEventType(value: String?): com.mcfly.shield_ai.data.local.entities.LogEntryEntity.EventType? =
        value?.let { com.mcfly.shield_ai.data.local.entities.LogEntryEntity.EventType.valueOf(it) }


}
