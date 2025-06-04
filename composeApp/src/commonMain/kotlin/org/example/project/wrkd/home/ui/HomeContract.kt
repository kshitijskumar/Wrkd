package org.example.project.wrkd.home.ui

data class HomeState(
    val title: String = "",
    val homeInfoCards: List<HomeInfoCard> = listOf()
)

sealed class HomeIntent {
    data object InitializationIntent : HomeIntent()

    data object DummyClickIntent: HomeIntent()
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