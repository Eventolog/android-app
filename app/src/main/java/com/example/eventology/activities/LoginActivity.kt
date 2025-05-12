package com.example.eventology.activities

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import com.example.eventology.R
import com.example.eventology.databinding.ActivityLoginBinding
import java.util.*

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.languageSelector.setOnClickListener { view ->
            showLanguagePopup(view)
        }
    }

    private fun showLanguagePopup(view: View) {
        val popup = PopupMenu(this, view)
        popup.menuInflater.inflate(R.menu.language_menu, popup.menu)
        popup.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.lang_ca -> setAppLocale("ca")
                R.id.lang_es -> setAppLocale("es")
                R.id.lang_en -> setAppLocale("en")
            }
            true
        }
        popup.show()
    }

    private fun setAppLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        createConfigurationContext(config)
        recreate()
    }
}