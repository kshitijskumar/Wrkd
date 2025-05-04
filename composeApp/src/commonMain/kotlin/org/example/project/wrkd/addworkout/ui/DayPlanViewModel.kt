package org.example.project.wrkd.addworkout.ui

import org.example.project.wrkd.addworkout.WorkoutRoutinePlanner
import org.example.project.wrkd.core.models.app.DayPlanAppModel
import org.example.project.wrkd.core.models.app.WeightInGrams
import org.example.project.wrkd.core.ui.BaseViewModel
import org.example.project.wrkd.navigation.args.DayPlanArgs
import org.example.project.wrkd.utils.KUUID

class DayPlanViewModel(
    args: DayPlanArgs,
    private val workoutRoutinePlanner: WorkoutRoutinePlanner
) : BaseViewModel<DayPlanDataState, DayPlanIntent>() {

    override fun initialData() = DayPlanDataState()

    init {
        processIntent(DayPlanIntent.InitializationIntent(args.day))
    }

    override fun processIntent(intent: DayPlanIntent) {
//        when(intent) {
//            is DayPlanIntent.InitializationIntent -> handleInitializationIntent(intent)
//            is DayPlanIntent.DayNameEnteredIntent -> handleDayNameEnteredIntent(intent)
//            is DayPlanIntent.ExerciseNameEnteredIntent -> handleExerciseNameEnteredIntent(intent)
//            is DayPlanIntent.ExerciseSetCountEnteredIntent -> handleExerciseSetCountEnteredIntent(intent)
//            is DayPlanIntent.AddNewSetIntent -> handleAddNewSetIntent(intent)
//            is DayPlanIntent.SetRepsCountEnteredIntent -> handleSetRepsCountEnteredIntent(intent)
//            is DayPlanIntent.SetResistanceMethodEnteredIntent -> handleSetResistanceMethodEnteredIntent(intent)
//            is DayPlanIntent.SetAdditionalWeightEnteredIntent -> handleSetAdditionalWeightEnteredIntent(intent)
//            is DayPlanIntent.DeleteSetIntent -> handleDeleteSetIntent(intent)
//            DayPlanIntent.AddExerciseIntent -> handleAddExerciseIntent()
//            is DayPlanIntent.DeleteExerciseIntent -> handleDeleteExerciseIntent(intent)
//            DayPlanIntent.SaveDayPlanIntent -> handleSaveDayPlanIntent()
//        }
    }

//    private fun handleInitializationIntent(intent: DayPlanIntent.InitializationIntent) {
//        updateState {
//            it.copy(
//                day = intent.day,
//                dayPlanName = "",
//                exercisesList = listOf(ExercisePlanEntryInfo.create()), // starting with an empty placeholder
//            )
//        }
//    }
//
//    private fun handleDayNameEnteredIntent(intent: DayPlanIntent.DayNameEnteredIntent) {
//        updateState {
//            it.copy(
//                dayPlanName = intent.dayName
//            )
//        }
//    }
//
//    private fun handleExerciseNameEnteredIntent(intent: DayPlanIntent.ExerciseNameEnteredIntent) {
//        val exercises = getMutableExercisesMap()
//        exercises[intent.exerciseId]?.let { relevantExercise ->
//            exercises[intent.exerciseId] = relevantExercise.copy(name = intent.name)
//        }
//        val finalList = exercises.values.toList()
//
//        updateState {
//            it.copy(
//                exercisesList = finalList,
//                shouldEnableAddNextExerciseBtn = shouldEnableAddExerciseBtn(finalList)
//            )
//        }
//    }
//
//    private fun handleExerciseSetCountEnteredIntent(intent: DayPlanIntent.ExerciseSetCountEnteredIntent) {
//        val exercises = getMutableExercisesMap()
//        exercises[intent.exerciseId]?.let { relevantExercise ->
//            val setToCopy = if (relevantExercise.areAllSetsSame) {
//                relevantExercise.sets.firstOrNull() ?: SetPlanEntryInfo.create() // ideally will never be null since we create an entry in list at the start
//            } else {
//                // if all sets are not same, then create a fresh one to copy
//                SetPlanEntryInfo.create()
//            }
//            val newSets = (0 until intent.setCount).map { SetPlanEntryInfo.createNewUsing(setToCopy) }
//
//            exercises[intent.exerciseId] = relevantExercise.copy(sets = newSets)
//        }
//
//        val finalList = exercises.values.toList()
//        updateState {
//            it.copy(
//                exercisesList = finalList,
//                shouldEnableSaveBtn = shouldEnableSaveBtn(finalList)
//            )
//        }
//    }
//
//    private fun handleAddNewSetIntent(intent: DayPlanIntent.AddNewSetIntent) {
//        val exercises = getMutableExercisesMap()
//        exercises[intent.exerciseId]?.let { relevant ->
//            exercises[intent.exerciseId] = relevant.copy(
//                sets = relevant.sets.toMutableList().apply {
//                    add(SetPlanEntryInfo.create())
//                }
//            )
//        }
//
//        val finalList = exercises.values.toList()
//        updateState {
//            it.copy(
//                exercisesList = finalList,
//                shouldEnableSaveBtn = shouldEnableSaveBtn(finalList)
//            )
//        }
//    }
//
//    private fun handleSetRepsCountEnteredIntent(intent: DayPlanIntent.SetRepsCountEnteredIntent) {
//        val finalList = updateSets(
//            exerciseId = intent.exerciseId,
//            setId = intent.setId,
//            update = {
//                it.copy(repsCount = intent.repsCount)
//            }
//        )
//
//        updateState {
//            it.copy(
//                exercisesList = finalList,
//                shouldEnableSaveBtn = shouldEnableSaveBtn(finalList)
//            )
//        }
//    }
//
//    private fun handleSetResistanceMethodEnteredIntent(intent: DayPlanIntent.SetResistanceMethodEnteredIntent) {
//        val finalList = updateSets(
//            exerciseId = intent.exerciseId,
//            setId = intent.setId,
//            update = {
//                it.copy(resistanceMethod = intent.resistanceMethod)
//            }
//        )
//
//        updateState {
//            it.copy(
//                exercisesList = finalList,
//                shouldEnableSaveBtn = shouldEnableSaveBtn(finalList)
//            )
//        }
//    }
//
//    private fun handleSetAdditionalWeightEnteredIntent(intent: DayPlanIntent.SetAdditionalWeightEnteredIntent) {
//        val finalList = updateSets(
//            exerciseId = intent.exerciseId,
//            setId = intent.setId,
//            update = {
//                it.copy(additionalWeight = WeightInGrams.fromKg(intent.additionalWeight))
//            }
//        )
//
//        updateState {
//            it.copy(
//                exercisesList = finalList,
//                shouldEnableSaveBtn = shouldEnableSaveBtn(finalList)
//            )
//        }
//    }
//
//    private fun handleDeleteSetIntent(intent: DayPlanIntent.DeleteSetIntent) {
//        val exercises = getMutableExercisesMap()
//        exercises[intent.exerciseId]?.let { relevant ->
//            val finalSets = relevant.sets
//                .filter { it.id != intent.setId }
//                .ifEmpty { listOf(SetPlanEntryInfo.create()) } // ideally shouldn't be empty, but if it is, have a empty placeholder
//
//            exercises[intent.exerciseId] = relevant.copy(sets = finalSets)
//        }
//
//        val finalList = exercises.values.toList()
//
//        updateState {
//            it.copy(
//                exercisesList = finalList,
//                shouldEnableSaveBtn = shouldEnableSaveBtn(finalList)
//            )
//        }
//    }
//
//    private fun handleAddExerciseIntent() {
//        val isExerciseEntryAdditionAllowed = currentState.exercisesList
//            .isMoreThanGivenEntriesOnlyPlaceholder(EXERCISE_COUNT_CONSIDERED_AS_PLACEHOLDER)
//
//        if (!isExerciseEntryAdditionAllowed) {
//            return
//        }
//
//        val exercises = getMutableExercisesMap()
//        val newExercisePlaceholder = ExercisePlanEntryInfo.create()
//        exercises[newExercisePlaceholder.id] = newExercisePlaceholder
//
//        val finalList = exercises.values.toList()
//        updateState {
//            it.copy(
//                exercisesList = finalList,
//                shouldEnableAddNextExerciseBtn = shouldEnableAddExerciseBtn(finalList),
//                shouldEnableSaveBtn = shouldEnableSaveBtn(finalList)
//            )
//        }
//    }
//
//    private fun handleDeleteExerciseIntent(intent: DayPlanIntent.DeleteExerciseIntent) {
//        updateState {
//            val updatedExercises = it.exercisesList.filter { it.id != intent.exerciseId }
//            it.copy(
//                exercisesList = updatedExercises,
//                shouldEnableAddNextExerciseBtn = shouldEnableAddExerciseBtn(updatedExercises),
//                shouldEnableSaveBtn = shouldEnableSaveBtn(updatedExercises)
//            )
//        }
//    }
//
//    private fun handleSaveDayPlanIntent() {
//        if (!shouldEnableSaveBtn(currentState.exercisesList)) {
//            return // ideally should not happen as we only enable the save btn after checking this
//        }
//
//        try {
//            val dayPlan = DayPlanAppModel.WorkDay(
//                id = KUUID.generateId(),
//                day = currentState.day ?: return, // shouldn't be null since we set this in initialisation
//                dayName = currentState.dayPlanName,
//                exercises = currentState.exercisesList.map { it.toAppModel() }
//            )
//
//
//
//        } catch (e: IllegalStateException) {
//            updateState {
//                it.copy(
//                    error = e.message
//                )
//            }
//        } catch (e: Exception) {
//            updateState {
//                it.copy(
//                    error = "Something went wrong"
//                )
//            }
//        }
//
//    }
//
//    /**
//     * Returns the updated list of exercises with the set updated.
//     * - Case 1: when setId is null -> call update function for all sets of given exercise
//     * - Case 2: when setId is not null -> call update function for the given set
//     */
//    private fun updateSets(
//        exerciseId: String,
//        setId: String?,
//        update: (SetPlanEntryInfo) -> SetPlanEntryInfo
//    ): List<ExercisePlanEntryInfo> {
//        val exercises = getMutableExercisesMap()
//        exercises[exerciseId]?.let { relevant ->
//            val newSets = if (setId == null) {
//                // call update for all sets
//                relevant.sets.map(update)
//            } else {
//                relevant.sets.map {
//                    if (it.id == setId) {
//                        update(it)
//                    } else {
//                        it
//                    }
//                }
//            }
//
//            exercises[exerciseId] = relevant.copy(sets = newSets)
//        }
//
//        return exercises.values.toList()
//    }
//
//    private fun getMutableExercisesMap(): MutableMap<String, ExercisePlanEntryInfo> {
//        return currentState.exercisesList.associateBy { it.id }.toMutableMap()
//    }
//
//    private fun shouldEnableAddExerciseBtn(
//        exercises: List<ExercisePlanEntryInfo>
//    ): Boolean {
//        return !exercises.isMoreThanGivenEntriesOnlyPlaceholder(EXERCISE_COUNT_CONSIDERED_AS_PLACEHOLDER)
//    }
//
//    private fun shouldEnableSaveBtn(
//        exercises: List<ExercisePlanEntryInfo>
//    ): Boolean {
//        return exercises.isNotEmpty() && exercises.all { it.areAllRequiredFieldValid() }
//    }
//
//    companion object {
//        private const val EXERCISE_COUNT_CONSIDERED_AS_PLACEHOLDER = 2
//    }
}