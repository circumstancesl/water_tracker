package com.example.water_tracker.utils

import android.util.Log
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.format.DateTimeParseException
import java.util.*

object DateFormatter {

    fun formatDate(date: Date): Date? {
        return try {
            val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val resultString = simpleDateFormat.format(date)
            simpleDateFormat.parse(resultString)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun formatDateToString(date: Date): String {
        val formatter = SimpleDateFormat("MMM dd yyyy", Locale.getDefault())
        return formatter.format(date)
    }
}
