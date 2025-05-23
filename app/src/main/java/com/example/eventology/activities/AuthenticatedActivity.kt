package com.example.eventology.activities

import android.os.Bundle
import com.example.eventology.R
import com.example.eventology.constants.BaseActivity
import com.example.eventology.fragments.AuthenticatedLayoutFragment

/**
 * Main activity shown after user gets authenticated.
 * Load the [AuthenticatedLayoutFragment] that manages page routing.
 */
class AuthenticatedActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authenticated)

        // start AuthenticatedLayoutFragment
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .setCustomAnimations(
                    android.R.anim.slide_in_left,    // enter
                    android.R.anim.slide_out_right    // exit
                )
                .replace(R.id.main_container, AuthenticatedLayoutFragment())
                .commit()
        }
    }
}