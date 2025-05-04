package org.example.project.wrkd.core.mappers

import org.example.project.wrkd.core.models.app.DayPlanAppModel
import org.example.project.wrkd.core.models.app.ExercisePlanInfoAppModel
import org.example.project.wrkd.core.models.app.ExerciseSetInfoAppModel
import org.example.project.wrkd.core.models.app.WeightInGrams
import org.example.project.wrkd.core.models.entity.DayPlanEntity
import org.example.project.wrkd.core.models.entity.ExercisePlanInfoEntity
import org.example.project.wrkd.core.models.entity.ExerciseSetInfoEntity

fun DayPlanEntity.toAppModel(): DayPlanAppModel {
    return when(this) {
        is DayPlanEntity.RestDayEntity -> {
            DayPlanAppModel.RestDay(
                id = id,
                day = day
            )
        }
        is DayPlanEntity.WorkDayEntity -> {
            DayPlanAppModel.WorkDay(
                id = id,
                day = day,
                dayName = dayName,
                startedAt = startedAt,
                workoutDuration = workoutDuration,
                exercises = exercises.map { it.toAppModel() }
            )
        }
    }
}

private fun ExercisePlanInfoEntity.toAppModel(): ExercisePlanInfoAppModel {
    return ExercisePlanInfoAppModel(
        name = name,
        exerciseId = exerciseId,
        sets = sets.map { it.toAppModel() }
    )
}

private fun ExerciseSetInfoEntity.toAppModel(): ExerciseSetInfoAppModel {
    return ExerciseSetInfoAppModel(
        setId = setId,
        repsCount = repsCount,
        resistanceMethod = resistanceMethod,
        additionalWeight = WeightInGrams(additionalWeight)
    )
}

fun DayPlanAppModel.toEntity(): DayPlanEntity {
    return when(this) {
        is DayPlanAppModel.RestDay -> {
            DayPlanEntity.RestDayEntity(
                id = id,
                day = day
            )
        }
        is DayPlanAppModel.WorkDay -> {
            DayPlanEntity.WorkDayEntity(
                id = id,
                day = day,
                dayName = dayName,
                startedAt = startedAt,
                workoutDuration = workoutDuration,
                exercises = exercises.map { it.toEntity() }
            )
        }
    }
}

fun ExercisePlanInfoAppModel.toEntity(): ExercisePlanInfoEntity {
    return ExercisePlanInfoEntity(
        name = name,
        exerciseId = exerciseId,
        sets = sets.map { it.toEntity() }
    )
}

fun ExerciseSetInfoAppModel.toEntity(): ExerciseSetInfoEntity {
    return ExerciseSetInfoEntity(
        setId = setId,
        repsCount = repsCount,
        resistanceMethod = resistanceMethod,
        additionalWeight = additionalWeight.value
    )
}
