package org.example.project.wrkd.track.ui

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.example.project.wrkd.core.models.WeekDay
import org.example.project.wrkd.core.models.app.ExercisePlanInfoAppModel
import org.example.project.wrkd.core.models.app.isValid
import org.example.project.wrkd.core.models.dayName
import org.example.project.wrkd.core.ui.BaseViewModel
import org.example.project.wrkd.track.WorkoutTrackManager
import org.example.project.wrkd.track.domain.SaveWorkoutUseCase
import org.example.project.wrkd.utils.DayTimeRange
import org.example.project.wrkd.utils.System
import org.example.project.wrkd.utils.TimeUtils

class WorkoutTrackerViewModel(
    private val timeUtils: TimeUtils,
    private val workoutTrackManager: WorkoutTrackManager,
    private val saveWorkoutUseCase: SaveWorkoutUseCase
) : BaseViewModel<WorkoutTrackerState, WorkoutTrackerIntent>() {

    override fun initialData(): WorkoutTrackerState {
        return WorkoutTrackerState()
    }

    private var toggleTimerJob: Job? = null

    init {
        processIntent(WorkoutTrackerIntent.InitializationIntent)
        viewModelScope.launch {
            workoutTrackManager.state.collect { exerciseList ->
                println("TrackingStuff: collect: $exerciseList")
                updateState {
                    it.copy(
                        exercises = exerciseList,
                        isExerciseAdditionAllowed = isExerciseAdditionAllowed(exerciseList)
                    )
                }
            }
        }
    }

    override fun processIntent(intent: WorkoutTrackerIntent) {
        println("WorkoutTrackerViewModel: processIntent: $intent")
        when (intent) {
            WorkoutTrackerIntent.InitializationIntent -> handleInitializationIntent()
            is WorkoutTrackerIntent.AddExerciseIntent -> handleAddExerciseIntent(intent)
            is WorkoutTrackerIntent.AddRepsCountIntent -> handleAddRepsCountIntent(intent)
            is WorkoutTrackerIntent.AddSetIntent -> handleAddSetIntent(intent)
            is WorkoutTrackerIntent.ChangeResistanceMethodIntent -> handleChangeResistanceMethodIntent(intent)
            is WorkoutTrackerIntent.EnterExerciseNameIntent -> handleEnterExerciseNameIntent(intent)
            is WorkoutTrackerIntent.ToggleRestTimerIntent -> handleToggleRestTimerIntent(intent)
            is WorkoutTrackerIntent.CompleteWorkoutIntent -> handleCompleteWorkoutIntent(intent)
            is WorkoutTrackerIntent.WorkoutDayNameEnteredIntent -> handleWorkoutDayNameEnteredIntent(intent)
            is WorkoutTrackerIntent.WeightChangeIntent -> handleWorkoutWeightChangeIntent(intent)
            WorkoutTrackerIntent.DismissDialogIntent -> handleDismissDialogIntent()
        }
    }

    private fun handleInitializationIntent() {
        val currentDate = System.currentTimeInMillis
        val week = timeUtils.getWeekFromTime(currentDate)

        if (week == null) {
            // TODO::KSHITIJ - add back navigation -> although should never happen
            return
        }

        updateState {
            it.copy(
                week = week,
                date = currentDate,
                workoutDayName = defaultWorkoutDayName(
                    weekDay = week,
                    startTime = currentDate
                )
            )
        }
    }

    private fun handleAddExerciseIntent(intent: WorkoutTrackerIntent.AddExerciseIntent) {
        if (currentState.isExerciseAdditionAllowed.not()) {
            return // shouldn't happen
        }

        workoutTrackManager.addExercise()
    }

    private fun handleAddRepsCountIntent(intent: WorkoutTrackerIntent.AddRepsCountIntent) {
        if (intent.count < 0) {
            updateState {
                it.copy(
                    error = "Enter a valid reps count"
                )
            }
        } else {
            workoutTrackManager.enterRepsCount(
                exerciseId = intent.exerciseId,
                setId = intent.setId,
                repCount = intent.count
            )
        }
    }

    private fun handleAddSetIntent(intent: WorkoutTrackerIntent.AddSetIntent) {
        workoutTrackManager.addSet(intent.exerciseId)
    }

    private fun handleChangeResistanceMethodIntent(intent: WorkoutTrackerIntent.ChangeResistanceMethodIntent) {
        workoutTrackManager.changeResistanceMethod(
            exerciseId = intent.exerciseId,
            setId = intent.setId,
            resistanceMethod = intent.resistanceMethod
        )
    }

    private fun handleEnterExerciseNameIntent(intent: WorkoutTrackerIntent.EnterExerciseNameIntent) {
        // TODO::KSHITIJ - add auto suggestion kind of feature for exercise name
        workoutTrackManager.changeExerciseName(
            id = intent.exerciseId,
            nameEntered = intent.name
        )
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
        val exercisesList = currentState.exercises ?: listOf()
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
                saveWorkoutUseCase.invoke(
                    weekDay = weekDay,
                    dayName = currentState.workoutDayName.ifEmpty { defaultWorkoutDayName(weekDay, startedAt) },
                    startedAt = startedAt,
                    duration = currentTime - startedAt,
                    exercises = exercisesList
                )
            }
        }
    }

    private fun handleWorkoutDayNameEnteredIntent(intent: WorkoutTrackerIntent.WorkoutDayNameEnteredIntent) {
        updateState {
            it.copy(
                workoutDayName = intent.name
            )
        }
    }

    private fun handleWorkoutWeightChangeIntent(intent: WorkoutTrackerIntent.WeightChangeIntent) {
        workoutTrackManager.addAdditionalWeight(
            exerciseId = intent.exerciseId,
            setId = intent.setId,
            additionalWeight = intent.weightEntered
        )
    }

    private fun handleDismissDialogIntent() {
        updateState {
            it.copy(
                dialogType = null
            )
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