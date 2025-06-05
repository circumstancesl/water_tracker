package com.example.water_tracker.reminder

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.water_tracker.worker.WaterReminderWorker
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ReminderManager @Inject constructor(
    private val context: Context,
    private val workManager: WorkManager
) {

    fun setReminder(intervalMinutes: Long) {
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .build()

        val request = PeriodicWorkRequestBuilder<WaterReminderWorker>(
            intervalMinutes, TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .addTag(WATER_REMINDER_TAG)
            .build()

        workManager.enqueueUniquePeriodicWork(
            WATER_REMINDER_WORK_NAME,
            ExistingPeriodicWorkPolicy.REPLACE,
            request
        )
    }

    fun cancelReminder() {
        workManager.cancelAllWorkByTag(WATER_REMINDER_TAG)
    }

    companion object {
        const val WATER_REMINDER_WORK_NAME = "water_reminder_work_name"
        const val WATER_REMINDER_TAG = "water_reminder_tag"
    }
}