package com.example.simplenote

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.widget.addTextChangedListener
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import com.example.simplenote.local.LocalNote
import com.example.simplenote.repository.NoteRepository

class NotelistActivity : AppCompatActivity() {

    private lateinit var repository: NoteRepository
    private lateinit var notesContainer: LinearLayout
    private lateinit var searchInput: EditText
    private var allNotes: List<LocalNote> = emptyList() // Keep full list for filtering

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_notelist)

        notesContainer = findViewById(R.id.notesContainer)
        val addNoteButton = findViewById<ImageView>(R.id.addNoteButton)
        searchInput = findViewById(R.id.searchInput)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        repository = NoteRepository(this)

        // Floating button: create new note
        addNoteButton.setOnClickListener {
            val intent = Intent(this@NotelistActivity, NoteActivity::class.java)
            startActivity(intent)
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
            displayNotes(allNotes)
        })

        // Real-time search: filter notes as user types
        searchInput.addTextChangedListener { editable ->
            val query = editable.toString().trim().lowercase()
            val filtered = allNotes.filter { note ->
                note.title.lowercase().contains(query) || note.description.lowercase().contains(query)
            }
            displayNotes(filtered)
        }
    }

    private fun displayNotes(notes: List<LocalNote>) {
        notesContainer.removeAllViews()

        if (notes.isEmpty()) {
            val illustration = ImageView(this)
            illustration.setImageResource(R.drawable.illustration2)
            notesContainer.addView(illustration)

            val text1 = TextView(this)
            text1.text = "No notes yet!"
            text1.textSize = 18f
            notesContainer.addView(text1)

            val text2 = TextView(this)
            text2.text = "Tap the + button to add your first note."
            text2.textSize = 14f
            notesContainer.addView(text2)
        } else {
            for (note in notes) {
                val btn = Button(this)
                btn.id = note.id
                btn.text = "${note.title}: ${note.description.take(10)}"
                btn.setOnClickListener {
                    val intent = Intent(this@NotelistActivity, NoteActivity::class.java)
                    intent.putExtra("note_id", note.id)
                    intent.putExtra("note_title", note.title)
                    intent.putExtra("note_description", note.description)
                    startActivity(intent)
                }
                notesContainer.addView(btn)
            }
        }
    }
}
