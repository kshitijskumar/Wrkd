package org.example.project.wrkd.utils

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.example.project.wrkd.core.models.WeekDay

interface TimeUtils {

    fun getWeekFromTime(time: Long): WeekDay?

    fun getHourOfDay(time: Long): Int?

}

class TimeUtilsImpl : TimeUtils {

    override fun getWeekFromTime(time: Long): WeekDay? {
        val dateTime = time.getDateTime()
        return when(dateTime.dayOfWeek) {
            DayOfWeek.MONDAY -> WeekDay.Mon
            DayOfWeek.TUESDAY -> WeekDay.Tues
            DayOfWeek.WEDNESDAY -> WeekDay.Wed
            DayOfWeek.THURSDAY -> WeekDay.Thurs
            DayOfWeek.FRIDAY -> WeekDay.Fri
            DayOfWeek.SATURDAY -> WeekDay.Sat
            DayOfWeek.SUNDAY -> WeekDay.Sun
            else -> null
        }
    }

    override fun getHourOfDay(time: Long): Int? {
        val dateTime = time.getDateTime()
        return dateTime.hour
    }

    private fun Long.getDateTime(): LocalDateTime {
        val instant = Instant.fromEpochMilliseconds(this)
        return instant.toLocalDateTime(TimeZone.currentSystemDefault())
    }
}