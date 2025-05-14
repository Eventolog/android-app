package com.example.eventology.activities

import java.util.*
import android.view.View
import android.os.Bundle
import android.view.MenuItem
import android.content.Intent
import android.content.Context
import android.widget.PopupMenu
import com.example.eventology.R
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope
import com.example.eventology.constants.BaseActivity
import com.example.eventology.databinding.ActivityLoginBinding
import com.example.eventology.data.services.ApiServiceProvider
import com.example.eventology.data.services.DataServiceInterface

class LoginActivity : BaseActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun attachBaseContext(newBase: Context) {
        val prefs = newBase.getSharedPreferences("settings", Context.MODE_PRIVATE)
        val lang = prefs.getString("lang", "en") ?: "en"
        val locale = Locale(lang)
        Locale.setDefault(locale)

        val config = newBase.resources.configuration
        config.setLocale(locale)
        config.setLayoutDirection(locale)

        val context = newBase.createConfigurationContext(config)
        super.attachBaseContext(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (userAlreadyLoggedIn()) {
            val intent = Intent(this, AuthenticatedActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.languageSelector.setOnClickListener { view ->
            showLanguagePopup(view)
        }

        binding.loginButton.setOnClickListener {
            handleLoginClick()
        }

        binding.registerText.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun handleLoginClick(){
        println("start login")
        val context: Context = this.baseContext

        lifecycleScope.launch {
            println("lifecycle scope launch kotlin")
            val apiService: DataServiceInterface = ApiServiceProvider.getDataService()
            val email = binding.emailEditText.text?.toString() ?: ""
            val password = binding.passwordEditText.text?.toString() ?: ""

            val errMsg = apiService.login(email, password, context)
            println("error msg: $errMsg")
            binding.errorMessageTextView.text = errMsg
            binding.errorMessageTextView.setPadding(48, 24, 48, 24)
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

    private fun userAlreadyLoggedIn(): Boolean {
        val prefs = getSharedPreferences("session", Context.MODE_PRIVATE)
        return prefs.contains("token")
    }
}