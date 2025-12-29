package com.mcfly.shield_ai.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.mcfly.shield_ai.data.local.entities.ReflectionEntity

@Dao
interface ReflectionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(reflection: ReflectionEntity)

    @Query("SELECT * FROM reflections ORDER BY timestamp DESC")
    fun getAllReflections(): LiveData<List<ReflectionEntity>>

    @Query("SELECT * FROM reflections WHERE timestamp BETWEEN :start AND :end ORDER BY timestamp DESC")
    fun getReflectionsBetween(start: Long, end: Long): LiveData<List<ReflectionEntity>>

    @Delete
    suspend fun delete(reflection: ReflectionEntity)
}
