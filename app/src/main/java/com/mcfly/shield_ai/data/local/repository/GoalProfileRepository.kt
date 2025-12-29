package com.mcfly.shield_ai.data.local.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mcfly.shield_ai.data.local.LogDatabase
import com.mcfly.shield_ai.data.local.entities.GoalProfileEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GoalProfileRepository private constructor(private val db: LogDatabase) {

    private val _profileLiveData = MutableLiveData<GoalProfileEntity?>()
    val profileLiveData: LiveData<GoalProfileEntity?> get() = _profileLiveData

    companion object {
        @Volatile private var INSTANCE: GoalProfileRepository? = null

        fun getInstance(context: Context): GoalProfileRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: GoalProfileRepository(LogDatabase.getInstance(context)).also { INSTANCE = it }
            }
        }
    }

    fun getProfileLive(userId: String): LiveData<GoalProfileEntity?> {
        return db.goalProfileDao().getProfileLive(userId)
    }

    suspend fun getProfile(userId: String): GoalProfileEntity? = withContext(Dispatchers.IO) {
        db.goalProfileDao().getProfile(userId)
    }

    suspend fun getActiveProfile(): GoalProfileEntity? = withContext(Dispatchers.IO) {
        return@withContext db.goalProfileDao().getByUserId("default")
    }

    suspend fun saveProfile(profile: GoalProfileEntity) {
        withContext(Dispatchers.IO) {
            db.goalProfileDao().insertOrUpdate(profile)
            _profileLiveData.postValue(profile)
        }
    }

    suspend fun deleteProfile(userId: String = "default") = withContext(Dispatchers.IO) {
        db.goalProfileDao().deleteByUserId(userId)
        _profileLiveData.postValue(null)
    }

    suspend fun clearAll() = withContext(Dispatchers.IO) {
        db.goalProfileDao().clearAll()
        _profileLiveData.postValue(null)
    }
}
