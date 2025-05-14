package com.example.eventology.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import com.example.eventology.constants.BaseActivity

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        var intent = Intent(this, AuthenticatedActivity::class.java)
        startActivity(intent)
    }
}