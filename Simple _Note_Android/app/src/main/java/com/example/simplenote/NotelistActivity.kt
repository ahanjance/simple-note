package com.example.simplenote

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import com.example.simplenote.local.LocalNote
import com.example.simplenote.repository.NoteRepository

class NotelistActivity : AppCompatActivity() {
    private lateinit var repository: NoteRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_notelist)

        val notesContainer = findViewById<LinearLayout>(R.id.notesContainer)
        val addNoteButton = findViewById<ImageView>(R.id.addNoteButton)

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
            renderNotes(notes, notesContainer)
        })
    }

    private fun renderNotes(notes: List<LocalNote>, container: LinearLayout) {
        container.removeAllViews()
        if (notes.isEmpty()) {
            val illustration = ImageView(this)
            illustration.setImageResource(R.drawable.illustration2)
            container.addView(illustration)

            val text1 = TextView(this)
            text1.text = "No notes yet!"
            text1.textSize = 18f
            container.addView(text1)

            val text2 = TextView(this)
            text2.text = "Tap the + button to add your first note."
            text2.textSize = 14f
            container.addView(text2)
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
                container.addView(btn)
            }
        }
    }
}
