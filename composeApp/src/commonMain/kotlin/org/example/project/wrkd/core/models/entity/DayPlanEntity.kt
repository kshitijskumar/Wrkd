package org.example.project.wrkd.core.models.entity

import org.example.project.wrkd.core.models.WeekDay
import org.example.project.wrkd.core.models.app.ExerciseResistanceMethod

sealed class DayPlanEntity {

    abstract val id: String
    abstract val day: WeekDay

    data class RestDayEntity(
        override val id: String,
        override val day: WeekDay
    ) : DayPlanEntity()

    data class WorkDayEntity(
        override val id: String,
        override val day: WeekDay,
        val dayName: String,
        val startedAt: Long,
        val workoutDuration: Long,
        val exercises: List<ExercisePlanInfoEntity>
    ) : DayPlanEntity()

}

data class ExercisePlanInfoEntity(
    val name: String,
    val exerciseId: String,
    val exercisePerformedAt: Long,
    val sets: List<ExerciseSetInfoEntity>
)

data class ExerciseSetInfoEntity(
    val setId: String,
    val repsCount: Int,
    val resistanceMethod: ExerciseResistanceMethod,
    val setPerformedAt: Long,
    val additionalWeight: Long
)