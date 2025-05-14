package com.example.eventology.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.eventology.R
import com.example.eventology.fragments.AuthentiactedLayoutFragment

/**
 * Actividad principal que se muestra después de que el usuario se autentica.
 * Carga el MainFragment que contiene la navegación.
 */
class AuthenticatedActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authenticated)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .setCustomAnimations(
                    android.R.anim.slide_in_left,    // enter
                    android.R.anim.slide_out_right    // exit
                )
                .replace(R.id.main_container, AuthentiactedLayoutFragment())
                .commit()
        }
    }
}
