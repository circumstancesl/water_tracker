package com.example.water_tracker.ui.screens.history

import com.example.water_tracker.R
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleOwner
import com.example.water_tracker.data.models.MonthData
import com.example.water_tracker.data.models.WeekData
import com.example.water_tracker.ui.components.ContextMenuDialog
import com.example.water_tracker.ui.components.DeleteConfirmationDialog
import com.example.water_tracker.ui.components.DialogEditValue
import com.example.water_tracker.ui.components.HistoryItem
import com.example.water_tracker.ui.theme.QuickSand
import com.example.water_tracker.utils.DateFormatter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    val selectedPeriod = viewModel.selectedPeriod.value

    Scaffold(
        backgroundColor = MaterialTheme.colors.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "History",
                        color = MaterialTheme.colors.onBackground,
                        fontFamily = QuickSand,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                backgroundColor = MaterialTheme.colors.background,
                elevation = 0.dp
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(MaterialTheme.colors.background)
        ) {
            PeriodSelector(
                selectedPeriod = selectedPeriod,
                onPeriodSelected = { viewModel.setPeriod(it) }
            )

            Spacer(modifier = Modifier.height(8.dp))

            when (selectedPeriod) {
                StatsPeriod.DAY -> DailyHistoryContent(viewModel)
                StatsPeriod.WEEK -> WeeklyHistoryContent(viewModel)
                StatsPeriod.MONTH -> MonthlyHistoryContent(viewModel)
            }
        }
    }

    if (state.selectedHistory != null) {
        ContextMenuDialog(
            onEditClick = { viewModel.showEditDialog(true) },
            onDeleteClick = { viewModel.showDeleteDialog(true) },
            onDismiss = { viewModel.selectHistory(null) }
        )
    }

    if (state.showEditDialog) {
        DialogEditValue(
            title = "Edit Record",
            value = state.editAmount,
            onSubmit = { newValue ->
                viewModel.state.value = viewModel.state.value.copy(editAmount = newValue)
                viewModel.updateHistory()
            },
            onDismiss = { viewModel.showEditDialog(false) }
        )
    }

    if (state.showDeleteDialog) {
        DeleteConfirmationDialog(
            onConfirm = { viewModel.deleteHistory() },
            onDismiss = { viewModel.showDeleteDialog(false) }
        )
    }
}

@Composable
private fun PeriodSelector(
    selectedPeriod: StatsPeriod,
    onPeriodSelected: (StatsPeriod) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        backgroundColor = MaterialTheme.colors.primaryVariant.copy(alpha = 0.2f),
        elevation = 0.dp,
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            StatsPeriod.values().forEach { period ->
                val isSelected = period == selectedPeriod
                val backgroundColor = MaterialTheme.colors.primary
                val textColor = MaterialTheme.colors.onPrimary

                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(backgroundColor)
                        .clickable { onPeriodSelected(period) }
                        .padding(vertical = 10.dp, horizontal = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = when (period) {
                            StatsPeriod.DAY -> "Days"
                            StatsPeriod.WEEK -> "Weeks"
                            StatsPeriod.MONTH -> "Months"
                        },
                        color = textColor,
                        fontFamily = QuickSand,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun DailyHistoryContent(viewModel: HistoryViewModel) {
    val state = viewModel.state.value

    LazyColumn(
        modifier = Modifier.padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(state.histories) { history ->
            HistoryItem(
                date = DateFormatter.formatDateToString(Date(history.date)),
                value = history.totalAmount.toString(),
                onLongClick = { viewModel.selectHistory(history) }
            )
        }
    }
}

@Composable
private fun WeeklyHistoryContent(viewModel: HistoryViewModel) {
    val weeklyStats = viewModel.weeklyStats

    LazyColumn(
        modifier = Modifier.padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(weeklyStats) { week ->
            WeekHistoryItem(week)
        }
    }
}

@Composable
private fun MonthlyHistoryContent(viewModel: HistoryViewModel) {
    val monthlyStats = viewModel.monthlyStats

    LazyColumn(
        modifier = Modifier.padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(monthlyStats) { month ->
            MonthHistoryItem(month)
        }
    }
}

@Composable
private fun WeekHistoryItem(week: WeekData) {
    val startDate = DateFormatter.formatDateToString(Date(week.startDate))
    val endDate = DateFormatter.formatDateToString(Date(week.endDate))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        backgroundColor = MaterialTheme.colors.surface,
        elevation = 1.dp,
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Week ${week.week}, ${week.year}",
                style = MaterialTheme.typography.h3,
                color = MaterialTheme.colors.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "$startDate - $endDate",
                style = MaterialTheme.typography.body1,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_water_drop),
                    contentDescription = "Water",
                    tint = MaterialTheme.colors.primary,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "${week.totalAmount}ml",
                    style = MaterialTheme.typography.body1,
                    color = MaterialTheme.colors.onSurface
                )
            }
        }
    }
}

@Composable
private fun MonthHistoryItem(month: MonthData) {
    val calendar = Calendar.getInstance().apply {
        set(Calendar.YEAR, month.year)
        set(Calendar.MONTH, month.month)
    }
    val monthName = SimpleDateFormat("MMMM", Locale.getDefault()).format(calendar.time)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        backgroundColor = MaterialTheme.colors.surface,
        elevation = 1.dp,
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = monthName,
                    style = MaterialTheme.typography.h3,
                    color = MaterialTheme.colors.onSurface
                )

                Text(
                    text = "${month.year}",
                    style = MaterialTheme.typography.body1,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_water_drop),
                    contentDescription = "Water",
                    tint = MaterialTheme.colors.primary,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "${month.totalAmount}ml",
                    style = MaterialTheme.typography.body1,
                    color = MaterialTheme.colors.onSurface
                )
            }
        }
    }
}

enum class StatsPeriod {
    DAY, WEEK, MONTH
}