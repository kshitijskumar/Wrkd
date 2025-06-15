package org.example.project.wrkd.track

import kotlinx.coroutines.flow.Flow
import org.example.project.wrkd.core.models.app.ExercisePlanInfoAppModel
import org.example.project.wrkd.core.models.app.ExerciseSetInfoAppModel

interface WorkoutTrackManager {

    /**
     * @param workoutId workout id to initialise state, otherwise default state
     */
    fun initialise(
        workoutId: String?
    ): Flow<WorkoutTrackInfo>

    /**
     * Initialises the state with stub entries for the given exercises name
     */
    fun initialiseWithExercises(
        exercisesName: List<String>
    ): Flow<WorkoutTrackInfo>

    /**
     * @param exerciseId exercise if for an already existing exercise to become editable, otherwise default exercise
     */
    fun createEditableEntry(
        exerciseId: String?
    ): Flow<WorkoutEditableEntry?>

    fun resetEditableEntry()

    fun submitEditableEntry()

    fun deleteExercise(exerciseId: String)

    fun addSet()

    fun deleteSet(setId: String)

    fun exerciseNameChange(name: String)

    fun repCountChange(
        setId: String,
        reps: Int
    )

    fun additionalWeightChange(
        setId: String?,
        weight: Double
    )

    fun hasMadeAnyUpdates(): Boolean

}

data class WorkoutTrackInfo(
    val exercises: List<ExercisePlanInfoAppModel>,
)

data class WorkoutEditableEntry(
    val exerciseId: String?,
    val name: String,
    val sets: List<ExerciseSetInfoAppModel>
) {
    val shouldEnableSubmitBtn: Boolean
        get() {
            return name.isNotEmpty() && sets.isNotEmpty()
        }

    companion object {
        fun default(): WorkoutEditableEntry {
            return WorkoutEditableEntry(
                exerciseId = null,
                name = "",
                sets = listOf(ExerciseSetInfoAppModel.defaultSet())
            )
        }
    }
}