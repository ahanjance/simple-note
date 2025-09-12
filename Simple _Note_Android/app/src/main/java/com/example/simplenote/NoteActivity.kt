package com.example.simplenote

import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import com.example.simplenote.local.LocalNote
import com.example.simplenote.repository.NoteRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class NoteActivity : AppCompatActivity() {

    private lateinit var titleEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var backButton: ImageView
    private lateinit var saveButton: Button
    private var noteId: Int = -1 // -1 = new note
    private lateinit var repository: NoteRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_note)

        titleEditText = findViewById(R.id.titleEditText)
        descriptionEditText = findViewById(R.id.descriptionEditText)
        backButton = findViewById(R.id.button_back)
        saveButton = findViewById(R.id.button_save)

        repository = NoteRepository(this)

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
            // Grey out save button initially
            saveButton.setTextColor(getColor(R.color.colorDarkGrey))
            saveButton.isEnabled = false
        }

        // Enable/disable Save button on input changes
        val textWatcher = {
            val titleNotEmpty = titleEditText.text.toString().trim().isNotEmpty()
            val descNotEmpty = descriptionEditText.text.toString().trim().isNotEmpty()

            // Check if text changed from original
            val changedFromOriginal = noteId == -1 ||
                    titleEditText.text.toString() != intent.getStringExtra("note_title") ||
                    descriptionEditText.text.toString() != intent.getStringExtra("note_description")

            val enable = titleNotEmpty && descNotEmpty && changedFromOriginal
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

            val now = Date().toString()
            val localNote = LocalNote(
                id = if (noteId == -1) (0..Int.MAX_VALUE).random() else noteId,
                title = titleEditText.text.toString().trim(),
                description = descriptionEditText.text.toString().trim(),
                createdAt = now,
                updatedAt = now,
                creatorName = "Me", // or fetch from stored username
                creatorUsername = "me",
                isSynced = false
            )

            CoroutineScope(Dispatchers.IO).launch {
                repository.saveNotesLocally(listOf(localNote))
            }

            // Grey out the button after saving
            saveButton.isEnabled = false
            saveButton.setTextColor(getColor(R.color.colorDarkGrey))
        }
    }

    // Hide keyboard when touching outside EditTexts
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            currentFocus?.let { view ->
                val outRect = Rect()
                view.getGlobalVisibleRect(outRect)
                if (!outRect.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
                    view.clearFocus()
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view.windowToken, 0)
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }
}
