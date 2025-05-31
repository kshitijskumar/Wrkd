package org.example.project.wrkd.track.data.local

import kotlinx.coroutines.flow.Flow
import org.example.project.wrkd.core.models.entity.DayPlanEntity
import org.example.project.wrkd.track.data.dao.WorkoutDao

class WorkoutLocalDataSourceImpl(
    private val dao: WorkoutDao
) : WorkoutLocalDataSource {

    override suspend fun saveWorkout(workoutDetails: DayPlanEntity) {
        dao.insertWorkoutDetails(workoutDetails)
    }

    override fun getWorkoutBetweenTimestamps(lower: Long, upper: Long): Flow<List<DayPlanEntity>> {
        return dao.getWorkoutBetweenTimestamps(lowerLimit = lower, upperLimit = upper)
    }
}