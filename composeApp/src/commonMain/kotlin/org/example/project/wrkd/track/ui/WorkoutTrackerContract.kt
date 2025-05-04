package org.example.project.wrkd.track.ui

import androidx.compose.runtime.Immutable
import org.example.project.wrkd.core.models.app.ExercisePlanInfoAppModel
import org.example.project.wrkd.core.models.app.ExerciseResistanceMethod
import org.example.project.wrkd.core.models.WeekDay

@Immutable
data class WorkoutTrackerState(
    val week: WeekDay? = null,
    val date: Long? = null,
    val workoutDayName: String = "",
    val exercises: List<ExercisePlanInfoAppModel>? = null,
    val restTimer: Long? = null,
    val isExerciseAdditionAllowed: Boolean = true,
    val error: String? = null,
    val dialogType: WorkoutTrackerDialogTypes? = null
)

sealed class WorkoutTrackerDialogTypes {
    data object ConfirmationDialog : WorkoutTrackerDialogTypes()
    data object IncompleteWorkoutDetailsDialog : WorkoutTrackerDialogTypes()
}

sealed class WorkoutTrackerIntent {

    data object InitializationIntent : WorkoutTrackerIntent()

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

}