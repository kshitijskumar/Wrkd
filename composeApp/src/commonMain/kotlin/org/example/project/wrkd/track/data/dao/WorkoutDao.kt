package org.example.project.wrkd.track.data.dao

import kotlinx.coroutines.flow.Flow
import org.example.project.wrkd.core.models.entity.DayPlanEntity

interface WorkoutDao {

    fun getWorkoutDetailsById(workoutId: String): Flow<DayPlanEntity?>

    suspend fun insertWorkoutDetails(workoutDetails: DayPlanEntity)

}