package org.example.project.wrkd.core.navigation.scenes

enum class AppScenes(val route: String, val isStartingDestination: Boolean = false) {
    Home("/home", true),
    WorkoutTracking("/workoutTracking")
}