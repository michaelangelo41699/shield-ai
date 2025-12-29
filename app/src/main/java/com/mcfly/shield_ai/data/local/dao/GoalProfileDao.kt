package com.mcfly.shield_ai.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.mcfly.shield_ai.data.local.entities.GoalProfileEntity

@Dao
interface GoalProfileDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(profile: GoalProfileEntity)

    @Query("SELECT * FROM goal_profile WHERE user_id = :userId LIMIT 1")
    suspend fun getProfile(userId: String): GoalProfileEntity?

    @Query("SELECT * FROM goal_profile WHERE user_id = :userId LIMIT 1")
    fun getProfileLive(userId: String): LiveData<GoalProfileEntity?>

    @Query("SELECT * FROM goal_profile WHERE user_id = :userId LIMIT 1")
    suspend fun getByUserId(userId: String): GoalProfileEntity?

    @Query("DELETE FROM goal_profile WHERE user_id = :userId")
    suspend fun deleteByUserId(userId: String)

    @Query("DELETE FROM goal_profile")
    suspend fun clearAll()
}
