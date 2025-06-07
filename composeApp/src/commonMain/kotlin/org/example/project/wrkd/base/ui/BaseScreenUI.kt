package org.example.project.wrkd.base.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.example.project.wrkd.core.ui.compose.AppTheme
import org.example.project.wrkd.home.ui.HomeScreen
import org.example.project.wrkd.home.ui.HomeViewModel
import org.jetbrains.compose.resources.painterResource
import wrkd.composeapp.generated.resources.Res
import wrkd.composeapp.generated.resources.*

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BaseScreenUI(
    vm: BaseScreenViewModel,
    homeViewModel: HomeViewModel
) {
    val state by vm.state.collectAsStateWithLifecycle(LocalLifecycleOwner.current)

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        BaseScreenTabsScreenUI(
            selectedTab = state.selectedTab,
            homeViewModel = homeViewModel,
            modifier = Modifier.weight(1f)
        )

        BaseScreenBottomTabs(
            tabsList = state.tabsList,
            selectedTab = state.selectedTab,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AppTheme.dimens.medium1)
                .padding(bottom = AppTheme.dimens.medium1),
            onTabSelected = {
                vm.processIntent(BaseScreenIntent.TabClickedIntent(it))
            }
        )
    }

    BackHandler {
        vm.processIntent(BaseScreenIntent.BackClickedIntent)
    }
}

@Composable
fun BaseScreenTabsScreenUI(
    selectedTab: BaseScreenTabs,
    homeViewModel: HomeViewModel,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        when(selectedTab) {
            BaseScreenTabs.HOME -> {
                HomeScreen(vm = homeViewModel)
            }
            BaseScreenTabs.ADD -> {} // no UI for this, this will be a separate navigation screen
            BaseScreenTabs.PROGRESS -> {
                Text("Progress screen")
            }
        }
    }
}

@Composable
fun BaseScreenBottomTabs(
    tabsList: List<BaseScreenTabs>,
    selectedTab: BaseScreenTabs,
    onTabSelected: (BaseScreenTabs) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(
                color = AppTheme.color.black,
                shape = CircleShape
            )
            .padding(
                horizontal = AppTheme.dimens.small3,
                vertical = AppTheme.dimens.medium2
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        tabsList.forEach {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = AppTheme.dimens.small2)
                    .clickable { onTabSelected.invoke(it) },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                BaseScreenTabIcon(
                    tabType = it,
                    isSelected = selectedTab == it
                )
            }
        }
    }
}

@Composable
fun BaseScreenTabIcon(
    tabType: BaseScreenTabs,
    isSelected: Boolean,
    modifier: Modifier = Modifier
) {
    val iconResolver = remember(tabType) {
        when(tabType) {
            BaseScreenTabs.HOME -> Res.drawable.ic_home
            BaseScreenTabs.ADD -> Res.drawable.ic_add
            BaseScreenTabs.PROGRESS -> Res.drawable.ic_progress
        }
    }

    val tint = remember(tabType, isSelected) {
        if (isSelected) {
            AppTheme.color.selectedTabIcon
        } else {
            AppTheme.color.unselectedTabIcon
        }
    }

    Icon(
        painter = painterResource(iconResolver),
        tint = tint,
        modifier = modifier.size(AppTheme.dimens.medium3),
        contentDescription = null
    )
}