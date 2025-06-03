package com.example.water_tracker.ui.screens.settings

import com.example.water_tracker.utils.SharedPrefHelper

data class SettingsState(val dailyGoals: Int = SharedPrefHelper.DEFAULT_DAILY_GOAL)
