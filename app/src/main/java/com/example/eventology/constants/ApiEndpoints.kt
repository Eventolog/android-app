package com.example.eventology.constants

object ApiEndpoints {
    const val LOGIN = "/user/login"
    const val WHOAMI = "/user/whoami"
    const val SIGNUP = "/user/signup"
    const val EVENTS = "/events"
    const val MY_TICKETS = "/getMyTickets"
    const val INCIDENCES = "/incidences"
    fun getFreeSeats(eventId: Int): String {
        return "/$eventId/getFreeSeats"
    }
    fun bookSeat(eventId: Int): String {
        return "/$eventId/bookSeat"
    }
}