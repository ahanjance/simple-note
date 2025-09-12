package com.example.simplenote

import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import com.example.simplenote.local.LocalNote
import com.example.simplenote.repository.NoteRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class NoteActivity : AppCompatActivity() {

    private lateinit var titleEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var backButton: ImageView
    private lateinit var saveButton: Button
    private lateinit var bottomBar: ConstraintLayout
    private lateinit var lastEditedText: TextView
    private lateinit var deleteButton: ImageButton

    // Delete modal views
    private lateinit var overlay: View
    private lateinit var modalDeleteContainer: ConstraintLayout
    private lateinit var buttonClose: ImageButton
    private lateinit var buttonDelNote: ImageButton

    private var noteId: Int = -1
    private var idOnServer: Int? = null
    private lateinit var repository: NoteRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_note)

        // Initialize views
        titleEditText = findViewById(R.id.titleEditText)
        descriptionEditText = findViewById(R.id.descriptionEditText)
        backButton = findViewById(R.id.button_back)
        saveButton = findViewById(R.id.button_save)
        bottomBar = findViewById(R.id.bottomBar)
        lastEditedText = findViewById(R.id.lastEditedText)
        deleteButton = findViewById(R.id.deleteButton)

        overlay = findViewById(R.id.overlay)
        modalDeleteContainer = findViewById(R.id.modal_delete_container)
        buttonClose = findViewById(R.id.button_close)
        buttonDelNote = findViewById(R.id.button_delnote)

        repository = NoteRepository(this)

        // Edge-to-edge padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Load note if it exists
        noteId = intent.getIntExtra("note_id", -1)
        idOnServer = intent.getIntExtra("note_id_on_server", -1).takeIf { it != -1 }

        backButton.setOnClickListener { finish() }

        if (noteId != -1) {
            CoroutineScope(Dispatchers.IO).launch {
                val note = repository.getLocalNotes().find { it.id == noteId }
                note?.let {
                    withContext(Dispatchers.Main) {
                        titleEditText.setText(it.title)
                        descriptionEditText.setText(it.description)
                        saveButton.isEnabled = false
                        saveButton.setTextColor(getColor(R.color.colorDarkGrey))
                        bottomBar.visibility = View.VISIBLE
                        lastEditedText.text = formatLastEdited(it.updatedAt)
                    }
                }
            }
        }

        val textWatcher = {
            val titleNotEmpty = titleEditText.text.toString().trim().isNotEmpty()
            val descNotEmpty = descriptionEditText.text.toString().trim().isNotEmpty()
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

        // Save note
        saveButton.setOnClickListener {
            if (!saveButton.isEnabled) return@setOnClickListener

            val now = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            val localNote = LocalNote(
                id = if (noteId == -1) (0..Int.MAX_VALUE).random() else noteId,
                idOnServer = idOnServer,
                title = titleEditText.text.toString().trim(),
                description = descriptionEditText.text.toString().trim(),
                createdAt = now,
                updatedAt = now,
                creatorName = "Me",
                creatorUsername = "me",
                isSynced = false
            )

            CoroutineScope(Dispatchers.IO).launch {
                repository.saveNotesLocally(listOf(localNote))
                withContext(Dispatchers.Main) {
                    lastEditedText.text = formatLastEdited(localNote.updatedAt)
                    bottomBar.visibility = View.VISIBLE
                    saveButton.isEnabled = false
                    saveButton.setTextColor(getColor(R.color.colorDarkGrey))
                }
            }
        }

        // Delete button shows modal
        deleteButton.setOnClickListener {
            showDeleteModal()
        }

        // Close modal when overlay or close button clicked
        overlay.setOnClickListener { hideDeleteModal() }
        buttonClose.setOnClickListener { hideDeleteModal() }

        // Delete note permanently
        buttonDelNote.setOnClickListener {
            if (noteId != -1) {
                CoroutineScope(Dispatchers.IO).launch {
                    repository.deleteLocalNote(noteId)
                    withContext(Dispatchers.Main) {
                        hideDeleteModal()
                        finish() // Go back to NoteListActivity
                    }
                }
            }
        }
    }

    private fun formatLastEdited(updatedAt: String): String {
        return "Last edited on $updatedAt"
    }

    private fun showDeleteModal() {
        overlay.visibility = View.VISIBLE
        modalDeleteContainer.visibility = View.VISIBLE
        modalDeleteContainer.post {
            modalDeleteContainer.translationY = modalDeleteContainer.height.toFloat()
            modalDeleteContainer.animate()
                .translationY(0f)
                .setDuration(200)
                .start()
        }
    }

    private fun hideDeleteModal() {
        modalDeleteContainer.animate()
            .translationY(modalDeleteContainer.height.toFloat())
            .setDuration(200)
            .withEndAction {
                modalDeleteContainer.visibility = View.GONE
                overlay.visibility = View.GONE
            }
            .start()
    }

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
