package org.example.project.wrkd.track

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.example.project.wrkd.core.models.app.ExercisePlanInfoAppModel
import org.example.project.wrkd.core.models.app.ExerciseResistanceMethod
import org.example.project.wrkd.core.models.app.ExerciseSetInfoAppModel
import org.example.project.wrkd.core.models.app.WeightInGrams
import org.example.project.wrkd.core.models.app.updateSetInfo
import org.example.project.wrkd.utils.KUUID

class WorkoutTrackerManagerImpl : WorkoutTrackManager {

    private val _state = MutableStateFlow<List<ExercisePlanInfoAppModel>>(listOf())
    override val state: StateFlow<List<ExercisePlanInfoAppModel>> = _state.asStateFlow()

    override fun addExercise() {
        // when adding exercise create an empty placeholder and have a prefilled set
        val exercise = ExercisePlanInfoAppModel(
            name = "",
            exerciseId = KUUID.generateId(),
            sets = listOf(ExerciseSetInfoAppModel.defaultSet())
        )

        _state.update { it + exercise }
    }

    override fun changeExerciseName(id: String, nameEntered: String) {
        _state.update {  currentList ->
            currentList.map {
                if (it.exerciseId == id) {
                    it.copy(name = nameEntered)
                } else {
                    it
                }
            }
        }
    }

    override fun addSet(exerciseId: String) {
        _state.update { currentList ->
            currentList.map {
                if (it.exerciseId == exerciseId) {
                    it.copy(
                        sets = it.sets + ExerciseSetInfoAppModel.defaultSet()
                    )
                } else {
                    it
                }
            }
        }
    }

    override fun removeSet(exerciseId: String, setId: String) {
        _state.update { currentList ->
            currentList.map {
                if (it.exerciseId == exerciseId) {
                    it.copy(
                        sets = it.sets.filter { set -> set.setId != setId }
                    )
                } else {
                    it
                }
            }
        }
    }

    override fun enterRepsCount(exerciseId: String, setId: String, repCount: Int) {
        if (repCount <= 0) return
        _state.update { currentList ->
            currentList.updateSetInfo(
                exerciseId = exerciseId,
                setId = setId,
                update = { it.copy(repsCount = repCount) }
            )
        }
    }

    override fun changeResistanceMethod(
        exerciseId: String,
        setId: String,
        resistanceMethod: ExerciseResistanceMethod
    ) {
        _state.update {
            it.updateSetInfo(
                exerciseId = exerciseId,
                setId = setId,
                update = { set ->
                    set.copy(resistanceMethod = resistanceMethod)
                },
            )
        }
    }

    override fun addAdditionalWeight(exerciseId: String, setId: String, additionalWeight: Double) {
        if (additionalWeight < 0) return
        _state.update {
            it.updateSetInfo(
                exerciseId = exerciseId,
                setId = setId,
                update = { set -> set.copy(additionalWeight = WeightInGrams.fromKg(additionalWeight)) }
            )
        }
    }

    override fun deleteExercise(id: String) {
        _state.update {
            it.filter { ex -> ex.exerciseId != id }
        }
    }
}