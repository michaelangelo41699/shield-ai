package com.mcfly.shield_ai.data.local.dao

import androidx.room.*
import com.mcfly.shield_ai.data.local.entities.LogEntryEntity
import com.mcfly.shield_ai.data.local.dto.AppUsageStat
import com.mcfly.shield_ai.data.local.dto.HourlyUsageStat
import kotlinx.coroutines.flow.Flow

@Dao
interface LogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: LogEntryEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entries: List<LogEntryEntity>)

    @Query("SELECT * FROM log_entries WHERE syncStatus = 'PENDING' ORDER BY timestamp ASC LIMIT :limit")
    suspend fun getPendingLogs(limit: Int = 100): List<LogEntryEntity>

    @Query("SELECT * FROM log_entries WHERE appPackage = :packageName ORDER BY timestamp DESC")
    fun getLogsByPackage(packageName: String): Flow<List<LogEntryEntity>>

    @Query("SELECT COUNT(*) FROM log_entries WHERE syncStatus = 'PENDING'")
    fun getPendingLogsCount(): Flow<Int>

    @Update
    suspend fun update(entry: LogEntryEntity)

    @Query("UPDATE log_entries SET syncStatus = 'SYNCED' WHERE id IN (:ids)")
    suspend fun markAsSynced(ids: List<Long>)

    @Query("DELETE FROM log_entries WHERE syncStatus = 'SYNCED' AND timestamp < :threshold")
    suspend fun cleanupOldLogs(threshold: Long = System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000)

    @Query("DELETE FROM log_entries WHERE id = :id")
    suspend fun deleteById(id: Long)

    // ✅ Fixed complex query with data class
    @Query("""
        SELECT appPackage, COUNT(*) AS count 
        FROM log_entries 
        WHERE timestamp BETWEEN :startTime AND :endTime
        GROUP BY appPackage 
        ORDER BY count DESC
        LIMIT 5
    """)
    suspend fun getTopAppsByUsage(startTime: Long, endTime: Long): List<AppUsageStat>

    // ✅ Fixed complex query with data class
    @Query("""
        SELECT CAST(strftime('%H', datetime(timestamp / 1000, 'unixepoch')) AS INTEGER) AS hour, 
               COUNT(*) AS count
        FROM log_entries
        WHERE timestamp BETWEEN :startDate AND :endDate
        GROUP BY hour
    """)
    suspend fun getUsageByHour(startDate: Long, endDate: Long): List<HourlyUsageStat>
}
