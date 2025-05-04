package org.example.project.wrkd.core.models

import kotlinx.serialization.Serializable

@Serializable
data class UserData(
    val age: Int,
    val weight: Double,
    val heightInCm: Long
)
