package org.example.project.wrkd.addworkout.ui

import androidx.compose.runtime.Immutable
import org.example.project.wrkd.core.models.app.ExercisePlanInfoAppModel
import org.example.project.wrkd.core.models.app.ExerciseResistanceMethod
import org.example.project.wrkd.core.models.app.ExerciseSetInfoAppModel
import org.example.project.wrkd.core.models.app.WeightInGrams
import org.example.project.wrkd.core.models.WeekDay
import org.example.project.wrkd.utils.KUUID

@Immutable
data class DayPlanDataState(
    val dayPlanName: String = "",
    val day: WeekDay? = null,
    val exercisesList: List<ExercisePlanEntryInfo> = listOf(ExercisePlanEntryInfo.create()),
    val shouldEnableSaveBtn: Boolean = false,
    val shouldEnableAddNextExerciseBtn: Boolean = false,
    val error: String? = null
)

sealed class DayPlanIntent {

    data class InitializationIntent(
        val day: WeekDay
    ) : DayPlanIntent()

    data class DayNameEnteredIntent(
        val dayName: String
    ) : DayPlanIntent()

    data class ExerciseNameEnteredIntent(
        val exerciseId: String,
        val name: String
    ) : DayPlanIntent()

    data class ExerciseSetCountEnteredIntent(
        val exerciseId: String,
        val setCount: Int
    ) : DayPlanIntent()

    data class AddNewSetIntent(
        val exerciseId: String
    ) : DayPlanIntent()

    /**
     * @param setId pass setId null if same repsCount should be applied to all sets for the given exercise
     */
    data class SetRepsCountEnteredIntent(
        val exerciseId: String,
        val setId: String?,
        val repsCount: Int
    ) : DayPlanIntent()

    data class SetResistanceMethodEnteredIntent(
        val exerciseId: String,
        val setId: String?,
        val resistanceMethod: ExerciseResistanceMethod
    ) : DayPlanIntent()

    data class SetAdditionalWeightEnteredIntent(
        val exerciseId: String,
        val setId: String?,
        val additionalWeight: Double
    ) : DayPlanIntent()

    data class DeleteSetIntent(
        val exerciseId: String,
        val setId: String
    ) : DayPlanIntent()

    data object AddExerciseIntent : DayPlanIntent()

    data class DeleteExerciseIntent(val exerciseId: String) : DayPlanIntent()

    data object SaveDayPlanIntent : DayPlanIntent()
}


data class ExercisePlanEntryInfo private constructor(
    val id: String,
    val name: String?,
    val sets: List<SetPlanEntryInfo>
) {
    companion object {
        fun create(): ExercisePlanEntryInfo {
            return ExercisePlanEntryInfo(
                id = KUUID.generateId(),
                name = null,
                sets = listOf(SetPlanEntryInfo.create())
            )
        }
    }

}

val ExercisePlanEntryInfo.shouldShowExpandSetOption: Boolean
    get() {
        return sets.size > 1
    }

val ExercisePlanEntryInfo.shouldShowDeleteSetOption: Boolean
    get() {
        return sets.size > 1
    }

/**
 * Exercises entries which do not have any name are considered placeholder.
 * If an entry contains names but not other info like sets or reps, they are not considered placeholder
 */
fun List<ExercisePlanEntryInfo>.isMoreThanGivenEntriesOnlyPlaceholder(count: Int): Boolean {
    var placeholderCount = 0
    for (i in this.lastIndex downTo 0) {
        val exercise = this[i]
        if (exercise.name.isNullOrEmpty()) {
            placeholderCount++
        }
        if (placeholderCount >= count) {
            return true
        }
    }

    return false
}

val ExercisePlanEntryInfo.areAllSetsSame: Boolean
    get() {
        if (this.sets.size <= 1) {
            return true
        }

        val first = this.sets.first()
        return this.sets.all { first.compareWith(it) }
    }

data class SetPlanEntryInfo(
    val id: String,
    val repsCount: Int,
    val resistanceMethod: ExerciseResistanceMethod = ExerciseResistanceMethod.BODY,
    val additionalWeight: WeightInGrams = WeightInGrams(0)
) {
    companion object {
        fun create(): SetPlanEntryInfo {
            return SetPlanEntryInfo(
                id = KUUID.generateId(),
                repsCount = 1
            )
        }

        fun createNewUsing(other: SetPlanEntryInfo): SetPlanEntryInfo {
            return other.copy(id = KUUID.generateId())
        }
    }
}

fun SetPlanEntryInfo.compareWith(other: SetPlanEntryInfo): Boolean {
    return this.repsCount == other.repsCount &&
            this.resistanceMethod == other.resistanceMethod &&
            this.additionalWeight.value == this.additionalWeight.value
}

fun ExerciseResistanceMethod.isWeightValid(weight: WeightInGrams): Boolean {
    return when(this) {
        ExerciseResistanceMethod.BODY -> weight.value >= 0
        ExerciseResistanceMethod.WEIGHT -> weight.value > 0 // for weighted move, it cant be 0, should be something
    }
}

fun SetPlanEntryInfo.areAllRequiredFieldsValid(): Boolean {
    return repsCount >= 1 &&
            this.resistanceMethod.isWeightValid(this.additionalWeight)
}

fun ExercisePlanEntryInfo.areAllRequiredFieldValid(): Boolean {
    return !name.isNullOrEmpty() && sets.all { it.areAllRequiredFieldsValid() }
}

@Throws(IllegalStateException::class)
fun ExercisePlanEntryInfo.toAppModel(): ExercisePlanInfoAppModel {
    return ExercisePlanInfoAppModel(
        name = if (!this.name.isNullOrEmpty()) this.name else throw IllegalStateException("Exercise name missing"),
        exerciseId = this.id,
        sets = this.sets.map { it.toAppModel() }
    )
}

fun SetPlanEntryInfo.toAppModel(): ExerciseSetInfoAppModel {
    return ExerciseSetInfoAppModel(
        setId = this.id,
        repsCount = this.repsCount,
        resistanceMethod = this.resistanceMethod,
        additionalWeight = this.additionalWeight
    )
}
