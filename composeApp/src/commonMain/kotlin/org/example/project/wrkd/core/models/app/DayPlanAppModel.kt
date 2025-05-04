package org.example.project.wrkd.core.models.app

import org.example.project.wrkd.core.models.WeekDay
import org.example.project.wrkd.utils.KUUID
import kotlin.jvm.JvmInline
import kotlin.math.min

sealed class DayPlanAppModel {

    abstract val id: String
    abstract val day: WeekDay

    data class RestDay(
        override val id: String,
        override val day: WeekDay
    ) : DayPlanAppModel()

    data class WorkDay(
        override val id: String,
        override val day: WeekDay,
        val dayName: String,
        val startedAt: Long,
        val workoutDuration: Long,
        val exercises: List<ExercisePlanInfoAppModel>
    ) : DayPlanAppModel()

}

data class ExercisePlanInfoAppModel(
    val name: String,
    val exerciseId: String,
    val sets: List<ExerciseSetInfoAppModel>
)

data class ExerciseSetInfoAppModel(
    val setId: String,
    val repsCount: Int,
    val resistanceMethod: ExerciseResistanceMethod,
    val additionalWeight: WeightInGrams
) {
    companion object {
        fun defaultSet(): ExerciseSetInfoAppModel {
            return ExerciseSetInfoAppModel(
                setId = KUUID.generateId(),
                repsCount = 1,
                resistanceMethod = ExerciseResistanceMethod.BODY,
                additionalWeight = WeightInGrams.fromKg(0.0)
            )
        }
    }
}

fun List<ExercisePlanInfoAppModel>.updateSetInfo(
    exerciseId: String,
    setId: String,
    update: (ExerciseSetInfoAppModel) -> ExerciseSetInfoAppModel
): List<ExercisePlanInfoAppModel> {
    return this.map { exercise ->
        if (exercise.exerciseId == exerciseId) {
            exercise.copy(
                sets = exercise.sets.map { set ->
                    if (set.setId == setId) {
                        update.invoke(set)
                    } else {
                        set
                    }
                }
            )
        } else {
            exercise
        }
    }
}

@JvmInline
value class WeightInGrams(val value: Long) {
    companion object {
        fun fromKg(value: Double): WeightInGrams {
            return WeightInGrams((value * 1000).toLong())
        }
    }
}

fun WeightInGrams.toKg(): Double {
    return this.value / 1000.0
}

fun WeightInGrams.displayString(): String {
    val weightInKg = this.toKg()
    return if (weightInKg <= 0.0) {
        ""
    } else {
        var str = weightInKg.toString()
        val decimalIndex = str.indexOfFirst { it == '.' }
        if (decimalIndex == -1) {
            str
        } else {
            val substringEnd = min(str.length, decimalIndex + 3)
            str = str.substring(0, substringEnd)
            while (str.isNotEmpty() && str.last() == '0') {
                str = str.substring(0, str.length - 1)
            }
            if (str.last() == '.') {
                str = str.substring(0, str.length - 1)
            }
           str
        }
    }
}

fun WeightInGrams.isValid() = value >= 0L

enum class ExerciseResistanceMethod {
    BODY,
    WEIGHT
}

val ExerciseResistanceMethod.resistanceMethodName: String
    get() {
        return when(this) {
            ExerciseResistanceMethod.BODY -> "Body"
            ExerciseResistanceMethod.WEIGHT -> "Weights"
        }
    }
