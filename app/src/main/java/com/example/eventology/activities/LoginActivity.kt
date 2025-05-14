package com.example.eventology.activities

import android.content.Context
import android.content.Intent
import java.util.*
import android.os.Bundle
import android.view.View
import android.view.MenuItem
import android.widget.PopupMenu
import com.example.eventology.R
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.eventology.data.services.ApiServiceProvider
import com.example.eventology.data.services.DataServiceInterface
import com.example.eventology.databinding.ActivityLoginBinding
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.languageSelector.setOnClickListener { view ->
            showLanguagePopup(view)
        }

        binding.loginButton.setOnClickListener {
            handleLoginClick()
        }

        binding.registerText.setOnClickListener {
            var intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        var intent = Intent(this, AuthenticatedActivity::class.java)
        startActivity(intent)
    }

    private fun handleLoginClick(){
        println("start login")
        var context: Context = this.baseContext

        lifecycleScope.launch {
            println("lifecycle scope launch kotlin")
            var apiService: DataServiceInterface = ApiServiceProvider.getDataService()
            var email = binding.emailEditText.text?.toString() ?: ""
            var password = binding.passwordEditText.text?.toString() ?: ""

            var errMsg = apiService.login(email, password, context)
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

    private fun setAppLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        createConfigurationContext(config)
        recreate()
    }
}