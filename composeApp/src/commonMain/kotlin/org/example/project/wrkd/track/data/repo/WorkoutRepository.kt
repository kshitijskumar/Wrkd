package org.example.project.wrkd.track.data.repo

import kotlinx.coroutines.flow.Flow
import org.example.project.wrkd.core.models.app.DayPlanAppModel

interface WorkoutRepository {

    suspend fun saveWorkoutDetails(workout: DayPlanAppModel)

    fun getWorkoutBetweenTimestamp(lower: Long, upper: Long): Flow<List<DayPlanAppModel>>

}