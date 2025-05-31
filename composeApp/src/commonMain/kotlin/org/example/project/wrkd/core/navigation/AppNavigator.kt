package org.example.project.wrkd.core.navigation

import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import kotlinx.serialization.decodeFromString
import org.example.project.wrkd.core.navigation.NavigationData.Companion.getArgs
import org.example.project.wrkd.core.navigation.args.SceneArgs
import org.example.project.wrkd.core.navigation.scenes.AppScenes
import org.example.project.wrkd.utils.AppJson
import kotlin.jvm.JvmInline

interface AppNavigator {

    fun navigate(args: SceneArgs)

    fun goBack()

}

interface AppNavigatorManager {
    fun setNavigator(controller: NavHostController)
}

class AppNavigatorImpl : AppNavigator, AppNavigatorManager {

    private var controller: NavHostController? = null
    override fun navigate(args: SceneArgs) {
        controller?.navigate(NavigationData(args).buildRoute())
    }

    override fun goBack() {
        controller?.navigateUp()
    }

    override fun setNavigator(controller: NavHostController) {
        this.controller = controller
    }
}

@JvmInline
value class NavigationData(val args: SceneArgs) {

    fun buildRoute(): String {
        val argsJson = AppJson.encodeToString(args)
        return buildString {
            append(args.scene.route)
            append("/$argsJson")
        }
    }
    companion object {
        const val ARGS = "args"

        fun getOptionalArgs(backStackEntry: NavBackStackEntry): SceneArgs? {
            val args = backStackEntry.arguments?.getString(ARGS)?.takeIf { it.isNotEmpty() } ?: return null
            return AppJson.decodeFromString(args)
        }
        inline fun <reified T: SceneArgs>getArgs(backStackEntry: NavBackStackEntry): T {
            val args = this.getOptionalArgs(backStackEntry)

            requireNotNull(args) {
                "Expected args are not present in backStackEntry"
            }

            return args as T
        }
    }
}

