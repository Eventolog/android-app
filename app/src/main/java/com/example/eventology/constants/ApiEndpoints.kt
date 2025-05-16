package com.example.eventology.constants

object ApiEndpoints {
    const val LOGIN = "/user/login"
    const val WHOAMI = "/user/whoami"
    const val SIGNUP = "/user/signup"
    const val EVENTS = "/events"
    const val MY_TICKETS = "/getMyTickets"
    fun getFreeSeats(eventId: Int): String {
        return "/$eventId/getFreeSeats"
    }
}