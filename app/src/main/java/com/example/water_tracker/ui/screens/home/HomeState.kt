package com.example.water_tracker.ui.screens.home

import com.example.water_tracker.data.models.DailyHistory
import com.example.water_tracker.data.models.DrinkType

data class HomeState(
    val history: DailyHistory? = null,
    val drinkTypes: List<DrinkType>? = null,
    val percentage: Float = 0F,
    val totalAmount: Int = 0,
    val showCustomAmountDialog: Boolean = false,
    val customAmount: String = "",
    val isLiter: Boolean = false,
)