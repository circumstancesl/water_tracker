package com.example.water_tracker

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.core.content.getSystemService
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.WorkManager
import com.example.water_tracker.utils.SharedPrefHelper
import com.example.water_tracker.worker.ReminderManager
import dagger.hilt.EntryPoint
import dagger.hilt.EntryPoints
import dagger.hilt.InstallIn
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.components.SingletonComponent

@HiltAndroidApp
class HiltApplication : Application(), Configuration.Provider {
    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface HiltWorkerFactoryEntryPoint {
        fun workerFactory(): HiltWorkerFactory
    }

    override val workManagerConfiguration: Configuration = Configuration.Builder()
        .setWorkerFactory(EntryPoints.get(this, HiltWorkerFactoryEntryPoint::class.java).workerFactory())
        .build()

    override fun onCreate() {
        super.onCreate()
        SharedPrefHelper.initialize(this)
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "water_reminder_channel",
                "Напоминания о воде",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Напоминания пить воду через заданные интервалы"
            }
            getSystemService<NotificationManager>()?.createNotificationChannel(channel)
        }
    }
}