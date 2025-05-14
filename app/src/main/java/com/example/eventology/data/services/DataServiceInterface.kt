package com.example.eventology.data.services

import android.content.Context
import com.example.eventology.data.models.Event
import com.example.eventology.data.models.User

/**
 * Interface defining the contract for a data service an implementation that connects to the api
 * or an implementation with mocked data should be created and assigned to ApiServceProvider for be accessed by activities
 */
interface DataServiceInterface {

    /**
     * Attempts to log in a user with the given email and password.
     *
     * @param email The user's email address.
     * @param password The user's password.
     * @param context The application context, used to access resources.
     * @return A nullable error message string if the login fails, or `null` if successful.
     */
    suspend fun login(email: String, password: String, context: Context): String?

    /**
     * Attempts to sign up a new user with the provided name, email, and password.
     *
     * @param name The user's full name.
     * @param email The user's email address.
     * @param password The user's desired password.
     * @param context The application context, used to access resources.
     *
     * @return A nullable error message string if signup fails, or `null` if successful.
     */
    suspend fun signup(name: String, email: String, password: String, context: Context): String?

    /**
     * Retrieves a list of all available events.
     *
     * @return A list of [Event] objects representing the available events.
     */
    suspend fun getAllEvents(): List<Event>

    /**
     * Returns the current authenticated user
     *
     * @return An instance of the authenticated [User] or null if user its not authenticated
     */
    fun getUser(): User?
}