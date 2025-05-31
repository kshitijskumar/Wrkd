package org.example.project.wrkd.track.data.local

import kotlinx.coroutines.flow.Flow
import org.example.project.wrkd.core.models.entity.DayPlanEntity

interface WorkoutLocalDataSource {

    suspend fun saveWorkout(workoutDetails: DayPlanEntity)

    fun getWorkoutBetweenTimestamps(
        lower: Long,
        upper: Long
    ) : Flow<List<DayPlanEntity>>

}