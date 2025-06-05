package com.example.water_tracker.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import com.google.accompanist.systemuicontroller.rememberSystemUiController

private val LightColorPalette = lightColors(
    primary = PrimaryColor,
    primaryVariant = PrimaryVariantColor,
    secondary = SecondaryColor,
    background = BackgroundColor,
    onBackground = OnBackgroundColor,
    onPrimary = OnPrimaryColor,
    surface = BackgroundColor,
    onSurface = OnBackgroundColor
)

@Composable
fun WaterTrackerTheme(
    content: @Composable () -> Unit
) {
    val colors = LightColorPalette

    val systemUiController = rememberSystemUiController()
    systemUiController.setStatusBarColor(colors.background, darkIcons = true)
    systemUiController.setNavigationBarColor(colors.primaryVariant, darkIcons = true)

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}