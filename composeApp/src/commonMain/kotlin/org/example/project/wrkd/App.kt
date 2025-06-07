package org.example.project.wrkd

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import org.example.project.wrkd.base.ui.BaseScreenUI
import org.example.project.wrkd.core.navigation.AppNavigatorManager
import org.example.project.wrkd.core.navigation.NavigationData
import org.example.project.wrkd.core.navigation.args.AppBaseScreenArgs
import org.example.project.wrkd.core.navigation.args.WorkoutTrackingArgs
import org.example.project.wrkd.core.navigation.scenes.AppScenes
import org.example.project.wrkd.core.navigation.utils.viewModel
import org.example.project.wrkd.di.core.inject
import org.example.project.wrkd.home.ui.HomeScreen
import org.example.project.wrkd.track.ui.WorkoutTrackerScreen
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val navController = rememberNavController()
            LaunchedEffect(navController) {
                // update nav controller whenever it gets updated
                inject<AppNavigatorManager>().setNavigator(navController)
            }

            NavHost(
                navController = navController,
                startDestination = AppScenes.AppBaseScreen.route
            ) {
                AppScenes.entries.forEach {
                    buildScreens(it)
                }
            }
        }
    }
}

fun NavGraphBuilder.buildScreens(
    scene: AppScenes
) {
    composable(
        route = scene.route + if (scene.isStartingDestination) "" else "/{${NavigationData.ARGS}}",
        arguments = listOf(
            navArgument(name = NavigationData.ARGS) {
                type = NavType.StringType
                this.nullable = true
            }
        )
    ) {
        SceneContent(it)
    }
}

@Composable
fun SceneContent(
    backStackEntry: NavBackStackEntry
) {
    when(val args = NavigationData.getOptionalArgs(backStackEntry)) {
        WorkoutTrackingArgs -> WorkoutTrackerScreen(vm = args.viewModel(backStackEntry))
        AppBaseScreenArgs,
        null -> {
            // handling null as home because it will be the start destination and we will not get any args
            // ideally if you are changing the start destination, then handle null for that screen, and for all other screens
            // we will always have the args from backstack entry because args are necessary for navigation
            val baseArgs = args ?: AppBaseScreenArgs // default home args for start destination
            BaseScreenUI(
                vm = baseArgs.viewModel(backStackEntry),
                homeViewModel = viewModel(backStackEntry, baseArgs.toString())
            )
        }
    }
}
