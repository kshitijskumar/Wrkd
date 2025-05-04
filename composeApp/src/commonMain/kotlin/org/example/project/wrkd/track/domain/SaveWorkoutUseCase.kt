package org.example.project.wrkd.track.domain

import org.example.project.wrkd.core.models.WeekDay
import org.example.project.wrkd.core.models.app.DayPlanAppModel
import org.example.project.wrkd.core.models.app.ExercisePlanInfoAppModel
import org.example.project.wrkd.track.data.repo.WorkoutRepository
import org.example.project.wrkd.utils.KUUID

class SaveWorkoutUseCase(
    private val repository: WorkoutRepository
) {

    suspend operator fun invoke(
        workoutId: String = KUUID.generateId(),
        weekDay: WeekDay,
        dayName: String,
        startedAt: Long,
        duration: Long,
        exercises: List<ExercisePlanInfoAppModel>
    ) {
        val workout = DayPlanAppModel.WorkDay(
            id = workoutId,
            day = weekDay,
            dayName = dayName,
            startedAt = startedAt,
            workoutDuration = duration,
            exercises = exercises
        )

        repository.saveWorkoutDetails(workout)
    }

}