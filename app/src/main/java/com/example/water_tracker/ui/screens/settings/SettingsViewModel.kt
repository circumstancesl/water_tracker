package com.example.water_tracker.ui.screens.settings

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.water_tracker.utils.SharedPrefHelper
import com.example.water_tracker.reminder.ReminderManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val reminderManager: ReminderManager
) : ViewModel() {

    var state by mutableStateOf(SettingsState())
        private set

    private val _reminderInterval = mutableStateOf(60L)
    val reminderInterval: State<Long> = _reminderInterval

    private val _notificationsEnabled = mutableStateOf(false)
    val notificationsEnabled: State<Boolean> = _notificationsEnabled

    init {
        initData()
    }

    private fun initData() {
        val dailyGoals = SharedPrefHelper.readInt(
            SharedPrefHelper.PREF_DAILY_GOAL,
            SharedPrefHelper.DEFAULT_DAILY_GOAL
        )

        val isReminderEnabled = SharedPrefHelper.readBoolean(
            SharedPrefHelper.PREF_REMINDER_ENABLED,
            false
        )

        val interval = SharedPrefHelper.readLong(
            SharedPrefHelper.PREF_REMINDER_INTERVAL,
            60L
        )

        state = state.copy(
            dailyGoals = dailyGoals
        )
        _notificationsEnabled.value = isReminderEnabled
        _reminderInterval.value = interval
    }

    fun saveNewGoals(newGoals: Int) {
        SharedPrefHelper.saveInt(SharedPrefHelper.PREF_DAILY_GOAL, newGoals)
        initData()
    }

    fun setReminderInterval(minutes: Long) {
        _reminderInterval.value = minutes
        SharedPrefHelper.saveLong(SharedPrefHelper.PREF_REMINDER_INTERVAL, minutes)
        if (_notificationsEnabled.value) {
            restartReminders()
        }
    }

    fun toggleNotifications(enabled: Boolean) {
        _notificationsEnabled.value = enabled
        SharedPrefHelper.saveBoolean(SharedPrefHelper.PREF_REMINDER_ENABLED, enabled)
        if (enabled) {
            startReminders()
        } else {
            stopReminders()
        }
    }

    private fun startReminders() {
        reminderManager.setReminder(_reminderInterval.value)
    }

    private fun stopReminders() {
        reminderManager.cancelReminder()
    }

    private fun restartReminders() {
        stopReminders()
        startReminders()
    }
}