package com.example.simplenote

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener

class NoteActivity : AppCompatActivity() {

    private lateinit var titleEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var backButton: ImageView
    private lateinit var saveButton: Button
    private var noteId: Int = -1 // -1 = new note

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_note)

        titleEditText = findViewById(R.id.titleEditText)
        descriptionEditText = findViewById(R.id.descriptionEditText)
        backButton = findViewById(R.id.button_back)
        saveButton = findViewById(R.id.button_save)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        noteId = intent.getIntExtra("note_id", -1)

        // Back button closes activity
        backButton.setOnClickListener { finish() }

        if (noteId == -1) {
            // New note
            titleEditText.setText("")
            descriptionEditText.setText("")
            titleEditText.hint = getString(R.string.note_title)
            descriptionEditText.hint = getString(R.string.note_desc)
            saveButton.setTextColor(getColor(R.color.colorDarkGrey))
            saveButton.isEnabled = false
        } else {
            // Existing note
            val noteTitle = intent.getStringExtra("note_title") ?: ""
            val noteDescription = intent.getStringExtra("note_description") ?: ""
            titleEditText.setText(noteTitle)
            descriptionEditText.setText(noteDescription)
            titleEditText.hint = null
            descriptionEditText.hint = null
            saveButton.setTextColor(getColor(R.color.colorPrimary))
            saveButton.isEnabled = true
        }

        // Enable/disable Save button for new note
        val textWatcher = {
            val titleNotEmpty = titleEditText.text.toString().trim().isNotEmpty()
            val descNotEmpty = descriptionEditText.text.toString().trim().isNotEmpty()
            val enable = titleNotEmpty && descNotEmpty
            saveButton.isEnabled = enable
            saveButton.setTextColor(
                if (enable) getColor(R.color.colorPrimary)
                else getColor(R.color.colorDarkGrey)
            )
        }

        titleEditText.addTextChangedListener { textWatcher() }
        descriptionEditText.addTextChangedListener { textWatcher() }

        // Save button click
        saveButton.setOnClickListener {
            if (!saveButton.isEnabled) return@setOnClickListener

            // TODO: implement saving to local database and mark for syncing
        }
    }
}
