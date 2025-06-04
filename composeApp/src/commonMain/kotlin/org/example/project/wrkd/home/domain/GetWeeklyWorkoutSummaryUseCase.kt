package org.example.project.wrkd.home.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.example.project.wrkd.core.models.app.DayPlanAppModel
import org.example.project.wrkd.home.ui.HomeInfoCard
import org.example.project.wrkd.utils.AppConstants
import org.example.project.wrkd.utils.TimeUtils

class GetWeeklyWorkoutSummaryUseCase(
    private val timeUtils: TimeUtils,
    private val getAllWorkoutBetweenGivenTimestampUseCase: GetAllWorkoutBetweenGivenTimestampUseCase
) {

    operator fun invoke(
        weeklySummaryForDay: Long
    ): Flow<HomeInfoCard.WeeklyWorkoutSummary> {
        val weekInfo = timeUtils.getWeekStartAndEndForTime(weeklySummaryForDay)
        return getAllWorkoutBetweenGivenTimestampUseCase.invoke(
            startTime = weekInfo.first,
            endTime = weekInfo.second
        ).map { workoutList ->
            val weeklyWorkout = workoutList.groupBy { it.day }

            var numberOfDaysWorkedOut = 0
            var totalWorkoutDuration: Long = 0

            weeklyWorkout.values.forEach { workoutsInDay ->
                var hasHandledForThisDay = false
                workoutsInDay.forEach {
                    when(it) {
                        is DayPlanAppModel.RestDay -> {}
                        is DayPlanAppModel.WorkDay -> {
                            if (!hasHandledForThisDay) {
                                numberOfDaysWorkedOut++
                                hasHandledForThisDay = true
                            }
                            totalWorkoutDuration += it.workoutDuration
                        }
                    }
                }
            }

            HomeInfoCard.WeeklyWorkoutSummary(
                numberOfDaysWorkedOut = numberOfDaysWorkedOut,
                totalWorkoutDuration = totalWorkoutDuration,
                totalPlannedDays = AppConstants.TOTAL_WORKOUT_PLANNED_DAYS,
                formattedDuration = timeUtils.durationMillisToHourMinute(totalWorkoutDuration)
            )
        }
    }



}