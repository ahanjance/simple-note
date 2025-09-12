package com.example.simplenote

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class ChangePassActivity : AppCompatActivity() {

    private lateinit var errorText: TextView
    private lateinit var currentPassword: TextInputEditText
    private lateinit var newPassword: TextInputEditText
    private lateinit var retypePassword: TextInputEditText
    private lateinit var submitButton: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_change_pass)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Views
        errorText = findViewById(R.id.signupErrorText)
        currentPassword = findViewById(R.id.inputCurrentPassword)
        newPassword = findViewById(R.id.inputNewPassword)
        retypePassword = findViewById(R.id.inputRetypePassword)
        submitButton = findViewById(R.id.button_submit_pass)

        // Back button
        findViewById<ImageView>(R.id.button_back).setOnClickListener { finish() }

        // Submit button functionality
        submitButton.setOnClickListener {
            val oldPass = currentPassword.text.toString()
            val newPass = newPassword.text.toString()
            val retypePass = retypePassword.text.toString()

            // Local validation
            if (newPass != retypePass) {
                errorText.text = "New passwords do not match"
                return@setOnClickListener
            }

            errorText.text = "" // Clear previous error

            // Get token from shared prefs
            val prefs = getSharedPreferences("auth", MODE_PRIVATE)
            val token = prefs.getString("access_token", null)
            if (token.isNullOrBlank()) {
                errorText.text = "Authentication error"
                return@setOnClickListener
            }

            // Make network request
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val client = OkHttpClient()
                    val jsonBody = JSONObject().apply {
                        put("old_password", oldPass)
                        put("new_password", newPass)
                    }
                    val body = jsonBody.toString().toRequestBody("application/json".toMediaTypeOrNull())
                    val request = Request.Builder()
                        .url("https://simple.darkube.app/api/auth/change-password/")
                        .addHeader("Authorization", "Bearer $token")
                        .post(body)
                        .build()
                    val response = client.newCall(request).execute()

                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful) {
                            // Password changed, go back to Settings
                            finish()
                        } else {
                            errorText.text = "Error changing password"
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        errorText.text = "Error changing password"
                    }
                }
            }
        }
    }
}
