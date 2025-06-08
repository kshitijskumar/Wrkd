package org.example.project.wrkd.core.navigation.args

import kotlinx.serialization.Serializable
import org.example.project.wrkd.core.navigation.scenes.AppScenes

@Serializable
sealed class WorkoutTrackingArgs : SceneArgs() {
    override val scene: AppScenes
        get() = AppScenes.WorkoutTracking

    @Serializable
    data object TrackingArgs : WorkoutTrackingArgs()

    @Serializable
    data class DisplayArgs(val workoutId: String) : WorkoutTrackingArgs()
}