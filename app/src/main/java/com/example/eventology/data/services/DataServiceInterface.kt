package com.example.eventology.data.services

import android.content.Context
import com.example.eventology.data.models.Event
import com.example.eventology.data.models.Seat
import com.example.eventology.data.models.User

/**
 * Interface defining the contract for a data service.
 *
 * Implementations of this interface (such as a mocked or real API client) must be assigned to
 * [ApiServiceProvider] so that activities and fragments can use them to interact with the app's data.
 */
interface DataServiceInterface {

    /**
     * Attempts to log in a user with the given email and password.
     *
     * @param email The user's email address.
     * @param password The user's password.
     * @param context The application context, used to access string resources for error messages.
     * @return A nullable error message string if login fails, or `null` if successful.
     */
    suspend fun login(email: String, password: String, context: Context): String?

    /**
     * Attempts to sign up a new user with the provided name, email, and password.
     *
     * @param name The user's full name.
     * @param email The user's email address.
     * @param password The user's desired password.
     * @param context The application context, used to access string resources for error messages.
     * @return A nullable error message string if signup fails, or `null` if successful.
     */
    suspend fun signup(name: String, email: String, password: String, context: Context): String?

    /**
     * Retrieves a list of all available events from the backend or mocked data source.
     *
     * @return A list of [Event] objects representing the available events.
     */
    suspend fun getAllEvents(): List<Event>

    /**
     * Returns the currently authenticated user, if any.
     *
     * @return An instance of the authenticated [User], or `null` if no user is logged in.
     */
    fun getUser(): User?

    /**
     * Retrieves a list of reserved seat identifiers for the given event.
     *
     * This can be used to determine which seats are already taken before rendering a seating layout.
     *
     * @param eventId The ID of the event.
     * @return A list of seat IDs (as strings) that are currently reserved for the given event.
     */
    suspend fun getReservedSeats(eventId: Int): List<String>

    /**
     * Attempts to reserve one or more seats for the current user for a given event.
     *
     * @param eventId The ID of the event.
     * @param seatIds A list of seat identifiers (as strings) to reserve.
     * @return `true` if the reservation was successful, `false` otherwise.
     */
    suspend fun reserveSeats(eventId: Int, seatIds: List<String>): Boolean

    /**
     * Retrieves a list of available (non-reserved) seats for a specific event.
     *
     * @param eventId The ID of the event.
     * @return A list of [Seat] objects representing the free seats.
     */
    suspend fun getFreeSeats(eventId: Int): List<Seat>

    /**
     * Attempts to book the specified seat(s) for the authenticated user for a given event.
     *
     * Each seat is sent as a separate booking request.
     *
     * @param eventId The ID of the event.
     * @param seatIds A list of seat IDs to book.
     * @return `true` if all bookings succeeded, or `false` if any failed.
     */
    suspend fun bookSeats(eventId: Int, seatIds: List<Int>): Boolean
}