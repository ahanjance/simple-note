package com.example.simplenote

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Back button
        val backButton: ImageView = findViewById(R.id.button_back)
        backButton.setOnClickListener { finish() }

        // Change Password button
        val changePassButton: ImageView = findViewById(R.id.button_change_pass)
        changePassButton.setOnClickListener {
            val intent = Intent(this, ChangePassActivity::class.java)
            startActivity(intent)
        }

        // Load user info
        val prefs = getSharedPreferences("auth", MODE_PRIVATE)
        val username = prefs.getString("username", "Guest") ?: "Guest"
        val email = prefs.getString("email", "guest@email.com") ?: "guest@email.com"

        findViewById<TextView>(R.id.text_username).text = username
        findViewById<TextView>(R.id.text_email).text = email
    }
}
