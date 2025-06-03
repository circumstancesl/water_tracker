package com.example.water_tracker.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_histories")
data class DailyHistory(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    val date: Long,
    val totalAmount: Int = 0
)
