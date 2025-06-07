package org.example.project.wrkd.home.ui

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.example.project.wrkd.core.models.app.DayPlanAppModel
import org.example.project.wrkd.core.navigation.AppNavigator
import org.example.project.wrkd.core.navigation.args.WorkoutTrackingArgs
import org.example.project.wrkd.core.ui.BaseViewModel
import org.example.project.wrkd.home.domain.GetAllWorkoutBetweenGivenTimestampUseCase
import org.example.project.wrkd.home.domain.GetWeeklyWorkoutSummaryUseCase
import org.example.project.wrkd.home.domain.GetWorkoutSessionSectionUseCase
import org.example.project.wrkd.home.domain.SectionLogic
import org.example.project.wrkd.home.domain.WorkoutListSection
import org.example.project.wrkd.home.domain.WorkoutSessionInfo
import org.example.project.wrkd.utils.DayTimeRange
import org.example.project.wrkd.utils.System
import org.example.project.wrkd.utils.TimeUtils

class HomeViewModel(
    private val getAllWorkoutBetweenGivenTimestampUseCase: GetAllWorkoutBetweenGivenTimestampUseCase,
    private val getWeeklyWorkoutSummaryUseCase: GetWeeklyWorkoutSummaryUseCase,
    private val appNavigator: AppNavigator,
    private val timeUtils: TimeUtils,
    private val getWorkoutSessionSectionUseCase: GetWorkoutSessionSectionUseCase
) : BaseViewModel<HomeState, HomeIntent>() {

    override fun initialData() = HomeState()

    init {
        processIntent(HomeIntent.InitializationIntent)
    }

    override fun processIntent(intent: HomeIntent) {
        when(intent) {
            HomeIntent.InitializationIntent -> handleInitializationIntent()
            HomeIntent.AddWorkoutClickedIntent -> {
                appNavigator.navigate(WorkoutTrackingArgs)
            }
        }
    }

    private fun handleInitializationIntent() = viewModelScope.launch {
        updateState {
            it.copy(title = getGreetings())
        }
        launch {
            initialiseHomeInfoCards()
        }
        launch {
            initialiseThisWeekWorkoutSection()
        }
    }

    private suspend fun initialiseHomeInfoCards() {
        combine(
            flow = currentDayInfo(),
            flow2 = weeklySummaryInfo()
        ) { currentDayInfo, weeklySummary ->
            listOf(currentDayInfo, weeklySummary)
        }.collect { homeInfoCards ->
            updateState {
                it.copy(
                    homeInfoCards = homeInfoCards
                )
            }
        }
    }

    private suspend fun initialiseThisWeekWorkoutSection() {
        getWorkoutWeeklySessionsSections().collect { sections ->
            updateState {
                it.copy(
                    workoutInfoSections = sections
                )
            }
        }
    }

    private fun getGreetings(): String {
        val currentHour = timeUtils.getHourOfDay(System.currentTimeInMillis)
        return when (timeUtils.getDayTimeRange(currentHour)) {
            DayTimeRange.MORNING -> "Good Morning!"
            DayTimeRange.AFTERNOON -> "Good Afternoon!"
            DayTimeRange.EVENING,
            DayTimeRange.NIGHT -> "Good Evening!"
            null -> "Hello!"
        }
    }

    private fun currentDayInfo(): Flow<HomeInfoCard.CurrentDayWorkoutDetails> {
        return getAllWorkoutBetweenGivenTimestampUseCase.forCertainDay(System.currentTimeInMillis)
            .map { result ->
                val workoutInfoList = result.mapNotNull {
                    when (it) {
                        is DayPlanAppModel.RestDay -> null
                        is DayPlanAppModel.WorkDay -> {
                            if (it.exercises.isEmpty()) {
                                null
                            } else {
                                HomeInfoCard.CurrentDayWorkoutDetails.WorkoutInfo(
                                    workoutId = it.id,
                                    workoutName = it.dayName,
                                    duration = it.workoutDuration,
                                    formattedDuration = timeUtils.durationMillisToHourMinute(it.workoutDuration),
                                    totalExercises = it.exercises.size
                                )
                            }
                        }
                    }
                }

                HomeInfoCard.CurrentDayWorkoutDetails(
                    info = workoutInfoList,
                    today = getTodayInfoForCurrentDayCard()
                )
            }
    }

    private fun getTodayInfoForCurrentDayCard(): String {
        return timeUtils.getDateMonth(System.currentTimeInMillis)
    }

    private fun weeklySummaryInfo(): Flow<HomeInfoCard.WeeklyWorkoutSummary> {
        return getWeeklyWorkoutSummaryUseCase.invoke(System.currentTimeInMillis)
    }

    private fun getWorkoutWeeklySessionsSections(): Flow<List<WorkoutListSection>> {
        return getWorkoutSessionSectionUseCase.invoke(
            sectionLogic = SectionLogic.TodayAndCurrentWeek
        )
    }
}