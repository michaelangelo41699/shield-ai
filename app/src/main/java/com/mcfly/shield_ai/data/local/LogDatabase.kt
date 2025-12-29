package com.mcfly.shield_ai.data.local

import android.content.Context
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.mcfly.shield_ai.data.local.converters.GoalProfileConverters
import com.mcfly.shield_ai.data.local.converters.TypeConverters as AppConverters
import com.mcfly.shield_ai.data.local.dao.GoalProfileDao
import com.mcfly.shield_ai.data.local.dao.LogDao
import com.mcfly.shield_ai.data.local.dao.GuardianInsightDao
import com.mcfly.shield_ai.data.local.entities.GoalProfileEntity
import com.mcfly.shield_ai.data.local.entities.LogEntryEntity
import com.mcfly.shield_ai.data.local.entities.GuardianInsightEntity

@Database(
    entities = [
        LogEntryEntity::class,
        GoalProfileEntity::class,
        GuardianInsightEntity::class
    ],
    version = 4,
    exportSchema = true
)
@TypeConverters(
    AppConverters::class,
    GoalProfileConverters::class
)
abstract class LogDatabase : RoomDatabase() {

    abstract fun logDao(): LogDao
    abstract fun goalProfileDao(): GoalProfileDao
    abstract fun guardianInsightDao(): GuardianInsightDao

    companion object {
        @Volatile
        private var INSTANCE: LogDatabase? = null

        fun getInstance(context: Context): LogDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }

        private fun buildDatabase(context: Context): LogDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                LogDatabase::class.java,
                "shield_ai_logs.db"
            )
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                .fallbackToDestructiveMigrationOnDowngrade()
                .build()
        }

        // Migration: v1 → v2
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    ALTER TABLE log_entries 
                    ADD COLUMN eventType TEXT NOT NULL DEFAULT 'NOTIFICATION'
                """.trimIndent())
                db.execSQL("""
                    ALTER TABLE log_entries 
                    ADD COLUMN metadata TEXT NOT NULL DEFAULT '{}'
                """.trimIndent())
                db.execSQL("""
                    CREATE INDEX IF NOT EXISTS index_log_entries_eventType 
                    ON log_entries(eventType)
                """.trimIndent())
            }
        }

        // Migration: v2 → v3
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS goal_profile (
                        user_id TEXT PRIMARY KEY NOT NULL,
                        ideal_self TEXT,
                        life_goals TEXT,
                        core_values TEXT,
                        daily_focus TEXT,
                        demotivation_signals TEXT,
                        emotional_triggers TEXT,
                        psych_needs TEXT
                    )
                """.trimIndent())
            }
        }

        // Migration: v3 → v4
        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS guardian_insights (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        label TEXT NOT NULL,
                        confidence REAL NOT NULL,
                        sourceText TEXT NOT NULL,
                        patternName TEXT,
                        category TEXT NOT NULL,
                        metadata TEXT NOT NULL,
                        timestamp INTEGER NOT NULL,
                        influenceType TEXT NOT NULL,
                        interruptionLevel TEXT NOT NULL,
                        deliveryMechanism TEXT NOT NULL,
                        influenceCategory TEXT NOT NULL,
                        influenceSubCategory TEXT NOT NULL,
                        monetizationType TEXT NOT NULL,
                        logContext TEXT NOT NULL,
                        userSegment TEXT NOT NULL,
                        outcome TEXT NOT NULL,
                        trigger TEXT NOT NULL
                    )
                """.trimIndent())
            }
        }
    }
}
