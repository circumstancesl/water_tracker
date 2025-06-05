package com.example.water_tracker.ui.screens.settings

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.Surface
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.water_tracker.R
import com.example.water_tracker.ui.theme.WaterTrackerTheme
import com.example.water_tracker.ui.components.DialogEditValue
import com.example.water_tracker.ui.components.Setting
import com.example.water_tracker.ui.components.SettingHeader

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val state = viewModel.state
    var showDialog by remember { mutableStateOf(false) }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            viewModel.toggleNotifications(true)
        } else {
            viewModel.toggleNotifications(false)
        }
    }


    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        SettingsPageTopBar()

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            SettingHeader(title = stringResource(id = R.string.application_settings))

            Setting(
                title = stringResource(id = R.string.settings_goal_amount),
                value = state.dailyGoals.toString(),
                onItemClicked = {
                    showDialog = !showDialog
                }
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colors.surface)
                    .padding(16.dp)
            ) {
                Text(
                    text = "Reminders to drink water",
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Enable reminders",
                        style = MaterialTheme.typography.body1,
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        checked = viewModel.notificationsEnabled.value,
                        onCheckedChange = { enabled ->
                            if (enabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            } else {
                                viewModel.toggleNotifications(enabled)
                            }
                        }
                    )
                }

                if (viewModel.notificationsEnabled.value) {
                    Column {
                        Text(
                            text = "Interval: ${viewModel.reminderInterval.value} minutes",
                            style = MaterialTheme.typography.body1,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                        Slider(
                            value = viewModel.reminderInterval.value.toFloat(),
                            onValueChange = { viewModel.setReminderInterval(it.toLong()) },
                            valueRange = 15f..120f,
                            steps = 6,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }

    if (showDialog) {
        DialogEditValue(
            title = stringResource(id = R.string.settings_edit_goal_amount),
            value = state.dailyGoals.toString(),
            onSubmit = {
                val newValue = it.toInt()
                viewModel.saveNewGoals(newValue)
                showDialog = false
            },
            onDismiss = {
                showDialog = false
            }
        )
    }
}

@Composable
fun SettingsPageTopBar() {
    Text(
        text = stringResource(id = R.string.settings),
        style = MaterialTheme.typography.h1,
        modifier = Modifier
            .fillMaxWidth()
            .padding(18.dp),
        textAlign = TextAlign.Center
    )
}

@Preview
@Composable
fun SettingsScreenPreview() {
    WaterTrackerTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            SettingsScreen()
        }
    }
}