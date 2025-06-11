package org.example.project.wrkd.home.ui

import org.example.project.wrkd.core.models.WeekDay
import org.example.project.wrkd.home.domain.WorkoutListSection
import org.example.project.wrkd.utils.DayTimeRange

data class HomeState(
    val title: String = "",
    val initializationTime: Long? = null,
    val week: WeekDay? = null,
    val dayRange: DayTimeRange? = null,
    val homeInfoCards: List<HomeInfoCard> = listOf(),
    val workoutInfoSections: List<WorkoutListSection>? = null
)

sealed class HomeIntent {
    data object InitializationIntent : HomeIntent()

    data object AddWorkoutClickedIntent: HomeIntent()
}

sealed class HomeInfoCard {

    data class CurrentDayWorkoutDetails(
        val today: String,
        val info: List<WorkoutInfo>
    ) : HomeInfoCard() {
        data class WorkoutInfo(
            val workoutId: String,
            val workoutName: String,
            val duration: Long,
            val formattedDuration: String,
            val totalExercises: Int
        )
    }

    data class WeeklyWorkoutSummary(
        val numberOfDaysWorkedOut: Int,
        val totalPlannedDays: Int,
        val totalWorkoutDuration: Long,
        val formattedDuration: String
    ) : HomeInfoCard()

}