package com.mcfly.shield_ai.data.local.entities

import androidx.room.*
import com.mcfly.shield_ai.model.PsychNeed

@Entity(tableName = "goal_profile")
data class GoalProfileEntity(
    @PrimaryKey
    @ColumnInfo(name = "user_id")
    val userId: String,

    @ColumnInfo(name = "ideal_self")
    val idealSelf: String,

    @ColumnInfo(name = "daily_focus")
    val dailyFocus: String? = null,

    @ColumnInfo(name = "demotivation_signals")
    val demotivationSignals: List<String> = emptyList(),

    @ColumnInfo(name = "life_goals")
    val lifeGoals: List<String> = emptyList(),

    @ColumnInfo(name = "core_values")
    val coreValues: List<String> = emptyList(),

    @ColumnInfo(name = "ideal_confidence")
    val idealConfidence: String = "Not set",

    @ColumnInfo(name = "emotional_triggers")
    val emotionalTriggers: Map<String, List<String>> = emptyMap(),

    @ColumnInfo(name = "psych_needs")
    val psychNeeds: Map<String, PsychNeed> = emptyMap()
) {
    val goals: List<String>
        get() = lifeGoals
}
