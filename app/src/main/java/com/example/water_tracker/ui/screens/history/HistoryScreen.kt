package com.example.water_tracker.ui.screens.history

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.primarySurface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.example.water_tracker.theme.HydrationTrackerTheme
import com.example.water_tracker.ui.components.ContextMenuDialog
import com.example.water_tracker.ui.components.DeleteConfirmationDialog
import com.example.water_tracker.ui.components.DialogEditValue
import com.example.water_tracker.ui.components.HistoryItem
import com.example.water_tracker.utils.DateFormatter
import com.example.water_tracker.utils.DateFormatter.formatDateToString
import java.util.Date

@Composable
fun HistoryScreen(
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val state = viewModel.state.value

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("History") },
                backgroundColor = MaterialTheme.colors.primarySurface
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            LazyColumn(modifier = Modifier.padding(horizontal = 8.dp)) {
                items(state.histories) { history ->
                    HistoryItem(
                        date = DateFormatter.formatDateToString(Date(history.date)),
                        value = history.totalAmount.toString(),
                        onLongClick = { viewModel.selectHistory(history) }
                    )
                }
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
fun HistoryPageTopBar() {
    Text(
        text = "History",
        style = MaterialTheme.typography.h1,
        modifier = Modifier
            .fillMaxWidth()
            .padding(18.dp),
        textAlign = TextAlign.Center
    )
}

@Preview
@Composable
fun HistoryScreenPreview() {
    HydrationTrackerTheme {
        Surface(
            modifier = Modifier
                .fillMaxSize()
        ) {
            HistoryScreen()
        }
    }
}

@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun HistoryScreenPreviewDarkMode() {
    HydrationTrackerTheme {
        Surface(
            modifier = Modifier
                .fillMaxSize()
        ) {
            HistoryScreen()
        }
    }
}

