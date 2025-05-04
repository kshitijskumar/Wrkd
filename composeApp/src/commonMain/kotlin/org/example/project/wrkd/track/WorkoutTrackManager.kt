package org.example.project.wrkd.track

import kotlinx.coroutines.flow.StateFlow
import org.example.project.wrkd.core.models.app.ExercisePlanInfoAppModel
import org.example.project.wrkd.core.models.app.ExerciseResistanceMethod

interface WorkoutTrackManager {

    val state: StateFlow<List<ExercisePlanInfoAppModel>>

    fun addExercise()

    fun changeExerciseName(id: String, nameEntered: String)

    fun addSet(exerciseId: String)

    fun removeSet(exerciseId: String, setId: String)

    fun enterRepsCount(
        exerciseId: String,
        setId: String,
        repCount: Int
    )

    fun changeResistanceMethod(
        exerciseId: String,
        setId: String,
        resistanceMethod: ExerciseResistanceMethod
    )

    fun addAdditionalWeight(
        exerciseId: String,
        setId: String,
        additionalWeight: Double
    )

    fun deleteExercise(id: String)

}