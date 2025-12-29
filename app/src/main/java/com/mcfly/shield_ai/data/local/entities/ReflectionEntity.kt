// 1. ReflectionEntity.kt
package com.mcfly.shield_ai.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(tableName = "reflections")
data class ReflectionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Instant = Instant.now(),
    val text: String
)
