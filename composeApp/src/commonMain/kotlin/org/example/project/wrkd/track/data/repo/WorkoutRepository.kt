package org.example.project.wrkd.track.data.repo

import kotlinx.coroutines.flow.Flow
import org.example.project.wrkd.core.models.WeekDay
import org.example.project.wrkd.core.models.app.DayPlanAppModel

interface WorkoutRepository {

    suspend fun saveWorkoutDetails(workout: DayPlanAppModel)

    fun getWorkoutBetweenTimestamp(lower: Long, upper: Long): Flow<List<DayPlanAppModel>>

    fun getDistinctExerciseNameForDayBetweenTimestamps(
        day: WeekDay,
        start: Long,
        end: Long
    ): Flow<List<String>>

}