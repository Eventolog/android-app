package com.example.eventology.data.services

import android.content.Context
import com.example.eventology.R
import com.example.eventology.data.models.Event
import com.example.eventology.data.models.User
import com.example.eventology.constants.UserTypes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

object RealDataService : DataServiceInterface {
    var user: User? = null

    override suspend fun login(email: String, password: String, context: Context): String? {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL("https://10.0.1.223/api/user/login")
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

                val responseCode = connection.responseCode
                if (responseCode == 200) {
                    val response = connection.inputStream.bufferedReader().readText()
                    val json = JSONObject(response)

                    // Conversió segura del camp "role"
                    val role = json.getString("role").lowercase()
                    val userType = when (role) {
                        UserTypes.NORMAL -> UserTypes.NORMAL
                        UserTypes.ORGANIZER -> UserTypes.ORGANIZER
                        else -> UserTypes.NORMAL // per defecte o pots fer throw IllegalArgumentException()
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
    }

    override suspend fun signup(name: String, email: String, password: String, context: Context): String? {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL("https://10.0.1.223/api/user/signup")
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

                val responseCode = connection.responseCode
                if (responseCode == 201) {
                    null
                } else {
                    context.getString(R.string.error_signup_failed)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                context.getString(R.string.error_network)
            }
        }
    }

    override suspend fun getAllEvents(): List<Event> {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL("https://10.0.1.223/api/events")
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
    }

    override fun getUser(): User {
        TODO("Not yet implemented")
    }
}