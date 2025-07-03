package com.example.personalfinancetrackerapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileOutputStream

class Loginpage : AppCompatActivity() {

    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button

    private val PREFS_NAME = "LoginPrefs"
    private val BACKUP_FILENAME = "login_backup.txt"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loginpage)

        usernameEditText = findViewById(R.id.usernameEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        loginButton = findViewById(R.id.loginButton)
        val goToSignUpButton: Button = findViewById(R.id.goToSignUpButton)

        val sharedPref = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        // Restore credentials from backup if SharedPreferences is empty
        var savedUsername = sharedPref.getString("username", "") ?: ""
        var savedPassword = sharedPref.getString("password", "") ?: ""
        if (savedUsername.isEmpty() && savedPassword.isEmpty()) {
            val credentials = restoreCredentialsFromBackup()
            savedUsername = credentials.first
            savedPassword = credentials.second
            if (savedUsername.isNotEmpty() && savedPassword.isNotEmpty()) {
                // Save restored credentials to SharedPreferences
                sharedPref.edit()
                    .putString("username", savedUsername)
                    .putString("password", savedPassword)
                    .apply()
            }
        }

        usernameEditText.setText(savedUsername)
        passwordEditText.setText(savedPassword)

        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter both fields", Toast.LENGTH_SHORT).show()
            } else if (savedUsername.isEmpty() && savedPassword.isEmpty()) {
                Toast.makeText(this, "No account found. Please sign up.", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, SignUpActivity::class.java)
                startActivity(intent)
            } else {
                if (username == savedUsername && password == savedPassword) {
                    backupCredentialsToInternalStorage(username, password)
                    Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("username", username)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show()
                }
            }
        }

        goToSignUpButton.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }

    private fun backupCredentialsToInternalStorage(username: String, password: String) {
        val data = "Username: $username\nPassword: $password"
        try {
            val file = File(filesDir, BACKUP_FILENAME)
            val outputStream = FileOutputStream(file)
            outputStream.write(data.toByteArray())
            outputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to backup data", Toast.LENGTH_SHORT).show()
        }
    }

    private fun restoreCredentialsFromBackup(): Pair<String, String> {
        try {
            val file = File(filesDir, BACKUP_FILENAME)
            if (file.exists()) {
                val text = file.readText()
                val lines = text.split("\n")
                val username = lines.find { it.startsWith("Username: ") }?.substringAfter("Username: ")?.trim() ?: ""
                val password = lines.find { it.startsWith("Password: ") }?.substringAfter("Password: ")?.trim() ?: ""
                return username to password
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to restore credentials", Toast.LENGTH_SHORT).show()
        }
        return "" to ""
    }
}