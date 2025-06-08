package org.example.project.wrkd.track.domain

import kotlinx.coroutines.flow.Flow
import org.example.project.wrkd.core.models.WeekDay
import org.example.project.wrkd.track.data.repo.WorkoutRepository
import org.example.project.wrkd.utils.TimeUtils

class GetDistinctExercisesForDayBetweenTimestampUseCase(
    private val repository: WorkoutRepository,
    private val timeUtils: TimeUtils
) {

    operator fun invoke(
        day: WeekDay,
        start: Long,
        end: Long
    ): Flow<List<String>> {
        return repository.getDistinctExerciseNameForDayBetweenTimestamps(
            day = day,
            start = start,
            end = end
        )
    }

    fun forGivenDayInLastMonth(time: Long): Flow<List<String>> {
        val day = timeUtils.getWeekFromTime(time) ?: WeekDay.Mon // shouldn't be null
        val start = time - (30 * TimeUtils.MILLIS_IN_A_DAY)

        return invoke(day = day, start = start, end = time)
    }

}