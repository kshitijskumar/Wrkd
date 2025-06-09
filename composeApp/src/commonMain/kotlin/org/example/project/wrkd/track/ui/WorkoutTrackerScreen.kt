package org.example.project.wrkd.track.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.example.project.wrkd.core.models.WeekDay
import org.example.project.wrkd.core.models.app.ExercisePlanInfoAppModel
import org.example.project.wrkd.core.models.app.ExerciseSetInfoAppModel
import org.example.project.wrkd.core.models.app.displayString
import org.example.project.wrkd.core.models.dayName
import org.example.project.wrkd.core.ui.compose.AppButton
import org.example.project.wrkd.core.ui.compose.AppPrimaryButton
import org.example.project.wrkd.core.ui.compose.AppPrimaryHollowButton
import org.example.project.wrkd.core.ui.compose.AppSecondaryButton
import org.example.project.wrkd.core.ui.compose.AppTextField
import org.example.project.wrkd.core.ui.compose.AppTextFieldPlaceholder
import org.example.project.wrkd.core.ui.compose.AppTheme
import org.example.project.wrkd.core.ui.compose.AppTimerButton
import org.example.project.wrkd.core.ui.compose.ConfirmationDialog
import org.example.project.wrkd.core.ui.compose.HorizontalButtons
import org.example.project.wrkd.utils.TimeFormattingStringUtils
import org.jetbrains.compose.resources.painterResource
import wrkd.composeapp.generated.resources.Res
import wrkd.composeapp.generated.resources.*

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun WorkoutTrackerScreen(
    vm: WorkoutTrackerViewModel
) {

    val state by vm.state.collectAsStateWithLifecycle(LocalLifecycleOwner.current)
    val snackbarHostState = remember { SnackbarHostState() }

    val bottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )

    LaunchedEffect(state.error) {
        val error = state.error
        if (error != null) {
            snackbarHostState.showSnackbar(message = error)
        } else {
            snackbarHostState.currentSnackbarData?.dismiss()
        }
    }

    LaunchedEffect(state.bottomSheetType) {
        if (state.bottomSheetType != null) {
            bottomSheetState.show()
        } else {
            bottomSheetState.hide()
        }
    }

    ModalBottomSheetLayout(
        sheetState = bottomSheetState,
        sheetGesturesEnabled = false,
        sheetElevation = AppTheme.dimens.small2,
        sheetContent = {
            when(val bsState = state.bottomSheetType) {
                is WorkoutTrackerBottomSheetType.ExerciseDetails -> {
                    WorkoutExerciseDetailBottomSheetContent(
                        state = bsState,
                        restTimer = state.restTimer,
                        sendIntent = vm::processIntent
                    )
                }
                null -> {}
            }
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(AppTheme.color.white)
            ) {
                when(val screenState = state.screenState) {
                    is WorkoutTrackerScreenState.StartScreen -> {
                        WorkoutStartScreenContent(
                            week = state.week,
                            state = screenState,
                            sendIntent = vm::processIntent
                        )
                    }
                    is WorkoutTrackerScreenState.TrackerScreen -> {
                        val exercises = screenState.exercises
                        when {
                            exercises == null -> {}
                            else -> {
                                WorkTrackingScreen(
                                    exercises = exercises,
                                    isExerciseAdditionAllowed = screenState.isExerciseAdditionAllowed,
                                    restTimer = state.restTimer,
                                    error = state.error,
                                    sendIntent = vm::processIntent,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }
                    null -> {}
                }

            }
        }
    )

    state.dialogType?.let {
        WorkoutTrackerDialogHandling(
            dialogTypes = it,
            sendIntent = vm::processIntent
        )
    }

    BackHandler {
        vm.processIntent(WorkoutTrackerIntent.BackClickedIntent)
    }

}

@Composable
fun WorkoutExerciseDetailBottomSheetContent(
    state: WorkoutTrackerBottomSheetType.ExerciseDetails,
    restTimer: Long?,
    sendIntent: (WorkoutTrackerIntent) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = AppTheme.dimens.medium1)
                .padding(top = AppTheme.dimens.medium1)
        ) {
            WorkoutExerciseDetailBottomSheetHeader(
                backClicked = {
                    sendIntent.invoke(WorkoutTrackerIntent.BackClickedIntent)
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(AppTheme.dimens.medium1))

            AppTextField(
                value = state.name,
                onValueEntered = {
                    sendIntent.invoke(WorkoutTrackerIntent.EnterExerciseNameIntent(it))
                },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words
                ),
                placeholder = {
                    AppTextFieldPlaceholder("Exercise...")
                }
            )

            Spacer(Modifier.height(AppTheme.dimens.medium1))

            SetGridComponent(
                setsList = state.sets,
                sendIntent = sendIntent,
                modifier = Modifier
                    .fillMaxWidth()
            )

            Spacer(Modifier.height(AppTheme.dimens.medium3))

            AppSecondaryButton(
                text = "Add set",
                onClick = {
                    sendIntent.invoke(WorkoutTrackerIntent.AddSetIntent)
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(AppTheme.dimens.small3))
        }

        if (restTimer == null) {
            HorizontalButtons(
                primaryBtnText = "Submit",
                primaryBtnClick = { sendIntent.invoke(WorkoutTrackerIntent.SubmitExerciseIntent) },
                primaryEnabled = state.shouldEnableSubmitBtn,
                secondaryBtnText = "Rest",
                secondaryBtnClick = { sendIntent.invoke(WorkoutTrackerIntent.ToggleRestTimerIntent) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AppTheme.dimens.medium1)
            )

            Spacer(Modifier.height(AppTheme.dimens.small3))
        } else {
            WorkoutRestTimer(
                duration = restTimer,
                onStopRestClicked = {
                    sendIntent.invoke(WorkoutTrackerIntent.ToggleRestTimerIntent)
                },
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    }
}

@Composable
fun WorkoutExerciseDetailBottomSheetHeader(
    backClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Exercise details",
            style = AppTheme.typography.h5,
            fontWeight = FontWeight.Bold,
            color = AppTheme.color.black87,
            modifier = Modifier.weight(1f)
        )

        Icon(
            painter = painterResource(Res.drawable.ic_cross_in_circle),
            contentDescription = null,
            modifier = Modifier
                .size(AppTheme.dimens.medium2)
                .clickable(
                    interactionSource = null,
                    indication = null,
                    onClick = backClicked
                )
        )
    }
}

@Composable
fun SetGridComponent(
    setsList: List<ExerciseSetInfoAppModel>,
    sendIntent: (WorkoutTrackerIntent) -> Unit,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier.fillMaxWidth(),
    ) {

        SetGridHeader()
        setsList.forEachIndexed { index, set ->
            SetInfoComponent(
                setNumber = index + 1,
                set = set,
                onRepsChange = {
                    sendIntent.invoke(
                        WorkoutTrackerIntent.AddRepsCountIntent(
                            setId = set.setId,
                            count = it
                        )
                    )
                },
                onWeightChange = {
                    sendIntent.invoke(
                        WorkoutTrackerIntent.WeightChangeIntent(
                            setId = set.setId,
                            weightEntered = it
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                deleteSet = {
                    sendIntent.invoke(
                        WorkoutTrackerIntent.RemoveSetIntent(it)
                    )
                }
            )
        }
    }
}

@Composable
fun SetGridHeader(
    modifier: Modifier = Modifier
) {

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = AppTheme.dimens.small3,
                vertical = AppTheme.dimens.small3
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Set",
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = AppTheme.color.black87,
            style = AppTheme.typography.subtitle1,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(Modifier.width(AppTheme.dimens.small3))

        Text(
            text = "Reps",
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = AppTheme.color.black87,
            style = AppTheme.typography.subtitle1,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(Modifier.width(AppTheme.dimens.small3))

        Text(
            text = "Weight (kgs)",
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = AppTheme.color.black87,
            style = AppTheme.typography.subtitle1,
            fontWeight = FontWeight.SemiBold
        )
    }

}

@Composable
fun SetInfoComponent(
    setNumber: Int,
    set: ExerciseSetInfoAppModel,
    onRepsChange: (Int) -> Unit,
    onWeightChange: (Double) -> Unit,
    deleteSet: (setId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(vertical = AppTheme.dimens.small2),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AppTextField(
            value = "Set $setNumber",
            onValueEntered = {},
            readOnly = true,
            modifier = Modifier.weight(1f)
        )

        Spacer(Modifier.width(AppTheme.dimens.small3))

        AppTextField(
            value = set.repsCount.toString(),
            onValueEntered = {
                it.toIntOrNull()?.let(onRepsChange)
            },
            textChangeFilter = {
                it.filter { char -> char.isDigit() }
            },
            modifier = Modifier.weight(1f),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number
            )
        )

        Spacer(Modifier.width(AppTheme.dimens.small3))

        AppTextField(
            value = set.additionalWeight.displayString(),
            onValueEntered = {
                it.toDoubleOrNull()?.let(onWeightChange)
            },
            textChangeFilter = {
                it.filter { char -> (char.isDigit() || char == '.') }
            },
            modifier = Modifier.weight(1f),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number
            ),
            placeholder = {
                AppTextFieldPlaceholder(text = "0")
            }
        )

        Spacer(Modifier.width(AppTheme.dimens.small3))

        Icon(
            painter = painterResource(Res.drawable.ic_cross_in_circle),
            contentDescription = null,
            modifier = Modifier
                .size(AppTheme.dimens.medium2)
                .clickable(
                    interactionSource = null,
                    indication = null,
                    onClick = { deleteSet.invoke(set.setId) }
                ),
            tint = AppTheme.color.primaryRed
        )
    }
}

fun LazyGridScope.setsInfoContent(
    setNumber: Int,
    set: ExerciseSetInfoAppModel,
    onRepsChange: (Int) -> Unit,
    onWeightChange: (Double) -> Unit,
) {
    item {
        AppTextField(
            value = "Set $setNumber",
            onValueEntered = {},
            readOnly = true,
            modifier = Modifier.fillMaxWidth()
        )
    }

    item {
        AppTextField(
            value = set.repsCount.toString(),
            onValueEntered = {
                it.toIntOrNull()?.let(onRepsChange)
            },
            textChangeFilter = {
                it.filter { char -> char.isDigit() }
            },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number
            )
        )
    }

    item {
        AppTextField(
            value = set.additionalWeight.displayString(),
            onValueEntered = {
                it.toDoubleOrNull()?.let(onWeightChange)
            },
            textChangeFilter = {
                it.filter { char -> char.isDigit() }
            },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number
            )
        )
    }
}

fun LazyGridScope.setsInfoHeader() {
    val headerItems = listOf("Sets", "Reps", "Weight (kg)")

    items(headerItems) {
        Text(
            text = it,
            style = AppTheme.typography.subtitle1,
            color = AppTheme.color.black87,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun WorkoutStartScreenContent(
    week: WeekDay?,
    state: WorkoutTrackerScreenState.StartScreen,
    sendIntent: (WorkoutTrackerIntent) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                horizontal = AppTheme.dimens.medium1
            )
            .padding(
                top = AppTheme.dimens.medium1,
                bottom = AppTheme.dimens.small3
            )
    ) {
        Text(
            text = "Previous exercises done on ${(week?.dayName ?: "")}",
            style = AppTheme.typography.h5,
            fontWeight = FontWeight.Bold,
            maxLines = 2,
            color = AppTheme.color.black87,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(AppTheme.dimens.small2))

        Text(
            text = "Select exercises for a quick planning for the workout",
            style = AppTheme.typography.subtitle1,
            fontWeight = FontWeight.Normal,
            color = AppTheme.color.black60,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(AppTheme.dimens.small2))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(
                vertical = AppTheme.dimens.medium1
            ),
            verticalArrangement = Arrangement.spacedBy(AppTheme.dimens.medium1)
        ) {
            val exercises = state.previousExercises
            when {
                exercises == null -> {}
                exercises.isEmpty() -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(AppTheme.dimens.workoutSectionEmptyHeight)
                                .padding(horizontal = AppTheme.dimens.medium1),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No history of exercises for this day",
                                style = AppTheme.typography.subtitle1,
                                color = AppTheme.color.black60,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
                else -> {
                    items(exercises) {
                        PreviousExerciseSuggestionCard(
                            data = it,
                            onClick = {
                                sendIntent.invoke(
                                    WorkoutTrackerIntent.ExerciseClickedInStartScreen(it.exerciseName)
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(AppTheme.dimens.small3))

        AppPrimaryButton(
            text = "Start",
            onClick = {
                sendIntent.invoke(WorkoutTrackerIntent.StartWorkoutIntent)
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(AppTheme.dimens.small3))

        AppSecondaryButton(
            text = "Not now",
            onClick = {
                sendIntent.invoke(WorkoutTrackerIntent.BackClickedIntent)
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun PreviousExerciseSuggestionCard(
    data: PreviousExerciseSelection,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(AppTheme.corners.mainCard)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = AppTheme.dimens.small1,
                    color = AppTheme.color.black,
                    shape = RoundedCornerShape(AppTheme.corners.mainCard)
                )
                .clickable(
                    interactionSource = null,
                    indication = null,
                    onClick = onClick
                )
                .padding(
                    horizontal = AppTheme.dimens.medium1,
                    vertical = AppTheme.dimens.medium2
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = data.exerciseName,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = AppTheme.typography.subtitle1,
                fontWeight = FontWeight.Normal
            )

            if (data.isSelected) {
                Icon(
                    painter = painterResource(Res.drawable.ic_solid_tick),
                    contentDescription = null,
                    modifier = Modifier.size(AppTheme.dimens.medium2),
                    tint = AppTheme.color.primaryRed
                )
            }
        }
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
                    ExerciseCard(
                        exercise = item,
                        editClicked = {
                            sendIntent.invoke(WorkoutTrackerIntent.EditExerciseIntent(item.exerciseId))
                        },
                        deleteClicked = {
                            sendIntent.invoke(WorkoutTrackerIntent.DeleteExerciseIntent(item.exerciseId))
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
            AppPrimaryHollowButton(
                text = "Add Exercise",
                onClick = onAddClicked,
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
    HorizontalButtons(
        primaryBtnText = "Complete",
        primaryBtnClick = completeClicked,
        secondaryBtnText = "Rest",
        secondaryBtnClick = restClicked,
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = AppTheme.dimens.medium1,
                vertical = AppTheme.dimens.medium1
            )
    )
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
fun ExerciseCard(
    exercise: ExercisePlanInfoAppModel,
    editClicked: () -> Unit,
    deleteClicked: () -> Unit,
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
            Text(
                text = exercise.name,
                modifier = Modifier.fillMaxWidth(),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                style = AppTheme.typography.h6,
                fontWeight = FontWeight.Bold,
                color = AppTheme.color.black
            )
            Spacer(Modifier.height(AppTheme.dimens.medium1))

            SetsList(sets = exercise.sets)

            Spacer(Modifier.height(AppTheme.dimens.medium1))

            HorizontalButtons(
                primaryBtnText = "Edit",
                primaryBtnClick = editClicked,
                secondaryBtnText = "Delete",
                secondaryBtnClick = deleteClicked,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun SetsList(
    sets: List<ExerciseSetInfoAppModel>,
    modifier: Modifier = Modifier
) {
    sets.forEachIndexed { index, set ->
        Row(
            modifier = modifier
                .padding(vertical = AppTheme.dimens.small1),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val style = AppTheme.typography.subtitle1
            val color = AppTheme.color.black87

            Text(
                text = "Set ${index + 1}",
                style = style,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = color,
                modifier = Modifier.weight(1f)
            )

            val weight = set.additionalWeight.displayString()
            val repsAndWeight = if (weight.isNotEmpty()) {
                "x ${set.repsCount} reps - $weight kgs"
            } else {
                "x ${set.repsCount} reps"
            }

            Text(
                text = repsAndWeight,
                style = style,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = color,
                modifier = Modifier
            )
        }
    }
}




