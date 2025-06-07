package org.example.project.wrkd.di

import org.example.project.wrkd.addworkout.WorkoutRoutinePlanner
import org.example.project.wrkd.addworkout.ui.DayPlanViewModel
import org.example.project.wrkd.base.ui.BaseScreenViewModel
import org.example.project.wrkd.core.db.AppDB
import org.example.project.wrkd.core.db.dao.WorkoutDaoImpl
import org.example.project.wrkd.core.local.UserDataLocalDataSource
import org.example.project.wrkd.core.local.UserDataLocalDataSourceImpl
import org.example.project.wrkd.core.navigation.AppNavigator
import org.example.project.wrkd.core.navigation.AppNavigatorImpl
import org.example.project.wrkd.core.navigation.AppNavigatorManager
import org.example.project.wrkd.core.repo.UserDataRepository
import org.example.project.wrkd.core.repo.UserDataRepositoryImpl
import org.example.project.wrkd.core.utils.CoroutinesContextProvider
import org.example.project.wrkd.core.utils.CoroutinesContextProviderImpl
import org.example.project.wrkd.di.core.Module
import org.example.project.wrkd.di.core.createModule
import org.example.project.wrkd.di.core.factory
import org.example.project.wrkd.di.core.inject
import org.example.project.wrkd.di.core.single
import org.example.project.wrkd.home.domain.GetAllWorkoutBetweenGivenTimestampUseCase
import org.example.project.wrkd.home.domain.GetWeeklyWorkoutSummaryUseCase
import org.example.project.wrkd.home.domain.GetWorkoutSessionSectionUseCase
import org.example.project.wrkd.home.ui.HomeViewModel
import org.example.project.wrkd.track.WorkoutTrackManager
import org.example.project.wrkd.track.WorkoutTrackerManagerImpl
import org.example.project.wrkd.track.data.dao.WorkoutDao
import org.example.project.wrkd.track.data.local.WorkoutLocalDataSource
import org.example.project.wrkd.track.data.local.WorkoutLocalDataSourceImpl
import org.example.project.wrkd.track.data.repo.WorkoutRepository
import org.example.project.wrkd.track.data.repo.WorkoutRepositoryImpl
import org.example.project.wrkd.track.domain.SaveWorkoutUseCase
import org.example.project.wrkd.track.ui.WorkoutTrackerViewModel
import org.example.project.wrkd.utils.TimeUtils
import org.example.project.wrkd.utils.TimeUtilsImpl

fun appModule(): Module {
    return createModule {
        factory<UserDataLocalDataSource> {
            UserDataLocalDataSourceImpl()
        }

        factory(
            block = {
                bind(UserDataRepository::class)
            }
        ) {
            UserDataRepositoryImpl()
        }

        factory {
            WorkoutRoutinePlanner()
        }

        factory {
            DayPlanViewModel(
                args = inject(),
                workoutRoutinePlanner = inject()
            )
        }

        factory<TimeUtils> {
            TimeUtilsImpl()
        }

        factory {
            WorkoutTrackerViewModel(
                timeUtils = inject(),
                workoutTrackManager = inject(),
                saveWorkoutUseCase = inject()
            )
        }

        factory<WorkoutTrackManager> {
            WorkoutTrackerManagerImpl()
        }

        factory<WorkoutDao> {
            inject<AppDB>().workoutDao
        }

        factory<WorkoutLocalDataSource> {
            WorkoutLocalDataSourceImpl(dao = inject())
        }

        factory(
            block = {
                bind(WorkoutRepository::class)
            }
        ) {
            WorkoutRepositoryImpl(
                localDataSource = inject()
            )
        }

        factory {
            SaveWorkoutUseCase(repository = inject())
        }

        single<CoroutinesContextProvider> {
            CoroutinesContextProviderImpl()
        }

        single(
            block = {
                bind(
                    AppNavigator::class,
                    AppNavigatorManager::class
                )
            }
        ) {
            AppNavigatorImpl()
        }

        home()
    }
}

private fun Module.Builder.home() {
    factory {
        GetAllWorkoutBetweenGivenTimestampUseCase(
            repository = inject()
        )
    }

    factory {
        GetWeeklyWorkoutSummaryUseCase(
            timeUtils = inject(),
            getAllWorkoutBetweenGivenTimestampUseCase = org.example.project.wrkd.di.core.inject()
        )
    }

    factory {
        HomeViewModel(
            getAllWorkoutBetweenGivenTimestampUseCase = inject(),
            getWeeklyWorkoutSummaryUseCase = inject(),
            appNavigator = inject(),
            timeUtils = inject(),
            getWorkoutSessionSectionUseCase = inject()
        )
    }

    factory {
        BaseScreenViewModel(
            navigator = inject()
        )
    }

    factory {
        GetWorkoutSessionSectionUseCase(
            getAllWorkoutBetweenGivenTimestampUseCase = inject(),
            timeUtils = inject()
        )
    }
}

expect fun platformModule(): Module

class Test1(val a: Int)

class Test2(val a: Int)