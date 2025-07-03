package com.example.personalfinancetrackerapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SignUpActivity : AppCompatActivity() {

    private lateinit var fullName: EditText
    private lateinit var mobileNumber: EditText
    private lateinit var newUsername: EditText
    private lateinit var newPassword: EditText
    private lateinit var registerButton: Button

    private val PREFS_NAME = "LoginPrefs"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        // Initialize views
        fullName = findViewById(R.id.fullName)
        mobileNumber = findViewById(R.id.mobileNumber)
        newUsername = findViewById(R.id.newUsername)
        newPassword = findViewById(R.id.newPassword)
        registerButton = findViewById(R.id.registerButton)

        registerButton.setOnClickListener {
            val name = fullName.text.toString().trim()
            val mobile = mobileNumber.text.toString().trim()
            val username = newUsername.text.toString().trim()
            val password = newPassword.text.toString().trim()

            // Empty field validation
            if (name.isEmpty() || mobile.isEmpty() || username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Mobile number validation
            if (!mobile.matches(Regex("^[0-9]{10}$"))) {
                Toast.makeText(this, "Enter a valid 10-digit mobile number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Save data to SharedPreferences
            val sharedPref = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            with(sharedPref.edit()) {
                putString("name", name)
                putString("mobile", mobile)
                putString("username", username)
                putString("password", password)
                apply()
            }

            // Clear fields after saving
            fullName.text.clear()
            mobileNumber.text.clear()
            newUsername.text.clear()
            newPassword.text.clear()

            // Success message
            Toast.makeText(this, "Account Created Successfully. Please Login.", Toast.LENGTH_SHORT).show()

            // Navigate back to Login Page
            startActivity(Intent(this, Loginpage::class.java))
            finish()
        }
    }
}