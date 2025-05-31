package org.example.project.wrkd.home.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import org.example.project.wrkd.core.models.app.DayPlanAppModel
import org.example.project.wrkd.track.data.repo.WorkoutRepository

class GetAllWorkoutBetweenGivenTimestampUseCase(
    private val repository: WorkoutRepository
) {

    operator fun invoke(
        startTime: Long,
        endTime: Long
    ): Flow<List<DayPlanAppModel>> {
        return repository.getWorkoutBetweenTimestamp(startTime, endTime)
    }

    fun forCertainDay(
        time: Long
    ): Flow<List<DayPlanAppModel>> {
        val timezone = TimeZone.currentSystemDefault()
        val givenLocalDateTime = Instant
            .fromEpochMilliseconds(time)
            .toLocalDateTime(timezone)

        val dayStart = givenLocalDateTime.date.atTime(hour = 0, minute = 0, second = 0)
        val dayEnd = givenLocalDateTime.date.atTime(hour = 23, minute = 59, second = 59)

        return invoke(
            startTime = dayStart.toInstant(timezone).toEpochMilliseconds(),
            endTime = dayEnd.toInstant(timezone).toEpochMilliseconds()
        )
    }

}