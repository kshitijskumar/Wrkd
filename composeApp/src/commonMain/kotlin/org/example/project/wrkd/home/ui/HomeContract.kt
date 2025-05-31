package org.example.project.wrkd.home.ui

data class HomeState(
    val homeInfoCards: List<HomeInfoCard> = listOf()
)

sealed class HomeIntent {
    data object InitializationIntent : HomeIntent()

    data object DummyClickIntent: HomeIntent()
}

sealed class HomeInfoCard {

    data class CurrentDayWorkoutDetails(
        val info: List<WorkoutInfo>
    ) : HomeInfoCard() {
        data class WorkoutInfo(
            val duration: Long,
            val totalExercises: Int
        )
    }

    data class WeeklyWorkoutSummary(
        val numberOfDaysWorkedOut: Int,
        val totalPlannedDays: Int,
        val totalWorkoutDuration: Long
    ) : HomeInfoCard()

}