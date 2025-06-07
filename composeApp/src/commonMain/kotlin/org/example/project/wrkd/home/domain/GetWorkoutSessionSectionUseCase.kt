package org.example.project.wrkd.home.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.example.project.wrkd.core.models.app.DayPlanAppModel
import org.example.project.wrkd.core.models.app.ExercisePlanInfoAppModel
import org.example.project.wrkd.utils.System
import org.example.project.wrkd.utils.TimeUtils

class GetWorkoutSessionSectionUseCase(
    private val getAllWorkoutBetweenGivenTimestampUseCase: GetAllWorkoutBetweenGivenTimestampUseCase,
    private val timeUtils: TimeUtils
) {

    fun invoke(sectionLogic: SectionLogic): Flow<List<WorkoutListSection>> {
        val timeRangeForLogic = timeRangeForLogic(sectionLogic)
        return getAllWorkoutBetweenGivenTimestampUseCase.invoke(
            startTime = timeRangeForLogic.first,
            endTime = timeRangeForLogic.second
        ).map {
            val finalResult = when(sectionLogic) {
                SectionLogic.TodayAndCurrentWeek -> todayAndCurrentWeekSection(it)
//                SectionLogic.Weekly -> TODO()
//                SectionLogic.Monthly -> TODO()
            }

            finalResult.cleanAndReturnOnlyValidSection()
        }
    }

    private fun todayAndCurrentWeekSection(
        list: List<DayPlanAppModel>
    ): List<WorkoutListSection> {
        val today = mutableListOf<DayPlanAppModel.WorkDay>()
        val restOfTheWeek = mutableListOf<DayPlanAppModel.WorkDay>()

        list.forEach {
            when(it) {
                is DayPlanAppModel.RestDay -> {}
                is DayPlanAppModel.WorkDay -> {
                    if (timeUtils.isToday(it.startedAt)) {
                        today.add(it)
                    } else {
                        restOfTheWeek.add(it)
                    }
                }
            }
        }

        val todaySection = WorkoutListSection(
            title = "Today",
            workoutSessionsList = today.sortedByDescending { it.startedAt }.map(::toSessionInfo)
        )

        val restOfTheWeekSection = WorkoutListSection(
            title = "This Week",
            workoutSessionsList = restOfTheWeek.sortedByDescending { it.startedAt }.map(::toSessionInfo)
        )

        return listOf(todaySection, restOfTheWeekSection)
    }

    private fun List<WorkoutListSection>.cleanAndReturnOnlyValidSection(): List<WorkoutListSection> {
        return this.filter {
            it.workoutSessionsList.isNotEmpty()
        }
    }

    private fun toSessionInfo(info: DayPlanAppModel.WorkDay): WorkoutSessionInfo {
        return WorkoutSessionInfo(
            workoutId = info.id,
            workoutName = info.dayName,
            startedAt = info.startedAt,
            duration = info.workoutDuration,
            exercises = info.exercises
        )
    }

    private fun timeRangeForLogic(logic: SectionLogic): Pair<Long, Long> {
        return when(logic) {
            SectionLogic.TodayAndCurrentWeek -> {
                timeUtils.getWeekStartAndEndForTime(System.currentTimeInMillis)
            }
//            SectionLogic.Weekly,
//            SectionLogic.Monthly -> (0L to Long.MAX_VALUE)
        }
    }

}

enum class SectionLogic {
    TodayAndCurrentWeek,
    // implement them when needed
//    Weekly,
//    Monthly
}

data class WorkoutListSection(
    val title: String,
    val workoutSessionsList: List<WorkoutSessionInfo>
)

data class WorkoutSessionInfo(
    val workoutId: String,
    val workoutName: String,
    val startedAt: Long,
    val duration: Long,
    val exercises: List<ExercisePlanInfoAppModel>
)