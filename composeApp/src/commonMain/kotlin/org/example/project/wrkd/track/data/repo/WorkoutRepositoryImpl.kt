package org.example.project.wrkd.track.data.repo

import org.example.project.wrkd.core.mappers.toEntity
import org.example.project.wrkd.core.models.app.DayPlanAppModel
import org.example.project.wrkd.track.data.local.WorkoutLocalDataSource

class WorkoutRepositoryImpl(
    private val localDataSource: WorkoutLocalDataSource
) : WorkoutRepository {

    override suspend fun saveWorkoutDetails(workout: DayPlanAppModel) {
        localDataSource.saveWorkout(workout.toEntity())
    }
}