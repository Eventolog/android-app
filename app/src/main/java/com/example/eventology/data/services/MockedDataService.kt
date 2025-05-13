package com.example.eventology.data.services

import android.content.Context
import com.example.eventology.constants.UserTypes
import com.example.eventology.data.models.Event
import com.example.eventology.data.models.User
import com.example.eventology.R

object MockDataService : DataServiceInterface {
    var user: User? = null;

    private fun matchNormalUserCredentials(email: String, password: String): Boolean{
        var validEmail: String = "normal@gmail.com"
        var validPassword: String = "passw0rd"
        val matchNormalUserCredentials = email.equals(validEmail) && password.equals(validPassword);
        return matchNormalUserCredentials;
    }

    private fun matchOrganizerUserCredentials(email: String, password: String): Boolean{
        var validEmail: String = "organizer@gmail.com"
        var validPassword: String = "passw0rd"
        val matchNormalUserCredentials = email.equals(validEmail) && password.equals(validPassword);
        return matchNormalUserCredentials;
    }

    override suspend fun login(email: String, password: String, context: Context): String? {
        var errorMsg: String? = null;
        var matchNormalUser = matchNormalUserCredentials(email, password)
        var matchOrganizerUser = matchOrganizerUserCredentials(email, password)
        if(matchNormalUser){
            this.user = User(
                name = "Usuario Normal",
                email = email,
                type = UserTypes.NORMAL,
                jwt = ""
            );
        }else if(matchOrganizerUser){
            this.user = User(
                name = "Usuario Organizador",
                email = email,
                type = UserTypes.ORGANIZER,
                jwt = ""
            );
        }
        else{
            errorMsg = context.getString(R.string.error_invalid_credentials)
        }

        return errorMsg
    }

    override suspend fun signup(name: String, email: String, password: String, context: Context): String? {
        var errorMsg: String? = null;

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
//            Event(
//                id = 1,
//                name = "Startup Pitch",
//                description = "Pitch your startup ideas",
//                ifFullDay = false,
//                startTime = "2025-06-10T10:00:00",
//                endTime = "2025-06-10T13:00:00",
//                status = "scheduled",
//                createdAt = "2025-05-12T20:09:05.77",
//                roomName = "Room A",
//                roomDescription = "A small conference room",
//                roomDistribution = "{\"Scenery\":[],\"Seats\":[]}",
//                roomHasSeatDistribution = true
//            )
        )
    }
}
