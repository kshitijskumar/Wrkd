package org.example.project.wrkd.track.ui

import androidx.compose.runtime.Immutable
import org.example.project.wrkd.core.models.app.ExercisePlanInfoAppModel
import org.example.project.wrkd.core.models.app.ExerciseResistanceMethod
import org.example.project.wrkd.core.models.WeekDay
import org.example.project.wrkd.core.navigation.args.WorkoutTrackingArgs
import org.example.project.wrkd.utils.System

@Immutable
data class WorkoutTrackerState(
    val week: WeekDay? = null,
    val date: Long? = null,
    val workoutDayName: String = "",
    val restTimer: Long? = null,
    val error: String? = null,
    val dialogType: WorkoutTrackerDialogTypes? = null,
    val screenState: WorkoutTrackerScreenState? = null,
    val previousExercisesName: List<String>? = null
)

@Immutable
sealed class WorkoutTrackerScreenState {

    @Immutable
    data class StartScreen(
        val previousExercises: List<PreviousExerciseSelection>? = null
    ) : WorkoutTrackerScreenState()

    @Immutable
    data class TrackerScreen(
        val exercises: List<ExercisePlanInfoAppModel>? = null,
        val isExerciseAdditionAllowed: Boolean = true,
    ) : WorkoutTrackerScreenState()

}

data class PreviousExerciseSelection(
    val exerciseName: String,
    val isSelected: Boolean
) {
    val selectedAt: Long? = if (isSelected) System.currentTimeInMillis else null
}

sealed class WorkoutTrackerDialogTypes {
    data object ConfirmationDialog : WorkoutTrackerDialogTypes()
    data object IncompleteWorkoutDetailsDialog : WorkoutTrackerDialogTypes()
}

sealed class WorkoutTrackerIntent {

    data class InitializationIntent(val args: WorkoutTrackingArgs) : WorkoutTrackerIntent()

    data class ExerciseClickedInStartScreen(val name: String) : WorkoutTrackerIntent()

    data object StartWorkoutIntent : WorkoutTrackerIntent()

    data object AddExerciseIntent : WorkoutTrackerIntent()

    data class EnterExerciseNameIntent(
        val exerciseId: String,
        val name: String
    ) : WorkoutTrackerIntent()

    data class AddSetIntent(
        val exerciseId: String
    ) : WorkoutTrackerIntent()

    data class AddRepsCountIntent(
        val exerciseId: String,
        val setId: String,
        val count: Int
    ) : WorkoutTrackerIntent()

    data class ChangeResistanceMethodIntent(
        val exerciseId: String,
        val setId: String,
        val resistanceMethod: ExerciseResistanceMethod
    ) : WorkoutTrackerIntent()

    data object ToggleRestTimerIntent : WorkoutTrackerIntent()

    data class CompleteWorkoutIntent(val hasConfirmed: Boolean) : WorkoutTrackerIntent()

    data class WorkoutDayNameEnteredIntent(val name: String) : WorkoutTrackerIntent()

    data class WeightChangeIntent(
        val exerciseId: String,
        val setId: String,
        val weightEntered: Double
    ) : WorkoutTrackerIntent()

    data object DismissDialogIntent : WorkoutTrackerIntent()

    data object BackClickedIntent : WorkoutTrackerIntent()

}