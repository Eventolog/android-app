package com.example.eventology.data.models

data class Event(
    val id: Int,
    val name: String,
    val description: String,
    val ifFullDay: Boolean,
    val startTime: String,
    val endTime: String,
    val status: String,
    val createdAt: String,
    val roomName: String,
    val roomDescription: String,
    val roomDistribution: String,
    val roomHasSeatDistribution: Boolean
)
