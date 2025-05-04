package org.example.project.wrkd.track.data.local

import org.example.project.wrkd.core.models.entity.DayPlanEntity
import org.example.project.wrkd.track.data.dao.WorkoutDao

class WorkoutLocalDataSourceImpl(
    private val dao: WorkoutDao
) : WorkoutLocalDataSource {

    override suspend fun saveWorkout(workoutDetails: DayPlanEntity) {
        dao.insertWorkoutDetails(workoutDetails)
    }
}