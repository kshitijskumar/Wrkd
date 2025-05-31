package org.example.project.wrkd.home.ui

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.example.project.wrkd.core.models.app.DayPlanAppModel
import org.example.project.wrkd.core.navigation.AppNavigator
import org.example.project.wrkd.core.navigation.args.SceneArgs
import org.example.project.wrkd.core.navigation.args.WorkoutTrackingArgs
import org.example.project.wrkd.core.navigation.scenes.AppScenes
import org.example.project.wrkd.core.ui.BaseViewModel
import org.example.project.wrkd.home.domain.GetAllWorkoutBetweenGivenTimestampUseCase
import org.example.project.wrkd.utils.System

class HomeViewModel(
    private val getAllWorkoutBetweenGivenTimestampUseCase: GetAllWorkoutBetweenGivenTimestampUseCase,
    private val appNavigator: AppNavigator
) : BaseViewModel<HomeState, HomeIntent>() {

    override fun initialData() = HomeState()

    init {
        processIntent(HomeIntent.InitializationIntent)
    }

    override fun processIntent(intent: HomeIntent) {
        when(intent) {
            HomeIntent.InitializationIntent -> handleInitializationIntent()
            HomeIntent.DummyClickIntent -> {
                appNavigator.navigate(WorkoutTrackingArgs)
            }
        }
    }

    private fun handleInitializationIntent() = viewModelScope.launch {
        launch {
            initialiseHomeInfoCards()
        }
    }

    private suspend fun initialiseHomeInfoCards() {
        combine(
            flow = currentDayInfo(),
            flow2 = currentDayInfo()
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
                                    duration = it.workoutDuration,
                                    totalExercises = it.exercises.size
                                )
                            }
                        }
                    }
                }

                HomeInfoCard.CurrentDayWorkoutDetails(
                    info = workoutInfoList
                )
            }
    }
}