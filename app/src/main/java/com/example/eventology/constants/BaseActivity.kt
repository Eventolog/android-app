package com.example.eventology.constants

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import java.util.*

abstract class BaseActivity : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context) {
        val prefs = newBase.getSharedPreferences("settings", Context.MODE_PRIVATE)
        val lang = prefs.getString("lang", "en") ?: "en"
        val locale = Locale(lang)
        Locale.setDefault(locale)

        val config = Configuration(newBase.resources.configuration)
        config.setLocale(locale)
        config.setLayoutDirection(locale)

        val context = newBase.createConfigurationContext(config)
        super.attachBaseContext(context)
    }

    fun setAppLocale(languageCode: String) {
        val prefs = getSharedPreferences("settings", Context.MODE_PRIVATE)
        prefs.edit().putString("lang", languageCode).apply()

        val intent = Intent(this, this::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}