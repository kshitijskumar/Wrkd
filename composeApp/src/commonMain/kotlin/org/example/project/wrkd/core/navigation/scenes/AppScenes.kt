package org.example.project.wrkd.core.navigation.scenes

import org.example.project.wrkd.core.navigation.args.SceneArgs
import org.example.project.wrkd.utils.AppJson

enum class AppScenes(private val route: String, val isStartingDestination: Boolean = false) {
    AppBaseScreen("/appBase", true),
    WorkoutTracking("/workoutTracking");

    fun baseRoute(): String {
        return "$route/$NAV_GRAPH_PATTERN_ARGS"
    }

    fun navigationRoute(args: SceneArgs): String {
        return baseRoute()
            .replace(
                oldValue = NAV_GRAPH_PATTERN_ARGS,
                newValue = AppJson.encodeToString(args)
            )
    }

    companion object {
        const val ARGS = "ARGS"
        private const val NAV_GRAPH_PATTERN_ARGS = "{$ARGS}"
    }
}