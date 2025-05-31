package org.example.project.wrkd.utils

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.LocalDate
import kotlinx.datetime.atTime
import kotlinx.datetime.toInstant
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.DatePeriod
import org.example.project.wrkd.core.models.WeekDay

interface TimeUtils {

    fun getWeekFromTime(time: Long): WeekDay?

    fun getHourOfDay(time: Long): Int

    fun getWeekStartAndEndForTime(time: Long): Pair<Long, Long>

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

    override fun getHourOfDay(time: Long): Int {
        val dateTime = time.getDateTime()
        return dateTime.hour
    }

    override fun getWeekStartAndEndForTime(time: Long): Pair<Long, Long> {
        val timezone = TimeZone.currentSystemDefault()
        val givenDateTime = time.getDateTime()
        val currentDate = givenDateTime.date

        // Calculate days to go back to Monday (start of week)
        val daysFromMonday = when (givenDateTime.dayOfWeek) {
            DayOfWeek.MONDAY -> 0
            DayOfWeek.TUESDAY -> 1
            DayOfWeek.WEDNESDAY -> 2
            DayOfWeek.THURSDAY -> 3
            DayOfWeek.FRIDAY -> 4
            DayOfWeek.SATURDAY -> 5
            DayOfWeek.SUNDAY -> 6
            else -> return (time to time)
        }

        // Calculate Monday's date (start of week)
        val mondayDate = currentDate.minus(DatePeriod(days = daysFromMonday))

        // Calculate Sunday's date (end of week)
        val sundayDate = mondayDate.plus(DatePeriod(days = 6))

        // Create start of week: Monday 00:00:00
        val weekStart = mondayDate.atTime(hour = 0, minute = 0, second = 0)
            .toInstant(timezone)
            .toEpochMilliseconds()

        // Create end of week: Sunday 23:59:59
        val weekEnd = sundayDate.atTime(hour = 23, minute = 59, second = 59)
            .toInstant(timezone)
            .toEpochMilliseconds()

        return Pair(weekStart, weekEnd)
    }

    private fun Long.getDateTime(): LocalDateTime {
        val instant = Instant.fromEpochMilliseconds(this)
        return instant.toLocalDateTime(TimeZone.currentSystemDefault())
    }
}