package com.mcfly.shield_ai.data.local


import com.mcfly.shield_ai.data.local.dao.LogDao
import com.mcfly.shield_ai.data.local.dto.AppUsageStat
import com.mcfly.shield_ai.data.local.dto.HourlyUsageStat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LogRepository(private val logDao: LogDao) {

    suspend fun getTopAppsByUsageMap(startTime: Long, endTime: Long): Map<String, Int> =
        withContext(Dispatchers.IO) {
            val topApps: List<AppUsageStat> = logDao.getTopAppsByUsage(startTime, endTime)
            topApps.associate { it.appPackage to it.count }
        }

    suspend fun getUsageByHourMap(startDate: Long, endDate: Long): Map<Int, Int> =
        withContext(Dispatchers.IO) {
            val hourlyStats: List<HourlyUsageStat> = logDao.getUsageByHour(startDate, endDate)
            hourlyStats.associate { it.hour to it.count }
        }


    suspend fun getTopAppsByUsage(startTime: Long, endTime: Long): List<AppUsageStat> =
        logDao.getTopAppsByUsage(startTime, endTime)

    suspend fun getUsageByHour(startDate: Long, endDate: Long): List<HourlyUsageStat> =
        logDao.getUsageByHour(startDate, endDate)

}
