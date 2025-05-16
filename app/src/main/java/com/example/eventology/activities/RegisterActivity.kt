package com.example.eventology.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.eventology.constants.BaseActivity
import com.example.eventology.data.services.ApiServiceProvider
import com.example.eventology.data.services.DataServiceInterface
import com.example.eventology.databinding.ActivityRegisterBinding
import kotlinx.coroutines.launch

class RegisterActivity : BaseActivity() {
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginText.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.registerButton.setOnClickListener {
            handleRegister()
        }
    }

    private fun handleRegister() {
        println("start register")
        val context: Context = this@RegisterActivity

        lifecycleScope.launch {
            val apiService: DataServiceInterface = ApiServiceProvider.getDataService()
            val name = binding.nameEditText.text?.toString() ?: ""
            val email = binding.emailEditText.text?.toString() ?: ""
            val password = binding.passwordEditText.text?.toString() ?: ""

            val errMsg = apiService.signup(name, email, password, context)
            println("error msg: $errMsg")

            if (errMsg != null) {
                Toast.makeText(this@RegisterActivity, errMsg, Toast.LENGTH_LONG).show()
                return@launch
            } else {
                Toast.makeText(this@RegisterActivity, "Registre correcte", Toast.LENGTH_SHORT).show()

                // Guarda l'usuari a SharedPreferences
                val prefs = getSharedPreferences("session", Context.MODE_PRIVATE)
                prefs.edit()
                    .putString("email", email)
                    .putString("password", password)
                    .apply()

                // Inicia la pantalla autenticada
                val intent = Intent(this@RegisterActivity, AuthenticatedActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}