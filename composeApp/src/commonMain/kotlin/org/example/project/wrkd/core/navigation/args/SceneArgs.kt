package org.example.project.wrkd.core.navigation.args

import kotlinx.serialization.Serializable
import org.example.project.wrkd.core.navigation.scenes.AppScenes

@Serializable
sealed class SceneArgs {
    abstract val scene: AppScenes
}