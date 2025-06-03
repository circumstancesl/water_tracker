package com.example.water_tracker.ui.screens.history

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.util.Log
import com.example.water_tracker.data.models.DailyHistory
import com.example.water_tracker.data.repository.DailyHistoryRepository
import com.example.water_tracker.ui.screens.home.HomeState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repository: DailyHistoryRepository
) : ViewModel() {
    private val _state = mutableStateOf(HistoryState())
    val state = _state

    init {
        loadHistories()
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
                repository.updateHistory(history.copy(totalAmount = amount))
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