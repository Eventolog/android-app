package com.example.eventology.data.services

import java.net.URL
import org.json.JSONArray
import org.json.JSONObject
import android.content.Context
import android.util.Log
import com.example.eventology.constants.ApiEndpoints
import com.example.eventology.constants.HttpMethods
import java.net.HttpURLConnection
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import com.example.eventology.data.models.User
import com.example.eventology.data.models.Seat
import com.example.eventology.data.models.Event
import com.example.eventology.data.models.Ticket
import com.example.eventology.data.models.Incidence
import com.example.eventology.utils.Utils
import java.io.IOException

/**
 * Implementation of [DataServiceInterface] that connects to the real backend API.
 * Handles user authentication, event retrieval, seat booking, and seat availability.
 */
object RealDataService : DataServiceInterface {
    private var TAG = "ApiService"
    private var user: User = User(
        name = "",
        email = "",
        type = "",
        jwt = ""
    )
    private var BASE_URL = "http://10.0.1.223:49781/api"


    override fun getUser(): User? = user

    /**
     * Creates and configures a [HttpURLConnection] for the given endpoint and HTTP method.
     *
     * @param endpoint The relative endpoint path (e.g., "/user/login").
     * @param method The HTTP method to use (e.g., [HttpMethods.POST]).
     * @return A configured [HttpURLConnection] instance ready for use.
     */
    private fun getBaseConnection(endpoint: String, method: String): HttpURLConnection {
        val fullUrl = buildUrl(endpoint)
        Log.d(TAG, "Creating new HttpURLConnection for endpoint: $endpoint with method: $method")
        val connection = (URL(fullUrl).openConnection() as HttpURLConnection).apply {
            requestMethod = method
            setRequestProperty("Content-Type", "application/json")
            // Only enable output if method allows a body (POST, PUT, DELETE), not for GET
            doOutput = method != HttpMethods.GET
            // disable caching to avoid possible reuse issues
            useCaches = false
        }
        Log.d(TAG, "New connection requestMethod: ${connection.requestMethod}, doOutput: ${connection.doOutput}")
        return connection
    }


    /**
     * Sends an HTTP request to the given endpoint with the specified method and body.
     *
     * The connection is returned to allow the caller to handle the response.
     *
     * @param endpoint The relative URL path (e.g., "/user/login").
     * @param method The HTTP method to use (e.g., [HttpMethods.POST]).
     * @param body The request body as a JSON string.
     * @return The open [HttpURLConnection] ready for reading the response.
     * @throws IOException if an error occurs while opening or writing to the connection.
     */
    @Throws(IOException::class)
    private fun sendRequest(endpoint: String, method: String, body: String): HttpURLConnection {
        val connection = getBaseConnection(endpoint, method)

        if (method != HttpMethods.GET) {
            connection.outputStream.use { outputStream ->
                outputStream.write(body.toByteArray(Charsets.UTF_8))
                outputStream.flush()
            }
        }


        return connection
    }

    /**
     * Sends an HTTP request to the given endpoint with the specified method and body and the
     * stored authenticated user jwt.
     *
     * The connection is returned to allow the caller to handle the response.
     *
     * @param endpoint The relative URL path (e.g., "/user/login").
     * @param method The HTTP method to use (e.g., [HttpMethods.POST]).
     * @param body The request body as a JSON string.
     * @return The open [HttpURLConnection] ready for reading the response.
     * @throws IOException if an error occurs while opening or writing to the connection.
     */
    private fun sendAuthenticatedRequest(endpoint: String, method: String, body: String): HttpURLConnection {
        val connection = getBaseConnection(endpoint, method)  // new connection every time

        // Set auth header
        connection.setRequestProperty("Authorization", "Bearer ${user.jwt}")

        if (method != HttpMethods.GET) {
            connection.outputStream.use { outputStream ->
                outputStream.write(body.toByteArray(Charsets.UTF_8))
                outputStream.flush()
            }
        }
        return connection
    }

    /**
     * Build the full url by combinins the BASE_URL and the endpoint
     *
     * it ensures slashes are not getting duplicated
     * @param endpoint The relative path to append to the base URL (e.g., "/user/login").
     * @return A full URL string ready for use in network requests.
     *
     */
    private fun buildUrl(endpoint: String): String {
        return "$BASE_URL/${endpoint.removePrefix("/")}"
    }



    override suspend fun login(email: String, password: String, context: Context): String? = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "starting login")
            val jsonBody = JSONObject().apply {
                put("email", email)
                put("password", password)
            }
            val connection = sendRequest(ApiEndpoints.LOGIN, HttpMethods.POST, jsonBody.toString())
            if (connection.responseCode == 200) {
                val response = connection.inputStream.bufferedReader().readText()
                var token = Utils.removeDoubleQuotes(response)

                // stores user token
                user.jwt = token
                Log.d(TAG,"login token: $token")

                // execute whoami to get user values
                whoami()

                null
            } else {
                Log.d(TAG, "login failed")
                val errorMsg = connection.errorStream?.bufferedReader()?.readText()?.takeIf { it.isNotBlank() }
                errorMsg ?: "Signup failed with HTTP ${connection.responseCode}"
            }
        } catch (e: Exception) {
            Log.d(TAG, "login exception")
            e.printStackTrace()
            e.message ?: "Network error"
        }
    }

     override suspend fun whoami() {
        try {
            Log.d(TAG, "starting whoami")
            val connection = sendAuthenticatedRequest(ApiEndpoints.WHOAMI, HttpMethods.GET, "")
            Log.d(TAG, "connection method whoami: ${connection.requestMethod}")
            if (connection.responseCode == 200) {
                val response = connection.inputStream.bufferedReader().readText()

                val jsonObject = JSONObject(response)
                val id = jsonObject.getString("id")
                val name = jsonObject.getString("name")
                val type = jsonObject.getString("type")

                Log.d(TAG, "whoami data $id")

                if (name.isNotEmpty()) {
                    user.name = name
                }

                if (type.isNotEmpty()) {
                    user.type = type
                }
                null
            } else {
                val errorMsg = connection.errorStream?.bufferedReader()?.readText()?.takeIf { it.isNotBlank() }
                errorMsg ?: "Signup failed with HTTP ${connection.responseCode}"
                Log.d(TAG, "network error whoami: $errorMsg")
            }
        } catch (e: Exception) {
            Log.d(TAG, "excepion whoami res")
            e.printStackTrace()
            e.message ?: "Network error"
        }
    }

    override suspend fun signup(name: String, email: String, password: String, context: Context): String? = withContext(Dispatchers.IO) {
        try {
            val jsonBody = JSONObject().apply {
                put("name", name)
                put("email", email)
                put("password", password)
            }

            val connection = sendRequest(ApiEndpoints.SIGNUP, HttpMethods.POST, jsonBody.toString())

            if (connection.responseCode == 200 ) {
                val response = connection.inputStream.bufferedReader().readText()
                var token = Utils.removeDoubleQuotes(response)

                // stores user token
                user.jwt = token
                Log.d(TAG,"login token: $token")

                // execute whoami to get user values
                whoami()

                null
            } else {
                val errorMsg = connection.errorStream?.bufferedReader()?.readText()?.takeIf { it.isNotBlank() }
                errorMsg ?: "Signup failed with HTTP ${connection.responseCode}"
            }
        } catch (e: Exception) {
            e.printStackTrace()
            e.message ?: "Network error"
        }
    }


    override suspend fun getAllEvents(): List<Event> = withContext(Dispatchers.IO) {
        try {
            var connection = sendRequest(ApiEndpoints.EVENTS, HttpMethods.GET, "")
           if(connection.responseCode == 200){
               val response = connection.inputStream.bufferedReader().readText()
               val jsonArray = JSONObject("{\"data\":$response}").getJSONArray("data")
               val events = mutableListOf<Event>()

               for (i in 0 until jsonArray.length()) {
                   val e = jsonArray.getJSONObject(i)
                   val id = e.optInt("id", -1)
                   val name = e.optString("name", "Unknown Event")
                   val description = e.optString("description", "")
                   val ifFullDay = e.optBoolean("if_full_day", false)  // note snake_case key from JSON
                   val startTime = e.optString("start_time", "")
                   val endTime = e.optString("end_time", "")
                   val status = e.optString("status", "")
                   val createdAt = e.optString("created_at", "")
                   val roomName = e.optString("roomName", "")
                   val roomDescription = e.optString("roomDescription", "")
                   val roomDistribution = e.optString("roomDistribution", "")
                   val roomHasSeatDistribution = e.optBoolean("roomHasSeatDistribution", false)

                   val event = Event(
                       id = id,
                       name = name,
                       description = description,
                       ifFullDay = ifFullDay,
                       startTime = startTime,
                       endTime = endTime,
                       status = status,
                       createdAt = createdAt,
                       roomName = roomName,
                       roomDescription = roomDescription,
                       roomDistribution = roomDistribution,
                       roomHasSeatDistribution = roomHasSeatDistribution
                   )

                   events.add(event)
               }

               Log.d(TAG, "get all events ${events.size}")

               events
           }else{
               Log.d(TAG, "get empty events")

               listOf()
           }

        } catch (e: Exception) {
            Log.d(TAG, "events error: ${e.toString()}")
            e.printStackTrace()
            emptyList()
        }
    }

    override suspend fun getFreeSeats(eventId: Int): List<Seat> = withContext(Dispatchers.IO) {
        try {
            val connection = sendAuthenticatedRequest(
                ApiEndpoints.getFreeSeats(eventId),
                HttpMethods.GET,
                ""
            )

            if(connection.responseCode == 200){
                val response = connection.inputStream.bufferedReader().readText()
                val seatsJson = JSONArray(response)
                val seats = mutableListOf<Seat>()

                for (i in 0 until seatsJson.length()) {
                    val obj = seatsJson.getJSONObject(i)
                    seats.add(Seat(obj.getInt("id"), obj.getString("row_number"), obj.getInt("seat_number")))
                }

                Log.d(TAG, "get all free seats ${seats.size}")

                seats
            }else{
                Log.d(TAG, "get empty free seats")

                listOf()
            }

        } catch (e: Exception) {
            Log.d(TAG, "free seats error: ${e.toString()}")

            e.printStackTrace()
            emptyList()
        }
    }

    override suspend fun bookSeats(eventId: Int, seatIds: List<Int>): Boolean = withContext(Dispatchers.IO) {
        try {
            seatIds.forEach { seatId ->
                val jsonBody = JSONObject().put("seatId", seatId)

                val connection = sendAuthenticatedRequest(
                    ApiEndpoints.bookSeat(eventId),
                    HttpMethods.POST,
                    jsonBody.toString()
                )

                if (connection.responseCode != 200) return@withContext false
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override suspend fun getMyTickets(): List<Ticket> = withContext(Dispatchers.IO) {
        try {
            val connection = sendAuthenticatedRequest(ApiEndpoints.MY_TICKETS, HttpMethods.GET, "")
            val response = connection.inputStream.bufferedReader().readText()

            if (connection.responseCode == 200) {
                val ticketsJson = JSONArray(response)
                val tickets = mutableListOf<Ticket>()

                for (i in 0 until ticketsJson.length()) {
                    val t = ticketsJson.getJSONObject(i)

                    val event = t.optJSONObject("Event")
                    val seat = t.optJSONObject("Seat")

                    tickets.add(
                        Ticket(
                            id = t.getInt("id"),
                            eventId = event?.getInt("id") ?: 0,
                            eventName = event?.getString("name") ?: "",
                            seatRow = seat?.optInt("row_number")?.toString(),
                            seatNumber = seat?.optInt("seat_number"),
                            reservationDate = t.getString("reservation"),
                            status = t.getString("status")
                        )
                    )
                }

                Log.d(TAG, "tickets size:  ${tickets.size}")
                tickets
            } else {
                Log.d(TAG, "status code: ${connection.responseCode}")
                Log.d(TAG, "message: ${response}")
                Log.d(TAG, "get empty tickets")
                listOf()
            }
        } catch (e: Exception) {
            Log.d(TAG, "tickets error: ${e.toString()}")
            e.printStackTrace()
            emptyList()
        }
    }


    override suspend fun getMyIncidences(): List<Incidence> = withContext(Dispatchers.IO) {
        try {
            val connection =
                sendAuthenticatedRequest(ApiEndpoints.INCIDENCES, HttpMethods.GET, "")
            val response = connection.inputStream.bufferedReader().readText()

            if (connection.responseCode == 200) {
                val incidencesJson = JSONArray(response)
                val incidences = mutableListOf<Incidence>()

                for (i in 0 until incidencesJson.length()) {
                    val i = incidencesJson.getJSONObject(i)

                    incidences.add(
                        Incidence(
                            id = i.getInt("id"),
                            reason = i.getString("reason"),
                            status = i.getString("status")
                        )
                    )
                }

                Log.d(TAG, "incidences size:  ${incidences.size}")
                incidences
            } else {
                Log.d(TAG, "status code: ${connection.responseCode}")
                Log.d(TAG, "message: ${response}")
                Log.d(TAG, "get empty incidences")
                listOf()
            }
        } catch (e: Exception) {
            Log.d(TAG, "incidences error: ${e.toString()}")
            e.printStackTrace()
            emptyList()
        }
    }

    override suspend fun createIncidence(reason: String): Boolean  = withContext(Dispatchers.IO) {
        try {
            val jsonBody = JSONObject().apply {
                put("reason", reason)
            }

            val connection = sendAuthenticatedRequest(ApiEndpoints.INCIDENCES, HttpMethods.POST, jsonBody.toString())
            Log.d(TAG, "response code: ${connection.responseCode}")
            if (connection.responseCode == 200 ) {

                Log.d(TAG, "incidence created properly")
                true
            } else {
                false
            }
        } catch (e: Exception) {
            Log.d(TAG, "incidence create exception: ${e.toString()}")

            e.printStackTrace()
            e.message ?: "Network error"
            false
        }
    }
}