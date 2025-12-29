package com.mcfly.shield_ai.data.local.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mcfly.shield_ai.model.PsychNeed

object GoalProfileConverters {
    private val gson = Gson()

    @TypeConverter
    fun fromStringList(list: List<String>?): String = gson.toJson(list)

    @TypeConverter
    fun toStringList(json: String): List<String> =
        gson.fromJson(json, object : TypeToken<List<String>>() {}.type)

    @TypeConverter
    fun fromStringMap(map: Map<String, List<String>>?): String = gson.toJson(map)

    @TypeConverter
    fun toStringMap(json: String): Map<String, List<String>> =
        gson.fromJson(json, object : TypeToken<Map<String, List<String>>>() {}.type)

    @TypeConverter
    fun fromPsychNeedMap(map: Map<String, PsychNeed>?): String = gson.toJson(map)

    @TypeConverter
    fun toPsychNeedMap(json: String): Map<String, PsychNeed> =
        gson.fromJson(json, object : TypeToken<Map<String, PsychNeed>>() {}.type)
}


