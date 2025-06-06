package com.example.water_tracker.data.repository

import android.util.Log
import com.example.water_tracker.data.database.dao.DailyDrinkDAO
import com.example.water_tracker.data.models.DailyHistory
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DailyHistoryRepository @Inject constructor(private val dailyHistoryDao: DailyDrinkDAO) {

    suspend fun getHistory(date: Date): DailyHistory? {
        return try {
            dailyHistoryDao.getHistoryByDate(date.time)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getHistories(): List<DailyHistory> {
        return dailyHistoryDao.getAllHistories()
    }

    suspend fun createHistory(dailyHistory: DailyHistory) {
        try {
            dailyHistoryDao.createOrUpdateDrinkHistory(dailyHistory)
        } catch (e: Exception) {
            Log.e(DailyHistoryRepository::class.simpleName, "getHistory: ${e.localizedMessage}")
        }
    }

    suspend fun deleteHistory(dailyHistory: DailyHistory) {
        try {
            dailyHistoryDao.deleteHistory(dailyHistory)
        } catch (e: Exception) {
            Log.e(DailyHistoryRepository::class.simpleName, "deleteHistory: ${e.localizedMessage}")
        }
    }
}
