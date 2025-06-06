package com.example.water_tracker.data.models

import java.util.Date

data class WeekData(
    val weekNumber: Int,
    val year: Int,
    val startDate: Date,
    val endDate: Date,
    val totalAmount: Int
)