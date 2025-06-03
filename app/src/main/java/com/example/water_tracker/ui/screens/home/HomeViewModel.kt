package com.example.water_tracker.ui.screens.home

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.water_tracker.data.models.DailyHistory
import com.example.water_tracker.data.repository.DailyHistoryRepository
import com.example.water_tracker.utils.DateFormatter
import com.example.water_tracker.utils.DrinkTypeData
import com.example.water_tracker.utils.SharedPrefHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val dailyHistoryRepository: DailyHistoryRepository
) : ViewModel() {

    var state by mutableStateOf(HomeState())
        private set

    fun initDrinkType() {
        val totalAmount = SharedPrefHelper.readInt(SharedPrefHelper.PREF_DAILY_GOAL, 2700)
        state = state.copy(drinkTypes = DrinkTypeData.getData(), totalAmount = totalAmount)
    }

    fun initData() {
        viewModelScope.launch {
            val date = DateFormatter.formatDate(Date()) ?: Date()
            dailyHistoryRepository.getHistory(date)?.let {
                state = state.copy(
                    history = it,
                    percentage = it.totalAmount.toFloat() / state.totalAmount.toFloat()
                )
            }
        }
    }

    fun addWater(amount: Int) {
        if (state.history == null) {
            initData()
        }

        viewModelScope.launch {
            state.history?.let {
                dailyHistoryRepository.createHistory(it.copy(totalAmount = it.totalAmount + amount))
                initData()
            }
        }
    }

    fun reduceWater(amount: Int) {
        if (state.history == null) {
            initData()
        }

        viewModelScope.launch {
            initData()
            state.history?.let {
                dailyHistoryRepository.createHistory(it.copy(totalAmount = it.totalAmount - amount))
                initData()
            }
        }
    }

    fun toggleCustomAmountDialog(show: Boolean) {
        state = state.copy(showCustomAmountDialog = show)
    }

    fun updateCustomAmount(amount: String) {
        state = state.copy(customAmount = amount)
    }

    fun toggleUnit() {
        state = state.copy(isLiter = !state.isLiter)
    }

    fun addCustomWater() {
        val amount = state.customAmount.toIntOrNull() ?: 0
        if (amount > 0) {
            val finalAmount = if (state.isLiter) amount * 1000 else amount
            addWater(finalAmount)
            state = state.copy(
                showCustomAmountDialog = false,
                customAmount = ""
            )
        }
    }
}
