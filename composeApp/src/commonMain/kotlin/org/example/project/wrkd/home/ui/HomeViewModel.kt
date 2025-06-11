package org.example.project.wrkd.home.ui

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
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
import kotlin.math.abs

class HomeViewModel(
    private val getAllWorkoutBetweenGivenTimestampUseCase: GetAllWorkoutBetweenGivenTimestampUseCase,
    private val getWeeklyWorkoutSummaryUseCase: GetWeeklyWorkoutSummaryUseCase,
    private val appNavigator: AppNavigator,
    private val timeUtils: TimeUtils,
    private val getWorkoutSessionSectionUseCase: GetWorkoutSessionSectionUseCase
) : BaseViewModel<HomeState, HomeIntent>() {

    private var initializationJob: Job? = null
    override fun initialData() = HomeState()

    init {
        processIntent(HomeIntent.InitializationIntent)
    }

    override fun processIntent(intent: HomeIntent) {
        when(intent) {
            HomeIntent.InitializationIntent -> handleInitializationIntent()
            HomeIntent.AddWorkoutClickedIntent -> {
                appNavigator.navigate(WorkoutTrackingArgs.TrackingArgs)
            }
        }
    }

    private fun handleInitializationIntent() {
        initializationJob?.cancel()
        initializationJob = viewModelScope.launch {
            val currentTime = System.currentTimeInMillis
            val week = timeUtils.getWeekFromTime(currentTime)
            val dayRange = timeUtils.getDayTimeRange(timeUtils.getHourOfDay(currentTime))

            updateState {
                it.copy(
                    title = getGreetings(dayRange),
                    initializationTime = currentTime,
                    week = week,
                    dayRange = dayRange
                )
            }
            launch {
                initialiseHomeInfoCards()
            }
            launch {
                initialiseThisWeekWorkoutSection()
            }
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

    private fun getGreetings(dayTimeRange: DayTimeRange?): String {
        return when (dayTimeRange) {
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

    override fun viewStateActive() {
        if (shouldRefreshInitialization()) {
            processIntent(HomeIntent.InitializationIntent)
        }
    }

    private fun shouldRefreshInitialization(): Boolean {
        val currentState = currentState
        val previousInitializationTime = currentState.initializationTime
        val previousWeek = currentState.week
        val previousDayRange = currentState.dayRange

        if (previousInitializationTime == null && previousWeek == null && previousDayRange == null) {
            // no need to refresh initialization, first initialization will happen through init
            return false
        }

        val currentTime = System.currentTimeInMillis
        val currentDayRange = timeUtils.getDayTimeRange(timeUtils.getHourOfDay(currentTime))

        if (currentDayRange != previousDayRange) {
            // day range changed -> relevant for updating greetings
            return true
        }
        val currentWeek = timeUtils.getWeekFromTime(currentTime)

        if (currentWeek != previousWeek) {
            // week changed -> relevant for updating week's progress
            return true
        }

        // ideally this should always be positive, unless user changed the system time, in that case abs will be helpful
        val diffFromLastInitialization = abs(currentTime - (previousInitializationTime ?: currentTime))

        if (diffFromLastInitialization >= TimeUtils.MILLIS_IN_A_DAY) {
            // ideally this should cover the case where user opened the app after 7 days on the same week day
            // same hour range, then above checks will fail, this will cover that
            return true
        }

        return false
    }
}