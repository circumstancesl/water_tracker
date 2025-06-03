package com.example.water_tracker.ui.screens.history

import com.example.water_tracker.data.models.DailyHistory

data class HistoryState(
    val histories: List<DailyHistory> = emptyList(),
    val selectedHistory: DailyHistory? = null,
    val showEditDialog: Boolean = false,
    val showDeleteDialog: Boolean = false,
    val editAmount: String = ""
)