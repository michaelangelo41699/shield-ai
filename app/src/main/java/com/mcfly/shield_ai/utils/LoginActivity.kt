package com.mcfly.shield_ai.utils

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.mcfly.shield_ai.R

class LoginActivity : AppCompatActivity() {

    private var isSignUpMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check if onboarding is complete
        val prefs = getSharedPreferences("ShieldAI", MODE_PRIVATE)
        val hasCompletedOnboarding = prefs.getBoolean("onboarding_complete", false)
        if (hasCompletedOnboarding) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_login)

        // Views
        val usernameInput = findViewById<EditText>(R.id.usernameInput)
        val passwordInput = findViewById<EditText>(R.id.passwordInput)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val toggleText = findViewById<TextView>(R.id.toggleModeText)
        val titleText = findViewById<TextView>(R.id.loginTitle)

        // Toggle between login and sign-up modes
        toggleText.setOnClickListener {
            isSignUpMode = !isSignUpMode
            if (isSignUpMode) {
                loginButton.text = "Sign Up"
                toggleText.text = "Already have an account? Log in"
                titleText.text = "Create Your Shield-AI Account"
            } else {
                loginButton.text = "Log In"
                toggleText.text = "Don’t have an account? Sign up"
                titleText.text = "Welcome to Shield-AI"
            }
        }

        // Handle login or sign-up
        loginButton.setOnClickListener {
            val username = usernameInput.text.toString()
            val password = passwordInput.text.toString()

            if (username.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Please enter both username and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (isSignUpMode) {
                // Save credentials
                prefs.edit()
                    .putString("username", username)
                    .putString("password", password)
                    .apply()

                Toast.makeText(this, "Account created. Please complete onboarding.", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, OnboardingActivity::class.java))
                finish()
            } else {
                val savedUser = prefs.getString("username", null)
                val savedPass = prefs.getString("password", null)

                if (username == savedUser && password == savedPass) {
                    Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, OnboardingActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
