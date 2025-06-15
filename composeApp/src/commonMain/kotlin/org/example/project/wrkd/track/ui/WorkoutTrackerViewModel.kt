package org.example.project.wrkd.track.ui

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.example.project.wrkd.core.models.WeekDay
import org.example.project.wrkd.core.models.app.ExercisePlanInfoAppModel
import org.example.project.wrkd.core.models.app.isValid
import org.example.project.wrkd.core.models.dayName
import org.example.project.wrkd.core.navigation.AppNavigator
import org.example.project.wrkd.core.navigation.args.AppBaseScreenArgs
import org.example.project.wrkd.core.navigation.args.WorkoutTrackingArgs
import org.example.project.wrkd.core.navigation.navOptions
import org.example.project.wrkd.core.navigation.scenes.AppScenes
import org.example.project.wrkd.core.ui.BaseViewModel
import org.example.project.wrkd.track.WorkoutTrackManager
import org.example.project.wrkd.track.domain.GetDistinctExercisesForDayBetweenTimestampUseCase
import org.example.project.wrkd.track.domain.SaveWorkoutUseCase
import org.example.project.wrkd.utils.DayTimeRange
import org.example.project.wrkd.utils.System
import org.example.project.wrkd.utils.TimeUtils

class WorkoutTrackerViewModel(
    args: WorkoutTrackingArgs,
    private val timeUtils: TimeUtils,
    private val workoutTrackManager: WorkoutTrackManager,
    private val saveWorkoutUseCase: SaveWorkoutUseCase,
    private val appNavigator: AppNavigator,
    private val getDistinctExercisesForDayBetweenTimestampUseCase: GetDistinctExercisesForDayBetweenTimestampUseCase
) : BaseViewModel<WorkoutTrackerState, WorkoutTrackerIntent>() {

    override fun initialData(): WorkoutTrackerState {
        return WorkoutTrackerState()
    }

    private var toggleTimerJob: Job? = null
    private var workoutManagerJob: Job? = null
    private var editableEntryJob: Job? = null

    init {
        processIntent(WorkoutTrackerIntent.InitializationIntent(args))
    }

    override fun processIntent(intent: WorkoutTrackerIntent) {
        when (intent) {
            is WorkoutTrackerIntent.InitializationIntent -> handleInitializationIntent(intent)
            is WorkoutTrackerIntent.ExerciseClickedInStartScreen -> handleExerciseClickedInStartScreen(intent)
            WorkoutTrackerIntent.StartWorkoutIntent -> handleStartWorkoutIntent()
            is WorkoutTrackerIntent.AddExerciseIntent -> handleAddExerciseIntent(intent)
            is WorkoutTrackerIntent.AddRepsCountIntent -> handleAddRepsCountIntent(intent)
            is WorkoutTrackerIntent.AddSetIntent -> handleAddSetIntent(intent)
            is WorkoutTrackerIntent.EnterExerciseNameIntent -> handleEnterExerciseNameIntent(intent)
            is WorkoutTrackerIntent.ToggleRestTimerIntent -> handleToggleRestTimerIntent(intent)
            is WorkoutTrackerIntent.WorkoutDayNameEnteredIntent -> handleWorkoutDayNameEnteredIntent(intent)
            is WorkoutTrackerIntent.WeightChangeIntent -> handleWorkoutWeightChangeIntent(intent)
            is WorkoutTrackerIntent.RemoveSetIntent -> handleRemoveSetIntent(intent)
            WorkoutTrackerIntent.SubmitExerciseIntent -> handleSubmitExerciseIntent()
            is WorkoutTrackerIntent.DeleteExerciseIntent -> handleDeleteExerciseIntent(intent)
            is WorkoutTrackerIntent.EditExerciseIntent -> handleEditExerciseIntent(intent)
            WorkoutTrackerIntent.DismissDialogIntent -> handleDismissDialogIntent()
            is WorkoutTrackerIntent.CompleteBottomSheetPositiveClickedIntent -> handleCompleteBottomSheetPositiveClickedIntent(intent)
            is WorkoutTrackerIntent.CompleteWorkoutIntent -> handleCompleteWorkoutIntent(intent)
            WorkoutTrackerIntent.BackClickedIntent -> handleBackClickedIntent()
            WorkoutTrackerIntent.BackClickedConfirmedIntent -> handleBackClickedConfirmedIntent()
        }
    }

    private fun handleInitializationIntent(intent: WorkoutTrackerIntent.InitializationIntent) {
        val currentDate = System.currentTimeInMillis
        val week = timeUtils.getWeekFromTime(currentDate)

        if (week == null) {
            // should not happen
            goBack()
            return
        }

        when(intent.args) {
            WorkoutTrackingArgs.TrackingArgs -> {
                initialiseForTrackingArgs(week, currentDate)
            }
            is WorkoutTrackingArgs.DisplayArgs -> TODO()
        }
        updateState {
            it.copy(
                week = week,
                date = currentDate
            )
        }
    }

    private fun initialiseForTrackingArgs(
        week: WeekDay,
        currentDate: Long,
    ) = viewModelScope.launch {
        val previousExercises =
            getDistinctExercisesForDayBetweenTimestampUseCase.forGivenDayInLastMonth(
                time = currentDate
            ).firstOrNull() ?: listOf()

        updateState { currentState ->
            currentState.copy(
                week = week,
                date = currentDate,
                screenState = WorkoutTrackerScreenState.StartScreen(
                    previousExercises = previousExercises.map {
                        PreviousExerciseSelection(
                            exerciseName = it,
                            isSelected = false
                        )
                    }
                ),
                previousExercisesName = previousExercises
            )
        }
    }

    private fun handleAddExerciseIntent(intent: WorkoutTrackerIntent.AddExerciseIntent) {
        if (isExerciseAdditionAllowed().not()) {
            return // shouldn't happen
        }

        startCollectingEditableEntryInfo(null)
    }

    private fun startCollectingEditableEntryInfo(exerciseId: String?) {
        editableEntryJob?.cancel()
        editableEntryJob = viewModelScope.launch {
            workoutTrackManager.createEditableEntry(exerciseId).collect { entry ->
                val bsType = entry?.let {
                    WorkoutTrackerBottomSheetType.ExerciseDetails(
                        exerciseId = entry.exerciseId,
                        name = entry.name,
                        sets = entry.sets
                    )
                }

                updateState {
                    it.copy(
                        bottomSheetType = bsType
                    )
                }
            }
        }
    }

    fun isExerciseAdditionAllowed(): Boolean {
        return when(val state = currentState.screenState) {
            is WorkoutTrackerScreenState.TrackerScreen -> state.isExerciseAdditionAllowed
            is WorkoutTrackerScreenState.StartScreen,
            null -> false
        }
    }

    private fun handleAddRepsCountIntent(intent: WorkoutTrackerIntent.AddRepsCountIntent) {
        if (intent.count < 0) {
            updateState {
                it.copy(
                    error = "Enter a valid reps count"
                )
            }
        } else {
            workoutTrackManager.repCountChange(intent.setId, intent.count)
        }
    }

    private fun handleAddSetIntent(intent: WorkoutTrackerIntent.AddSetIntent) {
        workoutTrackManager.addSet()
    }
    private fun handleEnterExerciseNameIntent(intent: WorkoutTrackerIntent.EnterExerciseNameIntent) {
        // TODO::KSHITIJ - add auto suggestion kind of feature for exercise name
        workoutTrackManager.exerciseNameChange(intent.name)
    }

    private fun handleToggleRestTimerIntent(intent: WorkoutTrackerIntent.ToggleRestTimerIntent) {
        val isRestTimerRunning = currentState.restTimer != null

        if (isRestTimerRunning) {
            // stop timer and reset everything
            toggleTimerJob?.cancel()
            toggleTimerJob = null
            updateState {
                it.copy(
                    restTimer = null
                )
            }
        } else {
            // start rest timer
            toggleTimerJob?.cancel()
            toggleTimerJob = viewModelScope.launch {
                val timerStartedAt = System.currentTimeInMillis
                while (isActive) {
                    // TODO::KSHITIJ - this is not accurate - come back to this later as a separate problem
                    val totalRestTime = System.currentTimeInMillis - timerStartedAt
                    updateState {
                        it.copy(
                            restTimer = totalRestTime
                        )
                    }
                    delay(1000)
                }
            }
        }
    }

    private fun handleCompleteWorkoutIntent(intent: WorkoutTrackerIntent.CompleteWorkoutIntent) = viewModelScope.launch {
        val currentState = currentState
        val screenState = when(currentState.screenState) {
            is WorkoutTrackerScreenState.TrackerScreen -> currentState.screenState
            is WorkoutTrackerScreenState.StartScreen,
            null -> return@launch // complete wont be called for start screen
        }
        val exercisesList = screenState.exercises ?: listOf()
        val weekDay = currentState.week
        val startedAt = currentState.date
        when {
            (!exercisesList.areAllFieldsValid() || weekDay == null || startedAt == null) -> {
                updateState {
                    it.copy(
                        dialogType = WorkoutTrackerDialogTypes.IncompleteWorkoutDetailsDialog
                    )
                }
            }
            !intent.hasConfirmed -> {
                updateState {
                    it.copy(
                        dialogType = WorkoutTrackerDialogTypes.ConfirmationDialog
                    )
                }
            }
            else -> {
                updateState {
                    it.copy(
                        error = null,
                        dialogType = null
                    )
                }
                val currentTime = System.currentTimeInMillis
                val duration = currentTime - startedAt

                // before saving cancel all the collection jobs since now we do not want any kinds of updates
                workoutManagerJob?.cancel()
                editableEntryJob?.cancel()

                workoutTrackManager.resetEditableEntry()

                saveWorkoutUseCase.invoke(
                    weekDay = weekDay,
                    dayName = screenState.workoutDayName.ifEmpty { defaultWorkoutDayName(weekDay, startedAt) },
                    startedAt = startedAt,
                    duration = currentTime - startedAt,
                    exercises = exercisesList
                )
                updateState {
                    it.copy(
                        bottomSheetType = WorkoutTrackerBottomSheetType.WorkoutComplete(
                            totalDurationWorkedOut = timeUtils.durationMillisToHourMinute(
                                durationMillis = duration
                            ),
                            totalExercises = exercisesList.size
                        )
                    )
                }
            }
        }
    }

    private fun handleWorkoutDayNameEnteredIntent(intent: WorkoutTrackerIntent.WorkoutDayNameEnteredIntent) {
        val screenState = when(val scState = currentState.screenState) {
            is WorkoutTrackerScreenState.TrackerScreen -> scState
            is WorkoutTrackerScreenState.StartScreen,
            null -> return
        }
        updateState {
            it.copy(
                screenState = screenState.copy(
                    workoutDayName = intent.name
                )
            )
        }
    }

    private fun handleWorkoutWeightChangeIntent(intent: WorkoutTrackerIntent.WeightChangeIntent) {
        workoutTrackManager.additionalWeightChange(intent.setId, intent.weightEntered)
    }

    private fun handleDismissDialogIntent() {
        updateState {
            it.copy(
                dialogType = null
            )
        }
    }

    private fun handleExerciseClickedInStartScreen(intent: WorkoutTrackerIntent.ExerciseClickedInStartScreen) {
        updateState {
            val updatedScreenState = when(val screenState = it.screenState) {
                is WorkoutTrackerScreenState.StartScreen -> {
                    screenState.copy(
                        previousExercises = screenState.previousExercises?.map {
                            if (it.exerciseName == intent.name) {
                                it.copy(isSelected = !it.isSelected)
                            } else {
                                it
                            }
                        }
                    )
                }
                is WorkoutTrackerScreenState.TrackerScreen,
                null -> screenState
            }

            it.copy(
                screenState = updatedScreenState
            )
        }
    }

    private fun handleStartWorkoutIntent() {
        val exercisesSelected = when(val state = currentState.screenState) {
            is WorkoutTrackerScreenState.StartScreen -> {
                state.previousExercises
                    ?.filter { it.isSelected }
                    ?.sortedBy { it.selectedAt ?: 0L } ?: return
            }
            is WorkoutTrackerScreenState.TrackerScreen, // start wont be called when current screen is already tracker screen
            null -> return
        }

        val startTime = System.currentTimeInMillis

        val defaultDayName = defaultWorkoutDayName(
            weekDay = currentState.week ?: WeekDay.Mon, // shoudnt be null at this point as already handled in initialisation intent
            startTime = startTime
        )
        updateState {
            it.copy(
                date = System.currentTimeInMillis,
                screenState = WorkoutTrackerScreenState.TrackerScreen(
                    workoutDayName = defaultDayName,
                    workoutDayNamePlaceholder = defaultDayName
                )
            )
        }

        val exerciseNamesList = exercisesSelected.map { it.exerciseName }
        startCollectingFreshWorkoutInfo(exerciseNamesList)
        if (exerciseNamesList.isEmpty()) {
            processIntent(WorkoutTrackerIntent.AddExerciseIntent)
        }
    }

    private fun handleBackClickedIntent() {
        if (currentState.bottomSheetType != null) {
            updateState {
                it.copy(
                    bottomSheetType = null
                )
            }
            return
        }
        when(currentState.screenState) {
            is WorkoutTrackerScreenState.TrackerScreen ->  {
                if (workoutTrackManager.hasMadeAnyUpdates()) {
                    updateState {
                        it.copy(
                            dialogType = WorkoutTrackerDialogTypes.BackClickedConfirmationDialog
                        )
                    }
                } else {
                    // if not updates from initial data then go back directly
                    goBack()
                }
            }
            is WorkoutTrackerScreenState.StartScreen,
            null -> {
                goBack()
            }
        }
    }

    private fun handleBackClickedConfirmedIntent() {
        updateState {
            it.copy(
                dialogType = null
            )
        }
        goBack()
    }

    private fun goBack() {
        workoutManagerJob?.cancel()
        editableEntryJob?.cancel()

        updateState {
            it.copy(
                bottomSheetType = null,
                dialogType = null
            )
        }
        appNavigator.goBack()
    }

    private fun handleSubmitExerciseIntent() {
        workoutTrackManager.submitEditableEntry()
    }

    private fun handleRemoveSetIntent(intent: WorkoutTrackerIntent.RemoveSetIntent) {
        workoutTrackManager.deleteSet(intent.setId)
    }

    private fun handleDeleteExerciseIntent(intent: WorkoutTrackerIntent.DeleteExerciseIntent) {
        workoutTrackManager.deleteExercise(intent.exerciseId)
    }

    private fun handleEditExerciseIntent(intent: WorkoutTrackerIntent.EditExerciseIntent) {
        startCollectingEditableEntryInfo(intent.exerciseId)
    }

    private fun handleCompleteBottomSheetPositiveClickedIntent(intent: WorkoutTrackerIntent.CompleteBottomSheetPositiveClickedIntent) {
        goBack()
        updateState {
            it.copy(
                bottomSheetType = null
            )
        }
    }
    private fun startCollectingFreshWorkoutInfo(
        exerciseNames: List<String>
    ) {
        workoutManagerJob?.cancel()
        workoutManagerJob = viewModelScope.launch {
            workoutTrackManager.initialiseWithExercises(exerciseNames).collect { info ->
                updateState { currentState ->
                    val updatedScreenState = when(val screenState = currentState.screenState) {
                        is WorkoutTrackerScreenState.TrackerScreen -> {
                            screenState.copy(
                                exercises = info.exercises,
                                isExerciseAdditionAllowed = isExerciseAdditionAllowed(info.exercises)
                            )
                        }
                        is WorkoutTrackerScreenState.StartScreen,
                        null -> {
                            val defaultDayName = defaultWorkoutDayName(
                                weekDay = currentState.week ?: WeekDay.Mon,
                                startTime = currentState.date ?: System.currentTimeInMillis
                            )
                            WorkoutTrackerScreenState.TrackerScreen(
                                exercises = info.exercises,
                                isExerciseAdditionAllowed = isExerciseAdditionAllowed(info.exercises),
                                workoutDayName = defaultDayName,
                                workoutDayNamePlaceholder = defaultDayName
                            )
                        }
                    }

                    currentState.copy(
                        screenState = updatedScreenState
                    )
                }
            }
        }
    }

    private fun defaultWorkoutDayName(weekDay: WeekDay, startTime: Long): String {
        val dayName = weekDay.dayName
        val hour = timeUtils.getHourOfDay(startTime)


        val timeOfTheDay = when (timeUtils.getDayTimeRange(hour)) {
            DayTimeRange.MORNING -> "Morning"
            DayTimeRange.AFTERNOON -> "Afternoon"
            DayTimeRange.EVENING -> "Evening"
            DayTimeRange.NIGHT -> "Night"
            null -> ""
        }

        return if (timeOfTheDay.isNotBlank()) {
            "$dayName $timeOfTheDay"
        } else {
            dayName
        }
    }

    private fun List<ExercisePlanInfoAppModel>.areAllFieldsValid(): Boolean {
        return this.isNotEmpty() && this.all {
                    it.name.isNotBlank() && it.sets.all { set -> set.repsCount > 0 && set.additionalWeight.isValid() }
        }
    }

    private fun isExerciseAdditionAllowed(currentList: List<ExercisePlanInfoAppModel>): Boolean {
        if (currentList.isEmpty()) {
            return true
        }
        var countOfExercisesWithEmptyName = 0
        currentList.forEach {
            if (it.name.isEmpty()) {
                countOfExercisesWithEmptyName += 1
            }
            if (countOfExercisesWithEmptyName >= MAX_EMPTY_EXERCISES_ALLOWED) {
                return false
            }
        }

        return countOfExercisesWithEmptyName < MAX_EMPTY_EXERCISES_ALLOWED
    }

    companion object {
        const val MAX_EMPTY_EXERCISES_ALLOWED = 2
    }
}