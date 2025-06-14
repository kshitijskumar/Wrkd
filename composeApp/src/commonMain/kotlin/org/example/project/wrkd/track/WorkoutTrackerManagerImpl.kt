package org.example.project.wrkd.track

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import org.example.project.wrkd.core.models.app.ExercisePlanInfoAppModel
import org.example.project.wrkd.core.models.app.ExerciseResistanceMethod
import org.example.project.wrkd.core.models.app.ExerciseSetInfoAppModel
import org.example.project.wrkd.core.models.app.WeightInGrams
import org.example.project.wrkd.core.models.app.updateSetInfo
import org.example.project.wrkd.utils.KUUID
import org.example.project.wrkd.utils.System

class WorkoutTrackerManagerImpl : WorkoutTrackManager {

    private var initialDataStartedWith: WorkoutTrackInfo? = null
    private val _state = MutableStateFlow<WorkoutTrackManagerInternalState?>(null)

    override fun initialise(workoutId: String?): Flow<WorkoutTrackInfo> {
        val defaultState = WorkoutTrackManagerInternalState(
            exercises = listOf(),
            editableEntry = null
        )
        val finalState = _state.value?.copy(
            exercises = defaultState.exercises
        ) ?: defaultState

        return initialise(finalState)
    }

    override fun initialiseWithExercises(exercisesName: List<String>): Flow<WorkoutTrackInfo> {
        val stubExercisesEntries = exercisesName.map {
            ExercisePlanInfoAppModel(
                name = it,
                exerciseId = KUUID.generateId(),
                exercisePerformedAt = System.currentTimeInMillis,
                sets = listOf(ExerciseSetInfoAppModel.defaultSet())
            )
        }

        val default = WorkoutTrackManagerInternalState(
            exercises = stubExercisesEntries,
            editableEntry = null
        )
        val finalState = _state.value?.copy(exercises = default.exercises) ?: default
        return initialise(finalState)
    }

    private fun initialise(
        finalState: WorkoutTrackManagerInternalState
    ): Flow<WorkoutTrackInfo> {
        _state.update {
            finalState.also {
                initialDataStartedWith = WorkoutTrackInfo(
                    exercises = finalState.exercises
                )
            }
        }

        return _state
            .filterNotNull()
            .map {
                WorkoutTrackInfo(
                    exercises = it.exercises
                )
            }
    }

    override fun createEditableEntry(exerciseId: String?): Flow<WorkoutEditableEntry?> {
        _state.update {
            val editableEntry = it?.createEditableEntryForExercise(exerciseId ?: "") ?: WorkoutEditableEntry.default()

            it?.copy(
                editableEntry = editableEntry
            )
        }

        return _state
            .filterNotNull()
            .map {
                it.editableEntry
            }
    }

    override fun deleteExercise(exerciseId: String) {
        _state.update {
            it?.copy(
                exercises = it.exercises.filter { ex -> ex.exerciseId != exerciseId }
            )
        }
    }

    private fun WorkoutTrackManagerInternalState.createEditableEntryForExercise(id: String): WorkoutEditableEntry? {
        val exerciseForId = this.exercises.find { it.exerciseId == id }

        return exerciseForId?.let {
            WorkoutEditableEntry(
                exerciseId = id,
                name = exerciseForId.name,
                sets = exerciseForId.sets
            )
        }
    }

    override fun resetEditableEntry() {
        _state.update {
            it?.copy(
                editableEntry = null
            )
        }
    }

    override fun submitEditableEntry() {
        val entry = _state.value?.editableEntry ?: return
        _state.update {
            val exercises = it?.exercises ?: listOf()
            var isEntryPresentInList = false
            var updatedExercises = exercises.map { ex ->
                if (ex.exerciseId == entry.exerciseId) {
                    isEntryPresentInList = true
                    ex.copy(
                        name = entry.name,
                        sets = entry.sets
                    )
                } else {
                    ex
                }
            }

            if (!isEntryPresentInList) {
                updatedExercises = exercises + ExercisePlanInfoAppModel(
                    name = entry.name,
                    exerciseId = KUUID.generateId(),
                    exercisePerformedAt = System.currentTimeInMillis,
                    sets = entry.sets
                )
            }

            it?.copy(
                exercises = updatedExercises
            )
        }
        resetEditableEntry()
    }

    override fun addSet() {
        _state.update {
            it?.copy(
                editableEntry = it.editableEntry?.copy(
                    sets = it.editableEntry.sets + ExerciseSetInfoAppModel.defaultSet()
                )
            )
        }
    }

    override fun deleteSet(setId: String) {
        _state.update {
            it?.copy(
                editableEntry = it.editableEntry?.copy(
                    sets = it.editableEntry.sets.filter { set -> set.setId != setId }
                )
            )
        }
    }

    override fun exerciseNameChange(name: String) {
        _state.update {
            it?.copy(
                editableEntry = it.editableEntry?.copy(
                    name = name
                )
            )
        }
    }

    override fun repCountChange(setId: String, reps: Int) {
        _state.update {
            it?.copy(
                editableEntry = it.editableEntry?.copy(
                    sets = it.editableEntry.sets.map { set ->
                        if (set.setId == setId) {
                            set.copy(repsCount = reps)
                        } else {
                            set
                        }
                    }
                )
            )
        }
    }

    override fun additionalWeightChange(setId: String?, weight: Double) {
        _state.update {
            it?.copy(
                editableEntry = it.editableEntry?.copy(
                    sets = it.editableEntry.sets.map { set ->
                        if (set.setId == setId) {
                            set.copy(additionalWeight = WeightInGrams.fromKg(weight))
                        } else {
                            set
                        }
                    }
                )
            )
        }
    }

    override fun hasMadeAnyUpdates(): Boolean {
        // if no initial data/current state that means initialise havent been called
        val initialData = initialDataStartedWith ?: return false
        val currentState = _state.value ?: return false

        val initialExercises = initialData.exercises
        val currentExercises = currentState.exercises

        return (initialExercises != currentExercises)
    }
}

private data class WorkoutTrackManagerInternalState(
    val exercises: List<ExercisePlanInfoAppModel>,
    val editableEntry: WorkoutEditableEntry?
)
