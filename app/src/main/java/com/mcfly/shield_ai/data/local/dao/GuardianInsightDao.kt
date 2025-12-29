package com.mcfly.shield_ai.data.local.dao

import androidx.room.*
import com.mcfly.shield_ai.data.local.entities.GuardianInsightEntity
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query



@Dao
interface GuardianInsightDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(insight: GuardianInsightEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(insights: List<GuardianInsightEntity>)

    @Query("SELECT * FROM guardian_insights ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecent(limit: Int = 50): List<GuardianInsightEntity>

    @Query("DELETE FROM guardian_insights")
    suspend fun clearAll()

    @Query("SELECT * FROM guardian_insights WHERE timestamp >= :cutoff ORDER BY timestamp ASC")
    suspend fun getSinceTimestamp(cutoff: Long): List<GuardianInsightEntity>


}
