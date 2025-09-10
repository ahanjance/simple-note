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



class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        val rootView = findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.login)
        ViewCompat.setOnApplyWindowInsetsListener(rootView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val inputLayoutEmail = findViewById<TextInputLayout>(R.id.inputLayoutEmail)
        val inputEmail = findViewById<TextInputEditText>(R.id.inputEmail)

        fun updateEmailHint() {
            inputLayoutEmail.hint =
                if (inputEmail.hasFocus() || !inputEmail.text.isNullOrEmpty()) ""
                else getString(R.string.placeholder_email)
        }

        inputEmail.setOnFocusChangeListener { _, _ -> updateEmailHint() }
        inputEmail.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { updateEmailHint() }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Password field
        val inputLayoutPassword = findViewById<TextInputLayout>(R.id.inputLayoutPassword)
        val inputPassword = findViewById<TextInputEditText>(R.id.inputPassword)

        fun updatePasswordHint() {
            inputLayoutPassword.hint =
                if (inputPassword.hasFocus() || !inputPassword.text.isNullOrEmpty()) ""
                else getString(R.string.placeholder_password)
        }

        inputPassword.setOnFocusChangeListener { _, _ -> updatePasswordHint() }
        inputPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { updatePasswordHint() }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

    }

}