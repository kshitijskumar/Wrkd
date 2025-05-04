package org.example.project.wrkd.track.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.example.project.wrkd.core.models.app.ExercisePlanInfoAppModel
import org.example.project.wrkd.core.models.app.ExerciseResistanceMethod
import org.example.project.wrkd.core.models.app.ExerciseSetInfoAppModel
import org.example.project.wrkd.core.models.app.WeightInGrams
import org.example.project.wrkd.core.models.app.displayString
import org.example.project.wrkd.core.models.app.resistanceMethodName
import org.example.project.wrkd.core.ui.compose.AppButton
import org.example.project.wrkd.core.ui.compose.AppSecondaryButton
import org.example.project.wrkd.core.ui.compose.AppTextField
import org.example.project.wrkd.core.ui.compose.AppTextFieldPlaceholder
import org.example.project.wrkd.core.ui.compose.AppTextFieldTrailingText
import org.example.project.wrkd.core.ui.compose.AppTextFieldWithTrailingContent
import org.example.project.wrkd.core.ui.compose.AppTheme
import org.example.project.wrkd.core.ui.compose.AppTimerButton
import org.example.project.wrkd.core.ui.compose.ConfirmationDialog
import org.example.project.wrkd.utils.TimeFormattingStringUtils
import org.jetbrains.compose.resources.painterResource
import wrkd.composeapp.generated.resources.Res
import wrkd.composeapp.generated.resources.ic_drop_down

@Composable
fun WorkoutTrackerScreen(
    vm: WorkoutTrackerViewModel
) {

    val state by vm.state.collectAsStateWithLifecycle(LocalLifecycleOwner.current)
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.error) {
        val error = state.error
        if (error != null) {
            snackbarHostState.showSnackbar(message = error)
        } else {
            snackbarHostState.currentSnackbarData?.dismiss()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AppTheme.color.primaryBeige)
        ) {
            val exercises = state.exercises
            when {
                exercises == null -> {}
                exercises.isEmpty() -> {
                    EmptyWorkoutScreen(
                        addExerciseClicked = {
                            vm.processIntent(WorkoutTrackerIntent.AddExerciseIntent)
                        }
                    )
                }
                else -> {
                    WorkTrackingScreen(
                        exercises = exercises,
                        isExerciseAdditionAllowed = state.isExerciseAdditionAllowed,
                        restTimer = state.restTimer,
                        error = state.error,
                        sendIntent = vm::processIntent,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }

    state.dialogType?.let {
        WorkoutTrackerDialogHandling(
            dialogTypes = it,
            sendIntent = vm::processIntent
        )
    }

}

@Composable
fun WorkoutTrackerDialogHandling(
    dialogTypes: WorkoutTrackerDialogTypes,
    sendIntent: (WorkoutTrackerIntent) -> Unit
) {
    when(dialogTypes) {
        WorkoutTrackerDialogTypes.ConfirmationDialog -> {
            ConfirmationDialog(
                title = "Finish And Save Today's workout?",
                message = null,
                onConfirm = {
                    sendIntent.invoke(WorkoutTrackerIntent.CompleteWorkoutIntent(true))
                },
                onCancel = {
                    sendIntent.invoke(WorkoutTrackerIntent.DismissDialogIntent)
                },
                onDismissRequest = {
                    sendIntent.invoke(WorkoutTrackerIntent.DismissDialogIntent)
                }
            )
        }
        WorkoutTrackerDialogTypes.IncompleteWorkoutDetailsDialog -> {
            ConfirmationDialog(
                title = "Workout Information Missing..",
                message = """
                    Few of the information about today's work is missing or filled wrong. Review them once to finish the workout.
                """.trimIndent(),
                confirmText = "OK",
                cancelText = null,
                onConfirm = {
                    sendIntent.invoke(WorkoutTrackerIntent.DismissDialogIntent)
                },
                onCancel = null,
                onDismissRequest = {
                    sendIntent.invoke(WorkoutTrackerIntent.DismissDialogIntent)
                }
            )
        }
    }
}

@Composable
fun WorkTrackingScreen(
    exercises: List<ExercisePlanInfoAppModel>,
    isExerciseAdditionAllowed: Boolean,
    restTimer: Long?,
    error: String?,
    sendIntent: (WorkoutTrackerIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            itemsIndexed(exercises) { index, item ->
                Column {
                    WorkoutCard(
                        exercise = item,
                        onExerciseNameChange = { name ->
                            sendIntent.invoke(
                                WorkoutTrackerIntent.EnterExerciseNameIntent(
                                    exerciseId = item.exerciseId,
                                    name = name
                                )
                            )
                        },
                        repsCountChange = { setId, repsCount ->
                            sendIntent.invoke(
                                WorkoutTrackerIntent.AddRepsCountIntent(
                                    exerciseId = item.exerciseId,
                                    setId = setId,
                                    count = repsCount
                                )
                            )
                        },
                        resistanceMethodChange = { setId, method ->
                            sendIntent.invoke(
                                WorkoutTrackerIntent.ChangeResistanceMethodIntent(
                                    exerciseId = item.exerciseId,
                                    setId = setId,
                                    resistanceMethod = method
                                )
                            )
                        },
                        additionalWeightChange = { setId, weight ->
                            sendIntent.invoke(
                                WorkoutTrackerIntent.WeightChangeIntent(
                                    exerciseId = item.exerciseId,
                                    setId = setId,
                                    weightEntered = weight
                                )
                            )
                        },
                        addSet = {
                            sendIntent.invoke(
                                WorkoutTrackerIntent.AddSetIntent(item.exerciseId)
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = AppTheme.dimens.medium1)
                    )

                    if (index != exercises.lastIndex) {
                        Spacer(Modifier.height(AppTheme.dimens.medium1))
                    }
                }
            }

            item {
                AddExerciseItem(
                    isAddExerciseAllowed = isExerciseAdditionAllowed,
                    onAddClicked = {
                        sendIntent.invoke(WorkoutTrackerIntent.AddExerciseIntent)
                    },
                    modifier = Modifier
                        .padding(
                            horizontal = AppTheme.dimens.medium1,
                            vertical = AppTheme.dimens.medium2
                        )
                )
            }
        }

        if (restTimer != null) {
            WorkoutRestTimer(
                duration = restTimer,
                onStopRestClicked = {
                    sendIntent.invoke(WorkoutTrackerIntent.ToggleRestTimerIntent)
                },
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            WorkoutTrackingActionButtons(
                restClicked = {
                    sendIntent.invoke(WorkoutTrackerIntent.ToggleRestTimerIntent)
                },
                completeClicked = {
                    sendIntent.invoke(WorkoutTrackerIntent.CompleteWorkoutIntent(false))
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun WorkoutRestTimer(
    duration: Long,
    onStopRestClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(color = AppTheme.color.primaryRed)
            .clickable(onClick = onStopRestClicked)
            .padding(
                horizontal = AppTheme.dimens.medium1,
                vertical = AppTheme.dimens.medium2
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "Resting for",
                modifier = Modifier,
                style = AppTheme.typography.subtitle1,
                color = AppTheme.color.white
            )

            Spacer(Modifier.height(AppTheme.dimens.small2))

            Text(
                text = TimeFormattingStringUtils.convertMillisElapsedToString(duration),
                modifier = Modifier,
                style = AppTheme.typography.h6,
                color = AppTheme.color.white
            )
        }

        Text(
            text = "STOP",
            color = AppTheme.color.white,
            style = AppTheme.typography.subtitle2
        )
    }
}

@Composable
fun AddExerciseItem(
    isAddExerciseAllowed: Boolean,
    onAddClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        if (isAddExerciseAllowed) {
            AppButton(
                text = "Add Exercise",
                onClick = onAddClicked,
                enabled = true,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = AppTheme.color.primaryLightBlue
                ),
                textColor = AppTheme.color.white,
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            Text(
                text = "To add more exercises, please specify details of the ones already added",
                textAlign = TextAlign.Center,
                color = AppTheme.color.black60,
                style = AppTheme.typography.subtitle2,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AppTheme.dimens.medium1)
            )
        }
    }
}

@Composable
fun WorkoutTrackingActionButtons(
    restClicked: () -> Unit,
    completeClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = AppTheme.dimens.medium1,
                vertical = AppTheme.dimens.medium1
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AppTimerButton(
            text = "Rest",
            onClick = restClicked,
            modifier = Modifier.weight(1f)
        )

        Spacer(Modifier.width(AppTheme.dimens.medium1))

        AppButton(
            text = "Finish",
            onClick = completeClicked,
            enabled = true,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = AppTheme.color.primaryDarkBlue
            ),
            textColor = AppTheme.color.white,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun EmptyWorkoutScreen(
    addExerciseClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .clickable(onClick = addExerciseClicked),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Click to start logging your workout \n\uD83C\uDFCB\uFE0F\u200D♂\uFE0F",
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppTheme.dimens.medium2),
            color = AppTheme.color.primaryLightBlue,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun WorkoutCard(
    exercise: ExercisePlanInfoAppModel,
    onExerciseNameChange: (String) -> Unit,
    repsCountChange: (String, Int) -> Unit,
    resistanceMethodChange: (String, ExerciseResistanceMethod) -> Unit,
    additionalWeightChange: (String, Double) -> Unit,
    addSet: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(AppTheme.corners.mainCard)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = AppTheme.dimens.medium1)
        ) {
            AppTextField(
                value = exercise.name,
                onValueEntered = onExerciseNameChange,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words
                ),
                placeholder = {
                    AppTextFieldPlaceholder("Exercise...")
                }
            )

            Spacer(Modifier.height(AppTheme.dimens.medium1))

            SetsList(
                sets = exercise.sets,
                onRepCountChange = repsCountChange,
                onResistanceMethodChange = resistanceMethodChange,
                additionalWeightChange = additionalWeightChange
            )

            AppSecondaryButton(
                text = "Add Set ➕",
                onClick = addSet,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun ColumnScope.SetsList(
    sets: List<ExerciseSetInfoAppModel>,
    onRepCountChange: (String, Int) -> Unit,
    onResistanceMethodChange: (String, ExerciseResistanceMethod) -> Unit,
    additionalWeightChange: (String, Double) -> Unit,
) {
    sets.forEachIndexed { index, set ->
        SetCard(
            setNumber = index + 1,
            set = set,
            isLastSet = index == sets.lastIndex,
            onRepCountChange = {
                onRepCountChange.invoke(set.setId, it)
            },
            onResistanceMethodChange = {
                onResistanceMethodChange.invoke(set.setId, it)
            },
            additionalWeightChange = {
                additionalWeightChange.invoke(set.setId, it)
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun SetCard(
    setNumber: Int,
    set: ExerciseSetInfoAppModel,
    isLastSet: Boolean,
    onRepCountChange: (Int) -> Unit,
    onResistanceMethodChange: (ExerciseResistanceMethod) -> Unit,
    additionalWeightChange: (Double) -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize()
            .padding(
                horizontal = AppTheme.dimens.small3
            )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Set $setNumber",
                style = AppTheme.typography.subtitle1,
                color = AppTheme.color.black,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "x",
                style = AppTheme.typography.subtitle1,
                color = AppTheme.color.black,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .padding(horizontal = AppTheme.dimens.medium2)
            )

            SetRepsCountInput(
                repCount = set.repsCount,
                isExpanded = isExpanded,
                onRepCountChange = onRepCountChange,
                onExpandClicked = {
                    isExpanded = !isExpanded
                }
            )
        }

        if (isExpanded) {
            Spacer(Modifier.height(AppTheme.dimens.medium1))

            ExerciseResistanceMethodOption(
                resistanceMethodOptions = ExerciseResistanceMethod.entries,
                selectedResistanceMethod = set.resistanceMethod,
                additionalWeight = set.additionalWeight,
                onSelection = onResistanceMethodChange,
                onAdditionalWeightChange = additionalWeightChange,
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (!isLastSet) {
            Spacer(Modifier.height(AppTheme.dimens.small3))

            Divider(
                modifier = Modifier.fillMaxWidth(),
                color = AppTheme.color.lightGrey,
                thickness = AppTheme.dimens.small1
            )
        }

        Spacer(Modifier.height(AppTheme.dimens.small3))
    }
}

@Composable
fun ExerciseResistanceMethodOption(
    resistanceMethodOptions: List<ExerciseResistanceMethod>,
    selectedResistanceMethod: ExerciseResistanceMethod,
    additionalWeight: WeightInGrams,
    onSelection: (ExerciseResistanceMethod) -> Unit,
    onAdditionalWeightChange: (Double) -> Unit,
    modifier: Modifier = Modifier
) {

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Bottom
    ) {
        resistanceMethodOptions.forEachIndexed { index, method ->
            val isSelected = method == selectedResistanceMethod

            ResistanceMethodOption(
                option = method,
                isSelected = isSelected,
                onClick = {
                    onSelection.invoke(method)
                },
                modifier = Modifier
                    .padding(
                        start = if (index != 0) AppTheme.dimens.small2 else 0.dp,
                        end = if (index != resistanceMethodOptions.lastIndex) AppTheme.dimens.small2 else 0.dp
                    )
                    .weight(1f)
            )
        }
    }

    Spacer(Modifier.height(AppTheme.dimens.medium1))

    AppTextFieldWithTrailingContent(
        value = additionalWeight.displayString(),
        textChangeFilter = {
            it.filter { char -> char.isDigit() || char == '.' }
        },
        trailingContent = {
            AppTextFieldTrailingText(
                text = "Kgs"
            )
        },
        onValueEntered = {
            val weight = it.toDoubleOrNull() ?: return@AppTextFieldWithTrailingContent
            onAdditionalWeightChange.invoke(weight)
        },
        placeholder = {
            AppTextFieldPlaceholder("Additional weight \uD83D\uDCAA")
        },
        modifier = Modifier
            .fillMaxWidth()
    )
}

@Composable
fun ResistanceMethodOption(
    option: ExerciseResistanceMethod,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = remember { RoundedCornerShape(AppTheme.corners.selectable) }
    Row(
        modifier = modifier
            .background(
                color = if (isSelected) AppTheme.color.primaryLightBlue else Color.Transparent,
                shape = shape
            )
            .clip(shape)
            .run {
                if (isSelected) {
                    this
                } else {
                    border(
                        width = AppTheme.dimens.small1,
                        color = AppTheme.color.primaryLightBlue,
                        shape = shape
                    )
                }
            }
            .clickable(onClick = onClick)
            .padding(
                horizontal = AppTheme.dimens.small4,
                vertical = AppTheme.dimens.small3
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = option.resistanceMethodName,
            color = if (isSelected) AppTheme.color.white else AppTheme.color.primaryLightBlue,
            maxLines = 1,
            textAlign = TextAlign.Center,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier,
            style = AppTheme.typography.subtitle1,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
fun SetRepsCountInput(
    repCount: Int,
    isExpanded: Boolean,
    onRepCountChange: (Int) -> Unit,
    onExpandClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rotationDegrees by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f
    )
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        AppTextFieldWithTrailingContent(
            value = repCount.toString(),
            trailingContent = {
                AppTextFieldTrailingText(
                    text = "Reps"
                )
            },
            onValueEntered = {
                val reps = it.toIntOrNull() ?: return@AppTextFieldWithTrailingContent
                onRepCountChange.invoke(reps)
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number
            ),
            textChangeFilter = {
                it.filter { char -> char.isDigit() }
            },
            modifier = Modifier.weight(1f)
        )

        IconButton(
            onClick = onExpandClicked,
            modifier = Modifier.padding(AppTheme.dimens.small3),
            content = {
                Icon(
                    painter = painterResource(Res.drawable.ic_drop_down),
                    contentDescription = null,
                    modifier = Modifier
                        .size(AppTheme.dimens.medium1)
                        .rotate(rotationDegrees)
                )
            }
        )
    }
}




