package com.example.eventology.utils

import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object DateUtils {

    private val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
    private val outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    /**
     * Checks if the given date string is in the future compared to now.
     * @param dateStr The date string in ISO format: "yyyy-MM-dd'T'HH:mm:ss"
     * @return true if the date is after now, false otherwise.
     */
    fun isUpcoming(dateStr: String): Boolean {
        val date = LocalDateTime.parse(dateStr, inputFormatter)
        return date.isAfter(LocalDateTime.now())
    }

    /**
     * Checks if the given date string is in the past compared to now.
     * @param dateStr The date string in ISO format: "yyyy-MM-dd'T'HH:mm:ss"
     * @return true if the date is before now, false otherwise.
     */
    fun isPast(dateStr: String): Boolean {
        val date = LocalDateTime.parse(dateStr, inputFormatter)
        return date.isBefore(LocalDateTime.now())
    }

    /**
     * Converts a date string from ISO format to dd/MM/yyyy.
     * @param dateStr The date string in ISO format: "yyyy-MM-dd'T'HH:mm:ss"
     * @return A formatted date string in "dd/MM/yyyy" format.
     */
    fun toReadableDate(dateStr: String): String {
        val date = LocalDateTime.parse(dateStr, inputFormatter)
        return date.format(outputFormatter)
    }

    /**
     * Returns the difference in minutes between two date strings.
     * @param startDateStr The start date in ISO format: "yyyy-MM-dd'T'HH:mm:ss"
     * @param endDateStr The end date in ISO format: "yyyy-MM-dd'T'HH:mm:ss"
     * @return The number of minutes between start and end.
     */
    fun getDifferenceInMinutes(startDateStr: String, endDateStr: String): Long {
        val start = LocalDateTime.parse(startDateStr, inputFormatter)
        val end = LocalDateTime.parse(endDateStr, inputFormatter)
        return Duration.between(start, end).toMinutes()
    }


    /**
     * Returns the difference between two date strings in a human-readable format.
     * The result is expressed in days, hours, and minutes.
     *
     * @param startDateStr The start date in ISO format: "yyyy-MM-dd'T'HH:mm:ss"
     * @param endDateStr The end date in ISO format: "yyyy-MM-dd'T'HH:mm:ss"
     * @return A human-readable string representing the duration, e.g., "1h", "1h 30m", or "2d 3h".
     */
    fun getReadableDuration(startDateStr: String, endDateStr: String): String {
        val inputFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

        // Parse the start and end dates
        val start = LocalDateTime.parse(startDateStr, inputFormatter)
        val end = LocalDateTime.parse(endDateStr, inputFormatter)

        // Calculate the duration between the two times
        val duration = Duration.between(start, end)

        // Get the total number of days, hours, and minutes
        val days = duration.toDays()
        val hours = duration.toHours() % 24
        val minutes = duration.toMinutes() % 60

        // Build the human-readable string
        val result = StringBuilder()

        if (days > 0) result.append("${days}d ")
        if (hours > 0) result.append("${hours}h ")
        if (minutes > 0 || (days.toInt() == 0 && hours.toInt() == 0)) result.append("${minutes}m")

        // Trim any trailing spaces and return the result
        return result.toString().trim()
    }
}
