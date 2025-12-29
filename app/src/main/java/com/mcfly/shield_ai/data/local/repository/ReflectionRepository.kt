package com.mcfly.shield_ai.data.repository

import androidx.lifecycle.LiveData
import com.mcfly.shield_ai.data.local.ReflectionDao
import com.mcfly.shield_ai.data.local.entities.ReflectionEntity

class ReflectionRepository(private val dao: ReflectionDao) {

    val allReflections: LiveData<List<ReflectionEntity>> = dao.getAllReflections()

    suspend fun insertReflection(reflection: ReflectionEntity) {
        dao.insert(reflection)
    }

    suspend fun deleteReflection(reflection: ReflectionEntity) {
        dao.delete(reflection)
    }

    suspend fun clearAll() {
        dao.clearAll()
    }
}
