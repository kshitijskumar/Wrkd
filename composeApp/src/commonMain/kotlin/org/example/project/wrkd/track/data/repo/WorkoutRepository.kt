package org.example.project.wrkd.track.data.repo

import org.example.project.wrkd.core.models.app.DayPlanAppModel

interface WorkoutRepository {

    suspend fun saveWorkoutDetails(workout: DayPlanAppModel)

}