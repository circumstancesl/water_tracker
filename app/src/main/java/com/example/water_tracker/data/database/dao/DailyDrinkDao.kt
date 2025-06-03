package com.example.water_tracker.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.water_tracker.data.models.DailyHistory

@Dao
interface DailyDrinkDAO {

    @Query("SELECT * FROM daily_histories WHERE date = :date")
    suspend fun getHistoryByDate(date: Long): DailyHistory

    @Query("SELECT * FROM daily_histories ORDER BY date DESC")
    suspend fun getAllHistories(): List<DailyHistory>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createOrUpdateDrinkHistory(drinkHistory: DailyHistory)

    @Delete
    suspend fun deleteHistory(drinkHistory: DailyHistory)

    @Update
    suspend fun updateHistory(drinkHistory: DailyHistory)
}