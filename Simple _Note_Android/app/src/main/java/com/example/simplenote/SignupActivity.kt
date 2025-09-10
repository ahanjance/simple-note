package com.example.simplenote

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.text.TextWatcher
import android.text.Editable
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textfield.TextInputEditText
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.button.MaterialButton

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

        // Helper function to handle placeholder disappearance
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

        // Setup all 6 input fields
        setupHintBehavior(findViewById(R.id.inputFirstNameLayout), findViewById(R.id.inputFirstName), R.string.placeholder_firstname)
        setupHintBehavior(findViewById(R.id.inputLastNameLayout), findViewById(R.id.inputLastName), R.string.placeholder_lastname)
        setupHintBehavior(findViewById(R.id.inputUsernameLayout), findViewById(R.id.inputUsername), R.string.placeholder_username)
        setupHintBehavior(findViewById(R.id.inputEmailLayout), findViewById(R.id.inputEmail), R.string.placeholder_email)
        setupHintBehavior(findViewById(R.id.inputPasswordLayout), findViewById(R.id.inputPassword), R.string.placeholder_password)
        setupHintBehavior(findViewById(R.id.inputConfirmPasswordLayout), findViewById(R.id.inputConfirmPassword), R.string.placeholder_confirm_password)

        // "Already have an account?" button
        val logRedirectButton = findViewById<MaterialButton>(R.id.logRedirectButton)
        logRedirectButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish() // prevent back navigation to signup
        }
    }
}
