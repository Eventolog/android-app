package com.example.eventology.data.models

data class Ticket(
    val id: Int,
    val eventId: Int,
    val eventName: String,
    val seatRow: String?,
    val seatNumber: Int?,
    val reservationDate: String,
    val status: String
)