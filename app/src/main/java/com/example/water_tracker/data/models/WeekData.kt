package com.example.water_tracker.data.models

data class WeekData(
    val year: Int,
    val week: Int,
    val startDate: Long,
    val endDate: Long,
    val totalAmount: Int
)
