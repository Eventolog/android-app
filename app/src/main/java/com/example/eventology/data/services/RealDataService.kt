package com.example.eventology.data.services

import android.content.Context
import com.example.eventology.R
import com.example.eventology.data.models.Event
import com.example.eventology.data.models.Seat
import com.example.eventology.data.models.User
import com.example.eventology.constants.UserTypes
import com.example.eventology.data.models.Ticket
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

/**
 * Implementation of [DataServiceInterface] that connects to the real backend API.
 * Handles user authentication, event retrieval, seat booking, and seat availability.
 */
object RealDataService : DataServiceInterface {
    private var user: User? = null
    private var BASE_URL = "http://10.0.1.223:49781/api"

    /**
     * Returns the currently authenticated user, or null if no user is logged in.
     */
    override fun getUser(): User? = this.user

    /**
     * Retrieves a list of reserved seat IDs (as Strings) for a given event.
     * This is calculated by assuming all seat IDs range from 1 to 50 and subtracting the free ones.
     *
     * @param eventId The ID of the event.
     * @return A list of reserved seat IDs.
     */
    override suspend fun getReservedSeats(eventId: Int): List<String> = withContext(Dispatchers.IO) {
        try {
            val url = URL("${BASE_URL}/$eventId/getFreeSeats")
            val connection = url.openConnection() as HttpURLConnection
            connection.setRequestProperty("Authorization", "Bearer ${user?.jwt}")
            connection.requestMethod = "GET"

            val response = connection.inputStream.bufferedReader().readText()
            val freeSeatsJson = JSONArray(response)
            val freeSeatIds = mutableSetOf<String>()

            for (i in 0 until freeSeatsJson.length()) {
                val seat = freeSeatsJson.getJSONObject(i)
                freeSeatIds.add(seat.getInt("id").toString())
            }

            val allSeatIds = (1..50).map { it.toString() }
            allSeatIds.filterNot { it in freeSeatIds }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * Attempts to reserve a list of seats for a given event by calling the API.
     *
     * @param eventId The ID of the event.
     * @param seatIds A list of seat IDs (as Strings) to reserve.
     * @return `true` if all seats are reserved successfully, `false` otherwise.
     */
    override suspend fun reserveSeats(eventId: Int, seatIds: List<String>): Boolean = withContext(Dispatchers.IO) {
        try {
            for (seatId in seatIds) {
                val url = URL("${BASE_URL}/$eventId/bookSeat")
                val connection = url.openConnection() as HttpURLConnection
                connection.setRequestProperty("Authorization", "Bearer ${user?.jwt}")
                connection.setRequestProperty("Content-Type", "application/json")
                connection.requestMethod = "POST"
                connection.doOutput = true

                val jsonBody = JSONObject().put("seatId", seatId.toInt())
                connection.outputStream.use {
                    it.write(jsonBody.toString().toByteArray())
                }

                if (connection.responseCode != 200) return@withContext false
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Attempts to log in a user by sending credentials to the backend API.
     *
     * @param email The user's email address.
     * @param password The user's password.
     * @param context The application context (used for string resources).
     * @return An error message if login fails, or `null` on success.
     */
    override suspend fun login(email: String, password: String, context: Context): String? = withContext(Dispatchers.IO) {
        try {
            val url = URL("${BASE_URL}/user/login")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true

            val jsonBody = JSONObject().apply {
                put("email", email)
                put("password", password)
            }

            connection.outputStream.use {
                it.write(jsonBody.toString().toByteArray())
            }

            if (connection.responseCode == 200) {
                val response = connection.inputStream.bufferedReader().readText()
                val json = JSONObject(response)
                val role = json.getString("role").lowercase()
                val userType = when (role) {
                    UserTypes.NORMAL -> UserTypes.NORMAL
                    UserTypes.ORGANIZER -> UserTypes.ORGANIZER
                    else -> UserTypes.NORMAL
                }

                user = User(
                    name = json.getString("name"),
                    email = email,
                    jwt = json.getString("token"),
                    type = userType
                )
                null
            } else {
                context.getString(R.string.error_invalid_credentials)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            context.getString(R.string.error_network)
        }
    }

    /**
     * Registers a new user in the backend API.
     *
     * @param name The user's full name.
     * @param email The user's email.
     * @param password The user's password.
     * @param context The application context (used for string resources).
     * @return An error message if signup fails, or `null` on success.
     */
    override suspend fun signup(name: String, email: String, password: String, context: Context): String? = withContext(Dispatchers.IO) {
        try {
            val url = URL("${BASE_URL}/user/signup")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true

            val jsonBody = JSONObject().apply {
                put("name", name)
                put("email", email)
                put("password", password)
            }

            connection.outputStream.use {
                it.write(jsonBody.toString().toByteArray())
            }

            if (connection.responseCode == 201) null
            else context.getString(R.string.error_signup_failed)
        } catch (e: Exception) {
            e.printStackTrace()
            context.getString(R.string.error_network)
        }
    }

    /**
     * Retrieves a list of all available events from the backend API.
     *
     * @return A list of [Event] objects or an empty list in case of failure.
     */
    override suspend fun getAllEvents(): List<Event> = withContext(Dispatchers.IO) {
        try {
            val url = URL("${BASE_URL}/events")
            val connection = url.openConnection() as HttpURLConnection
            connection.setRequestProperty("Authorization", "Bearer ${user?.jwt}")
            connection.requestMethod = "GET"

            val response = connection.inputStream.bufferedReader().readText()
            val jsonArray = JSONObject("{\"data\":$response}").getJSONArray("data")
            val events = mutableListOf<Event>()

            for (i in 0 until jsonArray.length()) {
                val e = jsonArray.getJSONObject(i)
                events.add(
                    Event(
                        id = e.getInt("id"),
                        name = e.getString("name"),
                        description = e.getString("description"),
                        ifFullDay = e.getBoolean("ifFullDay"),
                        startTime = e.getString("startTime"),
                        endTime = e.getString("endTime"),
                        status = e.getString("status"),
                        createdAt = e.getString("createdAt"),
                        roomName = e.getString("roomName"),
                        roomDescription = e.getString("roomDescription"),
                        roomDistribution = e.getString("roomDistribution"),
                        roomHasSeatDistribution = e.getBoolean("roomHasSeatDistribution")
                    )
                )
            }

            events
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * Retrieves the list of free (not yet reserved) seats for the given event.
     *
     * @param eventId The ID of the event.
     * @return A list of [Seat] objects.
     */
    override suspend fun getFreeSeats(eventId: Int): List<Seat> = withContext(Dispatchers.IO) {
        try {
            val url = URL("${BASE_URL}/$eventId/getFreeSeats")
            val connection = url.openConnection() as HttpURLConnection
            connection.setRequestProperty("Authorization", "Bearer ${user?.jwt}")
            connection.requestMethod = "GET"

            val response = connection.inputStream.bufferedReader().readText()
            val seatsJson = JSONArray(response)
            val seats = mutableListOf<Seat>()

            for (i in 0 until seatsJson.length()) {
                val obj = seatsJson.getJSONObject(i)
                seats.add(Seat(obj.getInt("id"), obj.getString("row_number"), obj.getInt("seat_number")))
            }

            seats
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * Books a list of seat IDs for the given event.
     *
     * @param eventId The ID of the event.
     * @param seatIds The list of seat IDs to be booked.
     * @return `true` if all bookings succeed, `false` if any fail.
     */
    override suspend fun bookSeats(eventId: Int, seatIds: List<Int>): Boolean = withContext(Dispatchers.IO) {
        try {
            seatIds.forEach { seatId ->
                val url = URL("${BASE_URL}/$eventId/bookSeat")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Authorization", "Bearer ${user?.jwt}")
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true

                val jsonBody = JSONObject().put("seatId", seatId)
                connection.outputStream.use {
                    it.write(jsonBody.toString().toByteArray())
                }

                if (connection.responseCode != 200) return@withContext false
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Retrieves the list of tickets booked by the current authenticated user.
     *
     * @return A list of [Ticket] objects.
     */
    override suspend fun getMyTickets(): List<Ticket> = withContext(Dispatchers.IO) {
        try {
            val url = URL("${BASE_URL}/tickets/my")
            val connection = url.openConnection() as HttpURLConnection
            connection.setRequestProperty("Authorization", "Bearer \${user?.jwt}")
            connection.requestMethod = "GET"

            val response = connection.inputStream.bufferedReader().readText()
            val ticketsJson = JSONArray(response)
            val tickets = mutableListOf<Ticket>()

            for (i in 0 until ticketsJson.length()) {
                val t = ticketsJson.getJSONObject(i)
                val seat = t.optJSONObject("seat")

                tickets.add(
                    Ticket(
                        id = t.getInt("id"),
                        eventId = t.getInt("eventId"),
                        eventName = t.getString("eventName"),
                        seatRow = seat?.optString("row_number"),
                        seatNumber = seat?.optInt("seat_number"),
                        reservationDate = t.getString("reservation"),
                        status = t.getString("status")
                    )
                )
            }

            tickets
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}