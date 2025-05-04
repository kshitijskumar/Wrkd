package org.example.project.wrkd.navigation.args

import kotlinx.serialization.Serializable
import org.example.project.wrkd.core.models.WeekDay

@Serializable
data class DayPlanArgs(
    val day: WeekDay
)
