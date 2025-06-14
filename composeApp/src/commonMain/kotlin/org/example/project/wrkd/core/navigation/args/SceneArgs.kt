package org.example.project.wrkd.core.navigation.args

import androidx.navigation.NavBackStackEntry
import kotlinx.serialization.Serializable
import org.example.project.wrkd.core.navigation.scenes.AppScenes
import org.example.project.wrkd.utils.AppJson

@Serializable
sealed class SceneArgs {
    abstract val scene: AppScenes
}

fun SceneArgs.buildNavigationRoute(): String {
    return this.scene.navigationRoute(this)
}

fun getOptionalArgs(backStackEntry: NavBackStackEntry): SceneArgs? {
    val args = backStackEntry.arguments?.getString(AppScenes.ARGS)?.takeIf { it.isNotEmpty() } ?: return null
    return AppJson.decodeFromString(args)
}
inline fun <reified T: SceneArgs>getArgs(backStackEntry: NavBackStackEntry): T {
    val args = getOptionalArgs(backStackEntry)

    requireNotNull(args) {
        "Expected args are not present in backStackEntry"
    }

    return args as T
}