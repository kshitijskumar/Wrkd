package org.example.project.wrkd.base.ui

import org.example.project.wrkd.core.navigation.AppNavigator
import org.example.project.wrkd.core.navigation.args.WorkoutTrackingArgs
import org.example.project.wrkd.core.ui.BaseViewModel

class BaseScreenViewModel(
    private val navigator: AppNavigator
) : BaseViewModel<BaseScreenState, BaseScreenIntent>() {

    override fun initialData(): BaseScreenState {
        return BaseScreenState()
    }

    init {
        processIntent(BaseScreenIntent.InitializationIntent)
    }

    override fun processIntent(intent: BaseScreenIntent) {
        when(intent) {
            BaseScreenIntent.InitializationIntent -> handleInitializationIntent()
            is BaseScreenIntent.TabClickedIntent -> handleTabClickedIntent(intent)
            BaseScreenIntent.BackClickedIntent -> handleBackClickedIntent()
        }
    }

    private fun handleInitializationIntent() {
        val tabsList = listOf(
            BaseScreenTabs.HOME,
            BaseScreenTabs.ADD,
            BaseScreenTabs.PROGRESS
        )

        updateState {
            it.copy(
                tabsList = tabsList,
                selectedTab = BaseScreenTabs.HOME
            )
        }
    }

    private fun handleTabClickedIntent(intent: BaseScreenIntent.TabClickedIntent) {
        when(intent.tab) {
            BaseScreenTabs.ADD -> {
                navigator.navigate(WorkoutTrackingArgs)
            }
            BaseScreenTabs.HOME,
            BaseScreenTabs.PROGRESS -> {
                updateState {
                    it.copy(
                        selectedTab = if (isValidTabSelection(intent.tab)) intent.tab else it.selectedTab
                    )
                }
            }
        }
    }

    private fun handleBackClickedIntent() {
        val currentlySelectedTab = currentState.selectedTab

        when(currentlySelectedTab) {
            BaseScreenTabs.HOME -> {
                navigator.goBack()
            }
            BaseScreenTabs.ADD,
            BaseScreenTabs.PROGRESS -> {
                updateState {
                    it.copy(
                        selectedTab = BaseScreenTabs.HOME
                    )
                }
            }
        }
    }

    private fun isValidTabSelection(
        tab: BaseScreenTabs,
        allowedTabs: List<BaseScreenTabs> = currentState.tabsList
    ): Boolean {
        return allowedTabs.contains(tab)
    }
}