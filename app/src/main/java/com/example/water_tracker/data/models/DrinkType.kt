package com.example.water_tracker.data.models

import androidx.annotation.DrawableRes

data class DrinkType(
    val name: String,
    @DrawableRes val icon: Int,
    val amount: Int
)