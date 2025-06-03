package com.example.water_tracker.ui.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.water_tracker.utils.SharedPrefHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableLiveData<SettingsState>()
    val state: LiveData<SettingsState> = _state

    init {
        _state.value = SettingsState()
        initData()
    }

    private fun initData() {
        val dailyGoals = SharedPrefHelper.readInt(
            SharedPrefHelper.PREF_DAILY_GOAL,
            SharedPrefHelper.DEFAULT_DAILY_GOAL
        )
        _state.value = _state.value?.copy(
            dailyGoals = dailyGoals
        )
    }

    fun saveNewGoals(newGoals: Int) {
        SharedPrefHelper.saveInt(SharedPrefHelper.PREF_DAILY_GOAL, newGoals)
        initData()
    }
}