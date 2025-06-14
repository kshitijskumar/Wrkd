package org.example.project.wrkd.core.navigation

import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import org.example.project.wrkd.core.navigation.args.SceneArgs
import org.example.project.wrkd.core.navigation.args.buildNavigationRoute
import org.example.project.wrkd.core.navigation.scenes.AppScenes

interface AppNavigator {

    fun navigate(
        args: SceneArgs,
        navOptions: NavOptions? = null
    )

    fun goBack()

}

interface AppNavigatorManager {
    fun setNavigator(controller: NavHostController)
}

class AppNavigatorImpl : AppNavigator, AppNavigatorManager {

    private var controller: NavHostController? = null
    override fun navigate(args: SceneArgs, navOptions: NavOptions?) {
        controller?.navigate(
            route = args.buildNavigationRoute(),
            navOptions = navOptions
        )
    }

    override fun goBack() {
        controller?.navigateUp()
    }

    override fun setNavigator(controller: NavHostController) {
        this.controller = controller
    }
}

class NavOptionsDSLBuilder {
    private val operations = mutableListOf<NavOptionsOperations>()

    fun setPopUpTo(
        scene: AppScenes,
        inclusive: Boolean,
    ) {
        operations.add(
            NavOptionsOperations.SetPopUpTo(
                scene = scene,
                inclusive = inclusive,
            )
        )
    }

    /**
     * NO NEED TO CALL THIS METHOD
     */
    internal fun getBuilder(): NavOptions.Builder {
        var builder = NavOptions.Builder()
        operations.forEach {
            builder = when(it) {
                is NavOptionsOperations.SetPopUpTo -> {
                    builder.setPopUpTo(
                        route = it.scene.baseRoute(),
                        inclusive = it.inclusive,
                    )
                    builder.setLaunchSingleTop(true)
                }
            }
        }
        return builder
    }

    private sealed class NavOptionsOperations {
        data class SetPopUpTo(
            val scene: AppScenes,
            val inclusive: Boolean
        ) : NavOptionsOperations()
    }

}

fun navOptions(block: NavOptionsDSLBuilder.() -> Unit): NavOptions {
    val builder = NavOptionsDSLBuilder()
    builder.block()
    return builder.getBuilder().build()
}

