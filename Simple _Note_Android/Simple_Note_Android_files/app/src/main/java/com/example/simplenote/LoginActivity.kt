package com.example.simplenote

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.simplenote.local.LoginRequest
import com.example.simplenote.local.TokenResponse
import com.example.simplenote.network.RetrofitClient
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.text.Editable
import android.text.TextWatcher

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        val rootView = findViewById<ConstraintLayout>(R.id.login)
        ViewCompat.setOnApplyWindowInsetsListener(rootView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val errorText = findViewById<TextView>(R.id.loginErrorText)

        fun setupHintBehavior(layout: TextInputLayout, editText: TextInputEditText, placeholderResId: Int) {
            val placeholder = getString(placeholderResId)
            fun updateHint() {
                layout.hint = if (editText.hasFocus() || !editText.text.isNullOrEmpty()) "" else placeholder
            }
            editText.setOnFocusChangeListener { _, _ -> updateHint() }
            editText.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    updateHint()
                    errorText.visibility = TextView.GONE // hide error when typing
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
            updateHint()
        }

        val usernameLayout = findViewById<TextInputLayout>(R.id.inputLayoutUsername)
        val usernameEdit = findViewById<TextInputEditText>(R.id.inputUsername)
        val passwordLayout = findViewById<TextInputLayout>(R.id.inputLayoutPassword)
        val passwordEdit = findViewById<TextInputEditText>(R.id.inputPassword)

        setupHintBehavior(usernameLayout, usernameEdit, R.string.placeholder_username)
        setupHintBehavior(passwordLayout, passwordEdit, R.string.placeholder_password)

        val regRedirectButton = findViewById<MaterialButton>(R.id.buttonRegRedirect)
        regRedirectButton.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }

        val loginButton = findViewById<ImageView>(R.id.loginButton)
        loginButton.setOnClickListener {
            val username = usernameEdit.text.toString().trim()
            val password = passwordEdit.text.toString()

            errorText.visibility = TextView.GONE
            errorText.text = ""

            // Apply pressed/loading color filter
            loginButton.setColorFilter(getColor(R.color.colorPrimaryDark))

            if (username.isBlank() || password.isBlank()) {
                errorText.text = "Please enter username and password"
                errorText.visibility = TextView.VISIBLE
                loginButton.clearColorFilter()
                return@setOnClickListener
            }

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    // Login request
                    val response = RetrofitClient.apiService.login(LoginRequest(username = username, password = password))

                    runOnUiThread {
                        loginButton.clearColorFilter()
                    }

                    if (response.isSuccessful) {
                        val body: TokenResponse? = response.body()
                        val access = body?.access
                        val refresh = body?.refresh

                        if (!access.isNullOrBlank()) {
                            // Save tokens
                            val prefs = getSharedPreferences("auth", Context.MODE_PRIVATE)
                            prefs.edit()
                                .putString("access_token", access) // raw token
                                .putString("refresh_token", refresh)
                                .apply()

                            // Fetch notes
                            try {
                                val notesResponse = RetrofitClient.apiService.getNotes("Bearer $access")
                                runOnUiThread {
                                    if (notesResponse.isSuccessful) {
                                        val notes = notesResponse.body()?.results ?: emptyList()
                                        if (notes.isEmpty()) {
                                            startActivity(Intent(this@LoginActivity, StartActivity::class.java))
                                        } else {
                                            startActivity(Intent(this@LoginActivity, NotelistActivity::class.java))
                                        }
                                        finish()
                                    } else {
                                        Log.e("Login", "Notes fetch error: ${notesResponse.errorBody()?.string()}")
                                        errorText.text = "Error loading notes"
                                        errorText.visibility = TextView.VISIBLE
                                    }
                                }
                            } catch (e: Exception) {
                                Log.e("Login", "Exception fetching notes: ${e.message}")
                                runOnUiThread {
                                    errorText.text = "Error loading notes"
                                    errorText.visibility = TextView.VISIBLE
                                }
                            }
                        }
                    } else {
                        runOnUiThread {
                            errorText.text = "Invalid username or password"
                            errorText.visibility = TextView.VISIBLE
                        }
                    }

                } catch (e: Exception) {
                    Log.e("Login", "Network exception: ${e.message}")
                    runOnUiThread {
                        loginButton.clearColorFilter()
                        errorText.text = "Server error"
                        errorText.visibility = TextView.VISIBLE
                    }
                }
            }
        }
    }
}
