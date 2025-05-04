package org.example.project.wrkd.utils

object TimeFormattingStringUtils {

    fun convertMillisElapsedToString(millis: Long): String {
        val hours = millis / MILLIS_IN_ONE_HOUR
        var remainingTime = millis % MILLIS_IN_ONE_HOUR

        val minutes = remainingTime / MILLIS_IN_ONE_MIN
        remainingTime %= MILLIS_IN_ONE_MIN

        val seconds = remainingTime / 1000

        return "${hours.toString().convertToMin2Digs()} : ${minutes.toString().convertToMin2Digs()} : ${seconds.toString().convertToMin2Digs()}"
    }

    private fun String.convertToMin2Digs(): String {
        return if (this.length == 1) "0$this" else this
    }

    private const val MILLIS_IN_ONE_HOUR = 60 * 60 * 1000
    private const val MILLIS_IN_ONE_MIN = 60 * 1000

}