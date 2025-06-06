package com.example.water_tracker.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.SnackbarResult
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.example.water_tracker.ui.components.*
import com.example.water_tracker.ui.theme.WaterTrackerTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
) {
    val snackBarHostState = remember { SnackbarHostState() }
    val bottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val state = viewModel.state

    if (state.showCustomAmountDialog) {
        CustomAmountAlertDialog(
            customAmount = state.customAmount,
            isLiter = state.isLiter,
            onAmountChange = viewModel::updateCustomAmount,
            onToggleUnit = viewModel::toggleUnit,
            onAdd = viewModel::addCustomWater,
            onDismiss = { viewModel.toggleCustomAmountDialog(false) }
        )
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.initDrinkType()
                viewModel.initData()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    ModalBottomSheetLayout(
        sheetState = bottomSheetState,
        sheetShape = MaterialTheme.shapes.medium.copy(
            bottomStart = CornerSize(0.dp),
            bottomEnd = CornerSize(0.dp)
        ),
        sheetContent = {
            BottomSheetContent(
                drinkTypes = state.drinkTypes.orEmpty(),
                onOptionClicked = { drinkType ->
                    handleWaterAddition(
                        viewModel = viewModel,
                        snackBarHostState = snackBarHostState,
                        bottomSheetState = bottomSheetState,
                        scope = scope,
                        context = context,
                        drinkType = drinkType
                    )
                }
            )
        }
    ) {
        MainScreen(
            state = state,
            snackBarHostState = snackBarHostState,
            onOptionClicked = { drinkType ->
                scope.launch {
                    viewModel.addWater(drinkType.amount)
                    showUndoSnackbar(snackBarHostState, context, drinkType, viewModel)
                }
            },
            onCustomAmountClicked = { viewModel.toggleCustomAmountDialog(true) }
        )
    }
}

@Composable
private fun CustomAmountAlertDialog(
    customAmount: String,
    isLiter: Boolean,
    onAmountChange: (String) -> Unit,
    onToggleUnit: () -> Unit,
    onAdd: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Add custom amount") },
        text = {
            Column {
                OutlinedTextField(
                    value = customAmount,
                    onValueChange = onAmountChange,
                    label = { Text("Amount") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text("Unit:")
                    Spacer(Modifier.width(8.dp))
                    Switch(checked = isLiter, onCheckedChange = { onToggleUnit() })
                    Text(text = if (isLiter) "Liters" else "Milliliters")
                }
            }
        },
        confirmButton = {
            Button(onClick = onAdd) {
                Text("Add")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun BottomSheetContent(
    drinkTypes: List<DrinkType>,
    onOptionClicked: (DrinkType) -> Unit
) {
    Text(text = stringResource(R.string.all_option))
    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(drinkTypes, key = { it.name }) { drinkType ->
            OptionCard(
                onCardClicked = { onOptionClicked(drinkType) },
                title = drinkType.name,
                icon = drinkType.icon,
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
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(R.string.daily_balance),
                    style = MaterialTheme.typography.h1
                )
                Spacer(Modifier.height(18.dp))
                PercentageProgress((state.percentage * 100).toInt())
                Spacer(Modifier.height(8.dp))
                Text(
                    text = stringResource(
                        R.string.current_amount,
                        state.history?.totalAmount ?: 0,
                        state.totalAmount
                    ),
                    style = MaterialTheme.typography.body1.copy(color = MaterialTheme.colors.onBackground)
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                CustomSnackBar(state = snackBarHostState)
                OptionList(drinkTypes = state.drinkTypes?.take(3), onOptionClicked = onOptionClicked)
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
                onCardClicked = { onOptionClicked(type) },
                title = type.name,
                icon = type.icon,
                type = OptionType.COMMON_OPTION,
                modifier = Modifier.weight(2f)
            )
        }
    }
}

private suspend fun showUndoSnackbar(
    snackBarHostState: SnackbarHostState,
    context: android.content.Context,
    drinkType: DrinkType,
    viewModel: HomeViewModel
) {
    val result = snackBarHostState.showSnackbar(
        message = context.getString(R.string.water_added, drinkType.name),
        duration = SnackbarDuration.Short,
        actionLabel = context.getString(R.string.undo)
    )

    if (result == SnackbarResult.ActionPerformed) {
        viewModel.reduceWater(drinkType.amount)
    }
}

private fun handleWaterAddition(
    viewModel: HomeViewModel,
    snackBarHostState: SnackbarHostState,
    bottomSheetState: ModalBottomSheetState,
    scope: CoroutineScope,
    context: android.content.Context,
    drinkType: DrinkType,
) {
    scope.launch {
        viewModel.addWater(drinkType.amount)
        bottomSheetState.hide()
        snackBarHostState.currentSnackbarData?.dismiss()
        showUndoSnackbar(snackBarHostState, context, drinkType, viewModel)
    }
}

@ExperimentalMaterialApi
@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    WaterTrackerTheme {
        Surface(Modifier.fillMaxSize()) {
            HomeScreen()
        }
    }
}