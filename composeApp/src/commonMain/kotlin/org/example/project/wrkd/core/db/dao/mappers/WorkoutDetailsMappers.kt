package org.example.project.wrkd.core.db.dao.mappers

import org.example.project.wrkd.core.models.entity.DayPlanEntity
import org.example.project.wrkd.core.models.entity.ExercisePlanInfoEntity
import org.example.project.wrkd.core.models.entity.ExerciseSetInfoEntity
import srccommonMainsqldelightAppDatabase.Exercise_table
import srccommonMainsqldelightAppDatabase.Set_table
import srccommonMainsqldelightAppDatabase.Workout_day_table

fun DayPlanEntity.WorkDayEntity.toWorkoutDayTable(): Workout_day_table {
    return Workout_day_table(
        id = id,
        day = day,
        dayName = dayName,
        startedAt = startedAt,
        workoutDuration = workoutDuration
    )
}

fun ExercisePlanInfoEntity.toExerciseTable(workoutId: String): Exercise_table {
    return Exercise_table(
        id = this.exerciseId,
        name = this.name,
        workoutId = workoutId,
        exercisePerformedAt = exercisePerformedAt
    )
}

fun ExerciseSetInfoEntity.toSetTable(exerciseId: String): Set_table {
    return Set_table(
        id = setId,
        repsCount = repsCount.toLong(),
        resistanceMethod = resistanceMethod,
        additionalWeight = additionalWeight,
        exerciseId = exerciseId,
        setPerformedAt = setPerformedAt
    )
}