package com.example.eventology.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.eventology.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        var intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}