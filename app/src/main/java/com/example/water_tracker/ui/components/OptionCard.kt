package com.example.water_tracker.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.water_tracker.R
import com.example.water_tracker.theme.HydrationTrackerTheme
import com.example.water_tracker.theme.TestColor

@Composable
fun OptionCard(
    onCardClicked: () -> Unit,
    title: String,
    @DrawableRes icon: Int,
    modifier: Modifier = Modifier,
    type: OptionType = OptionType.COMMON_OPTION,
    backgroundColor: Color = MaterialTheme.colors.primary,
    textColor: Color = TestColor,
) {
    Card(
        modifier = modifier.clickable { onCardClicked() },
        backgroundColor = backgroundColor,
        shape = MaterialTheme.shapes.medium,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterVertically)
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = title,
                modifier = Modifier.size(25.dp),
                tint = textColor
            )

            if (type == OptionType.COMMON_OPTION) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.body1.copy(
                        color = textColor,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }
    }
}

enum class OptionType {
    COMMON_OPTION,
}

@Preview
@Composable
fun OptionCardPreview() {
    HydrationTrackerTheme {
        OptionCard(
            onCardClicked = { },
            title = "150ml",
            icon = R.drawable.ic_history,
            modifier = Modifier.size(100.dp)
        )
    }
}