package org.example.project.wrkd.track.ui

import androidx.compose.runtime.Immutable
import org.example.project.wrkd.core.models.app.ExercisePlanInfoAppModel
import org.example.project.wrkd.core.models.app.ExerciseResistanceMethod
import org.example.project.wrkd.core.models.WeekDay
import org.example.project.wrkd.core.models.app.ExerciseSetInfoAppModel
import org.example.project.wrkd.core.navigation.args.WorkoutTrackingArgs
import org.example.project.wrkd.utils.System

@Immutable
data class WorkoutTrackerState(
    val week: WeekDay? = null,
    val date: Long? = null,
    val restTimer: Long? = null,
    val error: String? = null,
    val dialogType: WorkoutTrackerDialogTypes? = null,
    val screenState: WorkoutTrackerScreenState? = null,
    val previousExercisesName: List<String>? = null,
    val bottomSheetType: WorkoutTrackerBottomSheetType? = null
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
        val workoutDayNamePlaceholder: String = "",
        val workoutDayName: String = "",
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

    data object BackClickedConfirmationDialog : WorkoutTrackerDialogTypes()
}

sealed class WorkoutTrackerIntent {

    data class InitializationIntent(val args: WorkoutTrackingArgs) : WorkoutTrackerIntent()

    data class ExerciseClickedInStartScreen(val name: String) : WorkoutTrackerIntent()

    data object StartWorkoutIntent : WorkoutTrackerIntent()

    data object AddExerciseIntent : WorkoutTrackerIntent()

    data class EnterExerciseNameIntent(
        val name: String
    ) : WorkoutTrackerIntent()

    data object AddSetIntent : WorkoutTrackerIntent()

    data class AddRepsCountIntent(
        val setId: String,
        val count: Int
    ) : WorkoutTrackerIntent()

    data object ToggleRestTimerIntent : WorkoutTrackerIntent()

    data class CompleteWorkoutIntent(val hasConfirmed: Boolean) : WorkoutTrackerIntent()

    data class WorkoutDayNameEnteredIntent(val name: String) : WorkoutTrackerIntent()

    data class WeightChangeIntent(
        val setId: String,
        val weightEntered: Double
    ) : WorkoutTrackerIntent()

    data object DismissDialogIntent : WorkoutTrackerIntent()

    data object BackClickedIntent : WorkoutTrackerIntent()

    data object BackClickedConfirmedIntent : WorkoutTrackerIntent()

    data object SubmitExerciseIntent : WorkoutTrackerIntent()

    data class RemoveSetIntent(val setId: String) : WorkoutTrackerIntent()

    data class EditExerciseIntent(val exerciseId: String) : WorkoutTrackerIntent()

    data class DeleteExerciseIntent(val exerciseId: String) : WorkoutTrackerIntent()

    data object CompleteBottomSheetPositiveClickedIntent : WorkoutTrackerIntent()

}

sealed class WorkoutTrackerBottomSheetType {

    /**
     * @param exerciseId Pass null if adding new exercise, otherwise pass the exercise id for editing
     */
    data class ExerciseDetails(
        val exerciseId: String?,
        val name: String,
        val sets: List<ExerciseSetInfoAppModel>
    ): WorkoutTrackerBottomSheetType() {
        val shouldEnableSubmitBtn: Boolean
            get() {
                return name.isNotEmpty() && sets.isNotEmpty()
            }
    }

    data class WorkoutComplete(
        val totalDurationWorkedOut: String,
        val totalExercises: Int
    ) : WorkoutTrackerBottomSheetType()

}