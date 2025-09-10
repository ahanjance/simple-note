package com.example.simplenote

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

        // Helper function to automatically hide/show placeholder
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

            // Initial hint setup
            updateHint()
        }

        // Apply helper for email
        setupHintBehavior(
            findViewById(R.id.inputLayoutEmail),
            findViewById(R.id.inputEmail),
            R.string.placeholder_email
        )

        // Apply helper for password
        setupHintBehavior(
            findViewById(R.id.inputLayoutPassword),
            findViewById(R.id.inputPassword),
            R.string.placeholder_password
        )
    }
}
