package org.example.project.wrkd.track.data.local

import org.example.project.wrkd.core.models.entity.DayPlanEntity

interface WorkoutLocalDataSource {

    suspend fun saveWorkout(workoutDetails: DayPlanEntity)

}