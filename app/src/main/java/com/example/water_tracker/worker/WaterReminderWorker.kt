package com.example.water_tracker.worker

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.water_tracker.R

class WaterReminderWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        showNotification()
        return Result.success()
    }

    private fun showNotification() {
        val notification = NotificationCompat.Builder(applicationContext, "water_reminder_channel")
            .setContentTitle("Пора пить воду!")
            .setContentText("Выпейте стакан воды для поддержания водного баланса")
            .setSmallIcon(R.drawable.ic_water_drop)
            .setAutoCancel(true)
            .build()

        val notificationManager = applicationContext
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    companion object {
        const val NOTIFICATION_ID = 1
        const val WORK_TAG = "water_reminder_work"
    }
}