package org.example.project.wrkd.core.navigation.args

import kotlinx.serialization.Serializable
import org.example.project.wrkd.core.navigation.scenes.AppScenes

@Serializable
data object WorkoutTrackingArgs : SceneArgs() {
    override val scene: AppScenes
        get() = AppScenes.WorkoutTracking
}