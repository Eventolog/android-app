package com.example.eventology.data.services

import android.content.Context
import com.example.eventology.R
import com.example.eventology.constants.IncidenceTypes
import com.example.eventology.data.models.Seat
import com.example.eventology.data.models.User
import com.example.eventology.data.models.Event
import com.example.eventology.data.models.Ticket
import com.example.eventology.constants.UserTypes
import com.example.eventology.data.models.Incidence

object MockDataService : DataServiceInterface {
    private var user: User? = null

    private var incidencesList = mutableListOf(
        Incidence(
            id = 1,
            reason = "No he rebut les entrades per correu",
            status = "pendent"
        ),
        Incidence(
            id = 2,
            reason = "Error en el pagament",
            status = "pendent"
        ),
        Incidence(
            id = 3,
            reason = "He comprat entrades duplicades per error",
            status = "en procés"
        ),
        Incidence(
            id = 4,
            reason = "El codi QR no funciona a l'entrada",
            status = "resolt"
        ),
        Incidence(
            id = 5,
            reason = "L'esdeveniment ha estat cancel·lat",
            status = "pendent"
        ),
        Incidence(
            id = 6,
            reason = "Vull canviar la data de l'esdeveniment",
            status = "pendent"
        ),
        Incidence(
            id = 7,
            reason = "Les meves dades personals són incorrectes",
            status = "en procés"
        ),
        Incidence(
            id = 8,
            reason = "No puc accedir al meu compte",
            status = "resolt"
        ),
        Incidence(
            id = 9,
            reason = "He comprat entrades però no apareixen a l'app",
            status = "pendent"
        ),
        Incidence(
            id = 10,
            reason = "El preu mostrat no coincideix amb el total cobrat",
            status = "en procés"
        )
    )


    override fun getUser(): User? {
        return this.user
    }

    private fun matchNormalUserCredentials(email: String, password: String): Boolean {
        val validEmail = "normal@gmail.com"
        val validPassword = "passw0rd"
        val matchNormalUserCredentials = email == validEmail && password == validPassword
        return matchNormalUserCredentials
    }

    private fun matchOrganizerUserCredentials(email: String, password: String): Boolean {
        val validEmail = "organizer@gmail.com"
        val validPassword = "passw0rd"
        val matchNormalUserCredentials = email == validEmail && password == validPassword
        return matchNormalUserCredentials
    }

    override suspend fun login(email: String, password: String, context: Context): String? {
        var errorMsg: String? = null
        val matchNormalUser = matchNormalUserCredentials(email, password)
        val matchOrganizerUser = matchOrganizerUserCredentials(email, password)
        if (matchNormalUser) {
            this.user = User(
                name = "Usuario Normal",
                email = email,
                type = UserTypes.NORMAL,
                jwt = ""
            )
        } else if (matchOrganizerUser) {
            this.user = User(
                name = "Usuario Organizador",
                email = email,
                type = UserTypes.ORGANIZER,
                jwt = ""
            )
        } else {
            errorMsg = context.getString(R.string.error_invalid_credentials)
        }

        return errorMsg
    }

    override suspend fun signup(
        name: String,
        email: String,
        password: String,
        context: Context
    ): String? {
        var errorMsg: String? = null

        // Validate email format using a simple regex
        val emailRegex = "^[A-Za-z0-9+_.-]+@(.+)\$".toRegex()
        if (!emailRegex.matches(email)) {
            errorMsg = context.getString(R.string.error_invalid_email_format)
        }

        // Validate password length
        if (password.length <= 5) {
            errorMsg = context.getString(R.string.error_password_length)
        }

        // If no validation errors, create the user
        if (errorMsg == null) {
            this.user = User(
                name = name,
                email = email,
                type = UserTypes.ORGANIZER,
                jwt = ""
            )
        }
        return errorMsg
    }

    override suspend fun getAllEvents(): List<Event> {
        return listOf(
            Event(
                id = 1,
                name = "Startup Pitch",
                description = "Pitch your startup ideas",
                ifFullDay = false,
                startTime = "2025-06-10T10:00:00",
                endTime = "2025-06-10T13:00:00",
                status = "scheduled",
                createdAt = "2025-05-12T20:09:05.77",
                roomName = "Room A",
                roomDescription = "A small conference room",
                roomDistribution = "{\"Scenery\":[],\"Seats\":[]}",
                roomHasSeatDistribution = true
            ),
            Event(
                id = 2,
                name = "AI & Ethics Panel",
                description = "Discuss the ethics of AI systems",
                ifFullDay = false,
                startTime = "2025-05-10T14:00:00",
                endTime = "2025-05-10T16:00:00",
                status = "completed",
                createdAt = "2025-04-25T10:15:00",
                roomName = "Room B",
                roomDescription = "Medium-sized auditorium",
                roomDistribution = "{\"Scenery\":[],\"Seats\":[]}",
                roomHasSeatDistribution = true
            ),
            Event(
                id = 3,
                name = "UX Design Workshop",
                description = "Hands-on session on design thinking",
                ifFullDay = false,
                startTime = "2025-05-13T09:00:00",
                endTime = "2025-05-13T12:00:00",
                status = "completed",
                createdAt = "2025-04-28T08:00:00",
                roomName = "Room C",
                roomDescription = "Workshop space with whiteboards",
                roomDistribution = "{\"Scenery\":[],\"Seats\":[]}",
                roomHasSeatDistribution = false
            ),
            Event(
                id = 4,
                name = "Cloud Computing 101",
                description = "Intro to cloud infrastructure",
                ifFullDay = false,
                startTime = "2025-05-15T11:00:00",
                endTime = "2025-05-15T13:00:00",
                status = "scheduled",
                createdAt = "2025-05-01T13:45:00",
                roomName = "Room D",
                roomDescription = "Standard meeting room",
                roomDistribution = "{\"Scenery\":[],\"Seats\":[]}",
                roomHasSeatDistribution = false
            ),
            Event(
                id = 5,
                name = "Full-Day Hackathon",
                description = "Code from dawn till dusk",
                ifFullDay = true,
                startTime = "2025-05-20T00:00:00",
                endTime = "2025-05-20T23:59:59",
                status = "scheduled",
                createdAt = "2025-05-05T16:30:00",
                roomName = "Main Hall",
                roomDescription = "Large hall with multiple stations",
                roomDistribution = "{\"Scenery\":[],\"Seats\":[]}",
                roomHasSeatDistribution = true
            ),
            Event(
                id = 6,
                name = "Networking Breakfast",
                description = "Meet fellow professionals over coffee",
                ifFullDay = false,
                startTime = "2025-05-14T08:00:00",
                endTime = "2025-05-14T09:30:00",
                status = "ongoing",
                createdAt = "2025-05-10T09:00:00",
                roomName = "Cafeteria",
                roomDescription = "Casual space with food and drinks",
                roomDistribution = "{\"Scenery\":[],\"Seats\":[]}",
                roomHasSeatDistribution = false
            ),
            Event(
                id = 7,
                name = "Cybersecurity Briefing",
                description = "Protect your digital assets",
                ifFullDay = false,
                startTime = "2025-06-01T10:00:00",
                endTime = "2025-06-01T12:00:00",
                status = "scheduled",
                createdAt = "2025-05-11T11:11:00",
                roomName = "Room E",
                roomDescription = "Secure room with limited access",
                roomDistribution = "{\"Scenery\":[],\"Seats\":[]}",
                roomHasSeatDistribution = true
            ),
            Event(
                id = 8,
                name = "Machine Learning Demo",
                description = "Live demo of ML models in action",
                ifFullDay = false,
                startTime = "2025-05-18T15:00:00",
                endTime = "2025-05-18T17:00:00",
                status = "scheduled",
                createdAt = "2025-05-08T14:20:00",
                roomName = "Lab A",
                roomDescription = "Equipped with GPUs",
                roomDistribution = "{\"Scenery\":[],\"Seats\":[]}",
                roomHasSeatDistribution = true
            ),
            Event(
                id = 9,
                name = "Data Privacy Training",
                description = "Learn GDPR and data security practices",
                ifFullDay = false,
                startTime = "2025-04-30T13:00:00",
                endTime = "2025-04-30T16:00:00",
                status = "completed",
                createdAt = "2025-04-15T09:00:00",
                roomName = "Room F",
                roomDescription = "Training room with projectors",
                roomDistribution = "{\"Scenery\":[],\"Seats\":[]}",
                roomHasSeatDistribution = false
            ),
            Event(
                id = 10,
                name = "Open Source Contributions",
                description = "How to contribute to OSS projects",
                ifFullDay = false,
                startTime = "2025-05-25T10:00:00",
                endTime = "2025-05-25T12:00:00",
                status = "scheduled",
                createdAt = "2025-05-09T18:00:00",
                roomName = "Room G",
                roomDescription = "Collaboration zone",
                roomDistribution = "{\"Scenery\":[],\"Seats\":[]}",
                roomHasSeatDistribution = true
            )
        )
    }

    suspend fun bookSeats(eventId: Int): List<String> {
        return listOf("A1", "A2", "B3")
    }

    suspend fun reserveSeats(eventId: Int, seatIds: List<String>): Boolean {
        println("Reservant butaques MOCK: $seatIds per a l’event $eventId")
        return true
    }

    override suspend fun getFreeSeats(eventId: Int): List<Seat> {
        return when (eventId) {
            1 -> listOf(
                Seat(1, "A", 1),
                Seat(2, "A", 2),
                Seat(3, "B", 1)
            )
            2 -> listOf(
                Seat(4, "A", 1),
                Seat(5, "B", 2)
            )
            3 -> listOf(
                Seat(6, "C", 1),
                Seat(7, "C", 2),
                Seat(8, "C", 3)
            )
            else -> emptyList()
        }
    }

    override suspend fun bookSeats(eventId: Int, seatIds: List<Int>): Boolean {
        println("Booking mocked seats for event $eventId: $seatIds")
        return true
    }

    override suspend fun getMyTickets(): List<Ticket> {
        val events = getAllEvents()

        return listOf(
            Ticket(
                id = 1,
                eventId = 1,
                eventName = events.first { it.id == 1 }.name,
                seatRow = "A",
                seatNumber = 1,
                reservationDate = "2025-05-15T09:00:00",
                status = "reserved"
            ),
            Ticket(
                id = 2,
                eventId = 2,
                eventName = events.first { it.id == 2 }.name,
                seatRow = "B",
                seatNumber = 1,
                reservationDate = "2025-05-15T10:30:00",
                status = "reserved"
            )
        )
    }

    override suspend fun getMyIncidences(): List<Incidence> {
        return incidencesList
    }

    override suspend fun createIncidence(reason: String): Boolean {
        incidencesList.add(Incidence(
            id = 1,
            reason = reason,
            status = IncidenceTypes.OPEN,
        ))
        return true
    }
}