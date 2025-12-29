package com.mcfly.shield_ai.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mcfly.shield_ai.data.local.dao.ReflectionDao
import com.mcfly.shield_ai.data.local.entities.ReflectionEntity

@Database(entities = [ReflectionEntity::class], version = 1, exportSchema = false)
abstract class ReflectionDatabase : RoomDatabase() {

    abstract fun reflectionDao(): ReflectionDao

    companion object {
        @Volatile
        private var INSTANCE: ReflectionDatabase? = null

        fun getInstance(context: Context): ReflectionDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ReflectionDatabase::class.java,
                    "reflection_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
