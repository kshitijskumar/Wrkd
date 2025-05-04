package org.example.project.wrkd.core.models

enum class WeekDay {
    Mon,
    Tues,
    Wed,
    Thurs,
    Fri,
    Sat,
    Sun
}

val WeekDay.dayName: String
    get() {
        return when(this) {
            WeekDay.Mon -> "Monday"
            WeekDay.Tues -> "Tuesday"
            WeekDay.Wed -> "Wednesday"
            WeekDay.Thurs -> "Thursday"
            WeekDay.Fri -> "Friday"
            WeekDay.Sat -> "Saturday"
            WeekDay.Sun -> "Sunday"
        }
    }