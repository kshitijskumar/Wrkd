package org.example.project.wrkd.core.navigation.utils

import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import org.example.project.wrkd.core.navigation.args.SceneArgs
import org.example.project.wrkd.core.navigation.scenes.AppScenes
import org.example.project.wrkd.di.core.inject

inline fun <reified T: ViewModel> SceneArgs.viewModel(navBackStackEntry: NavBackStackEntry): T {
    val existing = navBackStackEntry.viewModelStore["${T::class.simpleName}_${this}"]
    return if (existing is T) {
        existing
    } else {
        inject<T>(args = listOf(this)).also {
            navBackStackEntry.viewModelStore.put("${T::class.simpleName}_${this}", it)
        }
    }
}

inline fun <reified T: ViewModel> viewModel(
    navBackStackEntry: NavBackStackEntry,
    identifier: String = "",
): T {
    val existing = navBackStackEntry.viewModelStore["${T::class.simpleName}_${identifier}"]
    return if (existing is T) {
        existing
    } else {
        inject<T>().also {
            navBackStackEntry.viewModelStore.put("${T::class.simpleName}_${identifier}", it)
        }
    }
}