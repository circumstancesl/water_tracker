package com.example.water_tracker

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomAppBar
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import androidx.work.WorkManager
import com.example.water_tracker.navigation.AppNavGraph
import com.example.water_tracker.ui.theme.WaterTrackerTheme
import com.example.water_tracker.ui.components.AppBottomNav
import com.example.water_tracker.ui.screens.settings.SettingsViewModel
import com.example.water_tracker.utils.Constants.NOTIFICATION_PERMISSION_REQUEST_CODE
import com.example.water_tracker.utils.WorkerHelper
import com.example.water_tracker.worker.HistoryAddWorker
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
@ExperimentalMaterialApi
class MainActivity : ComponentActivity() {
    private val viewModel: SettingsViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()

        setContent {
            WaterTrackerTheme {
                val navController = rememberNavController()

                LaunchedEffect(true) {
                    initDatabaseWorker()
                }

                Scaffold(
                    bottomBar = {
                        BottomAppBar(
                            backgroundColor = MaterialTheme.colors.primaryVariant,
                            elevation = 0.dp
                        ) {
                            AppBottomNav(navController = navController)
                        }
                    },
                    content = {
                        Surface(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(it)
                        ) {
                            AppNavGraph(navController = navController)
                        }
                    }
                )
            }
        }
    }

    /**
     * Create a function for scheduling create database every night
     * at 00:00:00 am
     */
    private fun initDatabaseWorker() {
        if (WorkManager.getInstance(this)
                .getWorkInfosForUniqueWork(HistoryAddWorker.UNIQUE_WORKER_NAME).get().isEmpty()
        ) {
            WorkerHelper.createHistoryDrinkWorker(
                context = applicationContext,
                scheduleType = WorkerHelper.ScheduleType.NOW
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            NOTIFICATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    viewModel.toggleNotifications(true)
                } else {
                    showPermissionDeniedMessage()
                }
            }
        }
    }

    private fun showPermissionDeniedMessage() {
        AlertDialog.Builder(this)
            .setTitle("Permission required")
            .setMessage("Notifications permission is required for reminders to work.")
            .setPositiveButton("Settings") { _, _ ->
                openAppSettings()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
        }
        startActivity(intent)
    }
}