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
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.char
import org.example.project.wrkd.core.models.WeekDay
import org.example.project.wrkd.utils.TimeFormattingStringUtils.convertToMin2Digs

interface TimeUtils {

    fun getWeekFromTime(time: Long): WeekDay?

    fun getHourOfDay(time: Long): Int

    fun getWeekStartAndEndForTime(time: Long): Pair<Long, Long>

    fun getDayTimeRange(hour: Int): DayTimeRange?

    /**
     * formats in hh h  mm mins
     */
    fun durationMillisToHourMinute(durationMillis: Long): String

    /**
     * @return formatted in dd MMM
     */
    fun getDateMonth(millis: Long): String

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

    override fun getDayTimeRange(hour: Int): DayTimeRange? {
        DayTimeRange.entries.forEach {
            if (hour in it.range) {
                return it
            }
        }
        return null
    }

    override fun durationMillisToHourMinute(durationMillis: Long): String {
        val hours = durationMillis / TimeFormattingStringUtils.MILLIS_IN_ONE_HOUR
        var remainingTime = durationMillis % TimeFormattingStringUtils.MILLIS_IN_ONE_HOUR

        val minutes = remainingTime / TimeFormattingStringUtils.MILLIS_IN_ONE_MIN

        val hoursFormatted: (shouldMake2Digits: Boolean) -> String = { shouldMake2Digits ->
            val hourString = if (hours <= 9 && shouldMake2Digits) {
                hours.toString().convertToMin2Digs()
            } else {
                hours.toString()
            }
            "$hourString h"
        }

        val minutesFormatted: (shouldMake2Digits: Boolean) -> String = { shouldMake2Digits ->
            val minutesString = if (minutes <= 9 && shouldMake2Digits) {
                minutes.toString().convertToMin2Digs()
            } else {
                minutes.toString()
            }
            "$minutesString min"
        }

        return when {
            hours > 0 && minutes > 0 -> {
                "${hoursFormatted(false)} ${minutesFormatted(false)}"
            }
            hours > 0 -> {
                hoursFormatted(false)
            }
            minutes > 0 -> {
                minutesFormatted(false)
            }
            else -> {
                "0 min"
            }
        }
    }

    override fun getDateMonth(millis: Long): String {
        val dateTime = Instant
            .fromEpochMilliseconds(millis)
            .toLocalDateTime(TimeZone.currentSystemDefault())

        val dateFormat = LocalDate.Format {
            dayOfMonth()
            char(' ')
            monthName(MonthNames.ENGLISH_ABBREVIATED)
        }

        return dateFormat.format(dateTime.date)
    }

    private fun Long.getDateTime(): LocalDateTime {
        val instant = Instant.fromEpochMilliseconds(this)
        return instant.toLocalDateTime(TimeZone.currentSystemDefault())
    }
}

enum class DayTimeRange(val range: IntRange) {
    MORNING(0 until 12),
    AFTERNOON(12 until 16),
    EVENING(16 until 20),
    NIGHT(20 .. 23)
}