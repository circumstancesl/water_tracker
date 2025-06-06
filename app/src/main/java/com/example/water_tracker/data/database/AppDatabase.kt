package com.example.water_tracker.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.water_tracker.data.database.dao.DailyDrinkDAO
import com.example.water_tracker.data.models.DailyHistory

@Database(entities = [DailyHistory::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun dailyDrinkDao(): DailyDrinkDAO

    companion object {
        const val DATABASE_NAME = "WaterTrackerDatabase"
    }
}
