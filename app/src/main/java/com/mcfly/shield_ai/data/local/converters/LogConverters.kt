package com.mcfly.shield_ai.data.local.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mcfly.shield_ai.data.local.entities.LogEntryEntity
import java.util.*

class LogConverters {
    private val gson = Gson()

    // SyncStatus converters
    @TypeConverter
    fun fromSyncStatus(status: LogEntryEntity.SyncStatus): String = status.name

    @TypeConverter
    fun toSyncStatus(value: String): LogEntryEntity.SyncStatus = enumValueOf(value)

    // EventType converters
    @TypeConverter
    fun fromEventType(type: LogEntryEntity.EventType): String = type.name

    @TypeConverter
    fun toEventType(value: String): LogEntryEntity.EventType = enumValueOf(value)

    // Metadata Map converters
    @TypeConverter
    fun fromMetadata(map: Map<String, String>): String = gson.toJson(map)

    @TypeConverter
    fun toMetadata(json: String): Map<String, String> =
        gson.fromJson(json, object : TypeToken<Map<String, String>>() {}.type)

    // Date converters (optional but recommended)
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? = value?.let { Date(it) }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? = date?.time


    // Optional: ContextData Map<String, Any?> support for future parsing
    @TypeConverter
    fun fromContextData(map: Map<String, Any?>?): String = gson.toJson(map)

    @TypeConverter
    fun toContextData(json: String): Map<String, Any?> =
        gson.fromJson(json, object : TypeToken<Map<String, Any?>>() {}.type)
}
