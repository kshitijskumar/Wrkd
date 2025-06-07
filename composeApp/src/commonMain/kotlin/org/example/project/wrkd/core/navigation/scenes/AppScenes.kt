package org.example.project.wrkd.core.navigation.scenes

enum class AppScenes(val route: String, val isStartingDestination: Boolean = false) {
    AppBaseScreen("/base", true),
    WorkoutTracking("/workoutTracking")
}