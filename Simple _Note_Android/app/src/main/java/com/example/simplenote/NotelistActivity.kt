package com.example.simplenote

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import com.example.simplenote.local.LocalNote
import com.example.simplenote.repository.NoteRepository

class NotelistActivity : AppCompatActivity() {

    private lateinit var repository: NoteRepository
    private lateinit var notesContainer: LinearLayout
    private lateinit var searchInput: EditText
    private var allNotes: List<LocalNote> = emptyList() // Full list for filtering

    // Launcher for NoteActivity
    private val noteActivityLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val saved = result.data?.getBooleanExtra("note_saved", false) ?: false
                if (saved) {
                    // Clear search input if a note was saved
                    searchInput.setText("")
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_notelist)

        notesContainer = findViewById(R.id.notesContainer)
        searchInput = findViewById(R.id.searchInput)
        val addNoteButton = findViewById<ImageView>(R.id.addNoteButton)

        // Edge-to-edge padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        repository = NoteRepository(this)

        // Floating button: create new note
        addNoteButton.setOnClickListener {
            val intent = Intent(this@NotelistActivity, NoteActivity::class.java)
            noteActivityLauncher.launch(intent)
        }

        val prefs = getSharedPreferences("auth", Context.MODE_PRIVATE)
        val token = prefs.getString("access_token", null)

        if (token.isNullOrBlank()) {
            val errorText = TextView(this)
            errorText.text = "Not logged in"
            notesContainer.addView(errorText)
            return
        }

        // Observe local notes
        repository.getLocalNotesLive().observe(this, Observer { notes ->
            allNotes = notes
            displayNotes(filterNotes(searchInput.text.toString().trim(), allNotes))
        })

        // Real-time search: filter notes as user types
        searchInput.addTextChangedListener { editable ->
            val query = editable.toString().trim()
            displayNotes(filterNotes(query, allNotes))
        }
    }

    // Filter notes by title or description
    private fun filterNotes(query: String, notes: List<LocalNote>): List<LocalNote> {
        if (query.isEmpty()) return notes
        val lowerQuery = query.lowercase()
        return notes.filter { note ->
            note.title.lowercase().contains(lowerQuery) ||
                    note.description.lowercase().contains(lowerQuery)
        }
    }

    private fun displayNotes(notes: List<LocalNote>) {
        notesContainer.removeAllViews()

        if (notes.isEmpty()) {
            // Do not show anything if no notes match the search
            return
        }

        for (note in notes) {
            val btn = Button(this)
            btn.id = note.id
            btn.text = "${note.title}: ${note.description.take(10)}"
            btn.setOnClickListener {
                val intent = Intent(this@NotelistActivity, NoteActivity::class.java)
                intent.putExtra("note_id", note.id)
                intent.putExtra("note_title", note.title)
                intent.putExtra("note_description", note.description)
                noteActivityLauncher.launch(intent)
            }
            notesContainer.addView(btn)
        }
    }

    // Unfocus search bar if user taps outside
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
