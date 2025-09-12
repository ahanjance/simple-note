package com.example.simplenote

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
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

        // Load user info
        val prefs = getSharedPreferences("auth", MODE_PRIVATE)
        val username = prefs.getString("username", "Guest") ?: "Guest"
        val email = prefs.getString("email", "guest@email.com") ?: "guest@email.com"

        findViewById<TextView>(R.id.text_username).text = username
        findViewById<TextView>(R.id.text_email).text = email

        // Buttons
        val backButton: ImageView = findViewById(R.id.button_back)
        val changePassButton: ImageView = findViewById(R.id.button_change_pass)
        val logoutButton: ImageView = findViewById(R.id.button_logout)

        // Overlay views
        val overlay: ConstraintLayout = findViewById(R.id.logoutOverlay)
        val logoutBox: ConstraintLayout = findViewById(R.id.logoutBox)
        val buttonCancel: ImageView = findViewById(R.id.button_cancel)
        val buttonYes: ImageView = findViewById(R.id.button_yes)

        // Load animations
        val buttonScale = AnimationUtils.loadAnimation(this, R.anim.button_scale)
        val overlayAnim = AnimationUtils.loadAnimation(this, R.anim.overlay_fade_scale)

        // Back button
        backButton.setOnClickListener { finish() }

        // Change Password button with scale animation
        changePassButton.setOnClickListener {
            changePassButton.startAnimation(buttonScale)
            val intent = Intent(this, ChangePassActivity::class.java)
            startActivity(intent)
        }

        // Logout button with scale animation and overlay
        logoutButton.setOnClickListener {
            logoutButton.startAnimation(buttonScale)
            overlay.visibility = View.VISIBLE
            overlay.startAnimation(overlayAnim)
        }

        // Clicking outside logout box dismisses overlay
        overlay.setOnClickListener {
            overlay.visibility = View.GONE
        }

        // Clicking inside box should not dismiss overlay
        logoutBox.setOnClickListener {
            // Absorb clicks
        }

        // Cancel button closes overlay
        buttonCancel.setOnClickListener {
            overlay.visibility = View.GONE
        }

        // Yes button logs out user and clears token
        buttonYes.setOnClickListener {
            // Clear bearer token and user info
            prefs.edit().clear().apply()

            // Navigate to login page and clear backstack
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}
