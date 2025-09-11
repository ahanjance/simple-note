package com.example.simplenote

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.simplenote.local.RegisterRequest
import com.example.simplenote.network.RetrofitClient
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

class SignupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_signup)

        val rootView = findViewById<ConstraintLayout>(R.id.signup)
        ViewCompat.setOnApplyWindowInsetsListener(rootView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val signupErrorText = findViewById<TextView>(R.id.signupErrorText)

        fun setupHintBehavior(layout: TextInputLayout, editText: TextInputEditText, placeholderResId: Int) {
            val placeholder = getString(placeholderResId)
            fun updateHint() {
                layout.hint = if (editText.hasFocus() || !editText.text.isNullOrEmpty()) "" else placeholder
            }
            editText.setOnFocusChangeListener { _, _ -> updateHint() }
            editText.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) { updateHint() }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
            updateHint()
        }

        setupHintBehavior(findViewById(R.id.inputFirstNameLayout), findViewById(R.id.inputFirstName), R.string.placeholder_firstname)
        setupHintBehavior(findViewById(R.id.inputLastNameLayout), findViewById(R.id.inputLastName), R.string.placeholder_lastname)
        setupHintBehavior(findViewById(R.id.inputUsernameLayout), findViewById(R.id.inputUsername), R.string.placeholder_username)
        setupHintBehavior(findViewById(R.id.inputEmailLayout), findViewById(R.id.inputEmail), R.string.placeholder_email)
        setupHintBehavior(findViewById(R.id.inputPasswordLayout), findViewById(R.id.inputPassword), R.string.placeholder_password)
        setupHintBehavior(findViewById(R.id.inputConfirmPasswordLayout), findViewById(R.id.inputConfirmPassword), R.string.placeholder_confirm_password)

        val logRedirectButton = findViewById<MaterialButton>(R.id.logRedirectButton)
        logRedirectButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        val signupButton = findViewById<ImageView>(R.id.signupButton)

        signupButton.setOnClickListener {
            // Change button color / image to pressed state
            signupButton.setColorFilter(getColor(R.color.colorPrimaryDark)) // pressed color

            val fName = findViewById<TextInputEditText>(R.id.inputFirstName).text.toString()
            val lName = findViewById<TextInputEditText>(R.id.inputLastName).text.toString()
            val uName = findViewById<TextInputEditText>(R.id.inputUsername).text.toString()
            val mail = findViewById<TextInputEditText>(R.id.inputEmail).text.toString()
            val pass = findViewById<TextInputEditText>(R.id.inputPassword).text.toString()
            val confirmPass = findViewById<TextInputEditText>(R.id.inputConfirmPassword).text.toString()

            signupErrorText.visibility = TextView.GONE

            // Local validation
            if (pass != confirmPass) {
                signupErrorText.text = "Passwords do not match"
                signupErrorText.visibility = TextView.VISIBLE
                signupButton.clearColorFilter() // revert color
                return@setOnClickListener
            }

            if (fName.isBlank() || lName.isBlank() || uName.isBlank() || mail.isBlank() || pass.isBlank()) {
                signupErrorText.text = "Please fill all fields"
                signupErrorText.visibility = TextView.VISIBLE
                signupButton.clearColorFilter() // revert color
                return@setOnClickListener
            }

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = RetrofitClient.apiService.register(
                        RegisterRequest(
                            username = uName,
                            password = pass,
                            email = mail,
                            first_name = fName,
                            last_name = lName
                        )
                    )

                    runOnUiThread {
                        // Revert color once response arrives
                        signupButton.clearColorFilter()

                        if (response.isSuccessful) {
                            startActivity(Intent(this@SignupActivity, LoginActivity::class.java))
                            finish()
                        } else {
                            val errorBody = response.errorBody()?.string()
                            Log.d("Signup", "Raw server response: $errorBody")

                            val fixableCodes = listOf(
                                "first_name_too_short",
                                "last_name_too_short",
                                "username_too_short",
                                "username_already_exists",
                                "email_invalid",
                                "email_already_exists",
                                "password_too_short",
                                "password_missing_number",
                                "password_too_common",
                                "password_entirely_numeric",
                                "invalid"
                            )

                            val fixableTexts = listOf(
                                "first_name is too short",
                                "last_name is too short",
                                "username is too short",
                                "username already exists",
                                "email is invalid",
                                "email already exists",
                                "password is too short",
                                "password missing number"
                            )

                            var displayError = "Server error"

                            if (!errorBody.isNullOrEmpty()) {
                                try {
                                    val json = JSONObject(errorBody)
                                    val errorsArray = json.optJSONArray("errors") ?: json.optJSONArray("detail")
                                    if (errorsArray != null && errorsArray.length() > 0) {
                                        val firstError = errorsArray.getJSONObject(0)
                                        val code = firstError.optString("code", "")
                                        val detail = firstError.optString("detail", "")
                                        val attr = firstError.optString("attr", "")

                                        displayError = when {
                                            code == "unique" && attr == "email" -> "This email is taken. Use another email."
                                            fixableCodes.contains(code) -> detail
                                            fixableTexts.any { detail.contains(it, ignoreCase = true) } -> {
                                                fixableTexts.first { detail.contains(it, ignoreCase = true) }
                                            }
                                            else -> "Server error"
                                        }
                                    }
                                } catch (e: Exception) {
                                    Log.e("Signup", "Error parsing server response: ${e.message}")
                                }
                            }

                            signupErrorText.text = displayError.replaceFirstChar {
                                if (it.isLowerCase()) it.titlecase() else it.toString()
                            }
                            signupErrorText.visibility = TextView.VISIBLE
                        }
                    }
                } catch (e: Exception) {
                    Log.e("Signup", "Exception: ${e.message}")
                    runOnUiThread {
                        signupButton.clearColorFilter() // revert color
                        signupErrorText.text = "Server error"
                        signupErrorText.visibility = TextView.VISIBLE
                    }
                }
            }
        }

    }
}
