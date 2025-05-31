package org.example.project.wrkd.track.data.repo

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.example.project.wrkd.core.mappers.toAppModel
import org.example.project.wrkd.core.mappers.toEntity
import org.example.project.wrkd.core.models.app.DayPlanAppModel
import org.example.project.wrkd.track.data.local.WorkoutLocalDataSource

class WorkoutRepositoryImpl(
    private val localDataSource: WorkoutLocalDataSource
) : WorkoutRepository {

    override suspend fun saveWorkoutDetails(workout: DayPlanAppModel) {
        localDataSource.saveWorkout(workout.toEntity())
    }

    override fun getWorkoutBetweenTimestamp(lower: Long, upper: Long): Flow<List<DayPlanAppModel>> {
        return localDataSource.getWorkoutBetweenTimestamps(lower, upper)
            .map { list ->
                list.map { it.toAppModel() }
            }
    }
}