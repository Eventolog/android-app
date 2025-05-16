package com.example.eventology.utils

/**
 * Utility class
 */
object Utils {
    /**
     * Removes all double quotes from the given string.
     *
     * @param input The string potentially wrapped or containing double quotes.
     * @return The string without any double quote characters.
     */
    fun removeDoubleQuotes(input: String): String {
        return input.replace("\"", "")
    }
}