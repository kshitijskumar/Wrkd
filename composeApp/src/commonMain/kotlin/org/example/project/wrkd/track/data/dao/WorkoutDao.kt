package org.example.project.wrkd.track.data.dao

import kotlinx.coroutines.flow.Flow
import org.example.project.wrkd.core.models.WeekDay
import org.example.project.wrkd.core.models.entity.DayPlanEntity

interface WorkoutDao {

    fun getWorkoutDetailsById(workoutId: String): Flow<DayPlanEntity?>

    suspend fun insertWorkoutDetails(workoutDetails: DayPlanEntity)

    fun getWorkoutBetweenTimestamps(
        lowerLimit: Long,
        upperLimit: Long
    ): Flow<List<DayPlanEntity>>

    fun getDistinctExerciseNameForDayBetweenTimestamps(
        day: WeekDay,
        start: Long,
        end: Long
    ): Flow<List<String>>

}