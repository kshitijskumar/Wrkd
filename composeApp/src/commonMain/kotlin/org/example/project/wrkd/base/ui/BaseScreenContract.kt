package org.example.project.wrkd.base.ui

import org.jetbrains.compose.resources.DrawableResource

data class BaseScreenState(
    val tabsList: List<BaseScreenTabs> = listOf(),
    val selectedTab: BaseScreenTabs = BaseScreenTabs.HOME
)

enum class BaseScreenTabs {
    HOME,
    ADD,
    PROGRESS
}

sealed class BaseScreenIntent {
    data object InitializationIntent : BaseScreenIntent()

    data class TabClickedIntent(val tab: BaseScreenTabs) : BaseScreenIntent()

    data object BackClickedIntent : BaseScreenIntent()
}