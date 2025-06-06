package com.example.water_tracker.ui.screens.history

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.water_tracker.data.models.DailyHistory
import com.example.water_tracker.data.models.MonthData
import com.example.water_tracker.data.models.WeekData
import com.example.water_tracker.data.repository.DailyHistoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repository: DailyHistoryRepository
) : ViewModel() {
    private val _state = mutableStateOf(HistoryState())
    val state = _state

    private val _selectedPeriod = mutableStateOf(StatsPeriod.DAY)
    val selectedPeriod = _selectedPeriod

    val weeklyStats: List<WeekData> by derivedStateOf {
        state.value.histories.groupBy { history ->
            val calendar = Calendar.getInstance().apply {
                timeInMillis = history.date
                firstDayOfWeek = Calendar.MONDAY
            }
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            calendar.timeInMillis
        }.map { (weekStart, histories) ->
            val calendar = Calendar.getInstance().apply {
                timeInMillis = weekStart
                firstDayOfWeek = Calendar.MONDAY
            }

            val startOfWeek = calendar.timeInMillis
            calendar.add(Calendar.DAY_OF_WEEK, 6)
            val endOfWeek = calendar.timeInMillis

            WeekData(
                year = calendar.get(Calendar.YEAR),
                week = calendar.get(Calendar.WEEK_OF_YEAR),
                startDate = startOfWeek,
                endDate = endOfWeek,
                totalAmount = histories.sumOf { it.totalAmount }
            )
        }.sortedByDescending { it.startDate }
    }

    val monthlyStats: List<MonthData> by derivedStateOf {
        state.value.histories.groupBy { history ->
            val calendar = Calendar.getInstance().apply { timeInMillis = history.date }
            Pair(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH))
        }.map { (yearMonth, histories) ->
            MonthData(
                year = yearMonth.first,
                month = yearMonth.second,
                totalAmount = histories.sumOf { it.totalAmount }
            )
        }.sortedWith(compareByDescending<MonthData> { it.year }.thenByDescending { it.month })
    }

    init {
        loadHistories()
    }

    fun setPeriod(period: StatsPeriod) {
        _selectedPeriod.value = period
    }

    private fun loadHistories() {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                histories = repository.getHistories()
            )
        }
    }

    fun selectHistory(history: DailyHistory?) {
        _state.value = _state.value.copy(
            selectedHistory = history,
            editAmount = history?.totalAmount?.toString() ?: ""
        )
    }

    fun showEditDialog(show: Boolean) {
        _state.value = _state.value.copy(showEditDialog = show)
    }

    fun showDeleteDialog(show: Boolean) {
        _state.value = _state.value.copy(showDeleteDialog = show)
    }

    fun updateHistory() {
        val amount = state.value.editAmount.toIntOrNull() ?: run {
            return
        }

        state.value.selectedHistory?.let { history ->
            viewModelScope.launch {
                repository.createHistory(history.copy(totalAmount = amount))
                loadHistories()
            }
        }

        _state.value = _state.value.copy(
            showEditDialog = false,
            selectedHistory = null,
        )
    }

    fun deleteHistory() {
        state.value.selectedHistory?.let { history ->
            viewModelScope.launch {
                repository.deleteHistory(history)
                loadHistories()
            }
        }

        _state.value = _state.value.copy(
            showDeleteDialog = false,
            selectedHistory = null,
        )
    }
}