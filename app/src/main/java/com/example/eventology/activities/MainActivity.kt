package com.example.eventology.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import com.example.eventology.constants.BaseActivity

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
}