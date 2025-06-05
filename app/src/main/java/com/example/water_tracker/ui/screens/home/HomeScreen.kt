package com.example.water_tracker.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.SnackbarResult
import androidx.compose.material.Surface
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.water_tracker.R
import com.example.water_tracker.data.models.DrinkType
import com.example.water_tracker.ui.theme.WaterTrackerTheme
import com.example.water_tracker.ui.components.CustomSnackBar
import com.example.water_tracker.ui.components.OptionCard
import com.example.water_tracker.ui.components.OptionType
import com.example.water_tracker.ui.components.PercentageProgress
import com.example.water_tracker.ui.components.WavesAnimationBox
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
) {
    val scope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }
    val state = viewModel.state
    val bottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val context = LocalContext.current

    if (state.showCustomAmountDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.toggleCustomAmountDialog(false) },
            title = { Text("Add custom amount") },
            text = {
                Column {
                    OutlinedTextField(
                        value = state.customAmount,
                        onValueChange = { viewModel.updateCustomAmount(it) },
                        label = { Text("Amount") },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text("Unit:")
                        Spacer(Modifier.width(8.dp))
                        Switch(
                            checked = state.isLiter,
                            onCheckedChange = { viewModel.toggleUnit() }
                        )
                        Text(if (state.isLiter) "Liters" else "Milliliters")
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.addCustomWater() }
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                Button(
                    onClick = { viewModel.toggleCustomAmountDialog(false) }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    DisposableEffect(lifecycleOwner) {
        val lifecycle = lifecycleOwner.lifecycle
        val observer = LifecycleEventObserver { _, e ->
            when (e) {
                Lifecycle.Event.ON_RESUME -> {
                    viewModel.initDrinkType()
                    viewModel.initData()
                }

                else -> {}
            }
        }

        lifecycle.addObserver(observer)
        onDispose { lifecycle.removeObserver(observer) }
    }

    ModalBottomSheetLayout(
        sheetState = bottomSheetState,
        sheetShape = MaterialTheme.shapes.medium.copy(
            bottomStart = CornerSize(0.dp),
            bottomEnd = CornerSize(0.dp)
        ),
        sheetContent = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                BottomSheetContent(
                    drinkTypes = state.drinkTypes.orEmpty(),
                    onOptionClicked = {
                        viewModel.addWater(it.amount)
                        scope.launch {
                            bottomSheetState.hide()
                            snackBarHostState.currentSnackbarData?.dismiss()
                            val result = snackBarHostState.showSnackbar(
                                message = context.getString(R.string.water_added, it.name),
                                duration = SnackbarDuration.Short,
                                actionLabel = context.getString(R.string.undo)
                            )

                            when (result) {
                                SnackbarResult.ActionPerformed -> {
                                    viewModel.reduceWater(it.amount)
                                }

                                SnackbarResult.Dismissed -> {
                                    snackBarHostState.currentSnackbarData?.dismiss()
                                }
                            }
                        }
                    }
                )
            }
        }
    ) {
        MainScreen(
            onOptionClicked = {
                viewModel.addWater(it.amount)
                scope.launch {
                    snackBarHostState.currentSnackbarData?.dismiss()
                    val result = snackBarHostState.showSnackbar(
                        message = context.getString(R.string.water_added, it.name),
                        duration = SnackbarDuration.Short,
                        actionLabel = context.getString(R.string.undo)
                    )

                    when (result) {
                        SnackbarResult.ActionPerformed -> {
                            viewModel.reduceWater(it.amount)
                        }

                        SnackbarResult.Dismissed -> {
                            snackBarHostState.currentSnackbarData?.dismiss()
                        }
                    }
                }
            },
            onCustomAmountClicked = { viewModel.toggleCustomAmountDialog(true) },
            state = state,
            snackBarHostState = snackBarHostState
        )
    }
}

@Composable
fun BottomSheetContent(
    drinkTypes: List<DrinkType>,
    onOptionClicked: (DrinkType) -> Unit
) {
    Text(text = stringResource(R.string.all_option))
    LazyVerticalGrid(
        columns = GridCells.Fixed(count = 4),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = drinkTypes,
            key = {
                it.name
            }
        ) {
            OptionCard(
                onCardClicked = { onOptionClicked(it) },
                title = it.name,
                icon = it.icon,
                type = OptionType.COMMON_OPTION,
                modifier = Modifier.height(75.dp)
            )
        }
    }
}

@Composable
fun MainScreen(
    state: HomeState,
    snackBarHostState: SnackbarHostState,
    onOptionClicked: (DrinkType) -> Unit,
    onCustomAmountClicked: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(
            modifier = Modifier
        ) {
            Text(
                text = stringResource(
                    id = R.string.current_amount,
                    state.history?.totalAmount ?: 0,
                    state.totalAmount
                ),
                style = MaterialTheme.typography.body1.copy(
                    color = MaterialTheme.colors.onBackground
                )
            )
        }
    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        WavesAnimationBox(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.primaryVariant,
            progress = state.percentage
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = R.string.daily_balance),
                    style = MaterialTheme.typography.h1
                )
                Spacer(modifier = Modifier.height(18.dp))
                PercentageProgress(value = (state.percentage * 100).toInt())
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(
                        id = R.string.current_amount,
                        state.history?.totalAmount ?: 0,
                        state.totalAmount
                    ),
                    style = MaterialTheme.typography.body1.copy(
                        color = MaterialTheme.colors.onBackground
                    )
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                CustomSnackBar(state = snackBarHostState)
                OptionList(
                    drinkTypes = state.drinkTypes?.take(3),
                    onOptionClicked = { onOptionClicked(it) },
                )

                Button(
                    onClick = onCustomAmountClicked,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text("Add Custom Amount")
                }
            }
        }
    }
}

@Composable
private fun OptionList(
    drinkTypes: List<DrinkType>?,
    onOptionClicked: (DrinkType) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(75.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        drinkTypes?.forEach { type ->
            OptionCard(
                onCardClicked = {
                    onOptionClicked(type)
                },
                title = type.name,
                icon = type.icon,
                type = OptionType.COMMON_OPTION,
                modifier = Modifier.weight(2F)
            )
        }
    }
}

@ExperimentalMaterialApi
@Preview
@Composable
fun HomeScreenPreview() {
    WaterTrackerTheme {
        Surface(
            modifier = Modifier
                .fillMaxSize()
        ) {
            HomeScreen()
        }
    }
}