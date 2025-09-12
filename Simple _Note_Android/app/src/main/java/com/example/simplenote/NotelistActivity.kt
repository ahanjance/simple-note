package com.example.simplenote

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.simplenote.adapter.NoteAdapter
import com.example.simplenote.local.LocalNote
import com.example.simplenote.repository.NoteRepository

class NotelistActivity : AppCompatActivity() {

    private lateinit var repository: NoteRepository
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchInput: EditText
    private lateinit var addNoteButton: ImageView
    private lateinit var adapter: NoteAdapter
    private var allNotes: List<LocalNote> = emptyList()
    private val ITEM_HEIGHT_RATIO = 0.3f

    // Launcher for NoteActivity
    private val noteActivityLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val saved = result.data?.getBooleanExtra("note_saved", false) ?: false
                if (saved) {
                    searchInput.setText("")
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_notelist)

        recyclerView = findViewById(R.id.recyclerViewNotes)
        searchInput = findViewById(R.id.searchInput)
        addNoteButton = findViewById(R.id.addNoteButton)

        // Edge-to-edge padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(android.view.WindowInsets.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        repository = NoteRepository(this)

        // RecyclerView setup
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        adapter = NoteAdapter(emptyList()) { note ->
            val intent = Intent(this, NoteActivity::class.java).apply {
                putExtra("note_id", note.id)
                putExtra("note_title", note.title)
                putExtra("note_description", note.description)
            }
            noteActivityLauncher.launch(intent)
        }
        recyclerView.adapter = adapter

        // After layout measured, compute item height and send to adapter
        recyclerView.post {
            val h = recyclerView.height
            if (h > 0) {
                adapter.setItemHeight((h * ITEM_HEIGHT_RATIO).toInt())
            }
        }

        // Add-note button with scale animation
        addNoteButton.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    v.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100).start()
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    v.animate().scaleX(1f).scaleY(1f).setDuration(100).start()
                    if (event.action == MotionEvent.ACTION_UP) {
                        val intent = Intent(this, NoteActivity::class.java)
                        noteActivityLauncher.launch(intent)
                    }
                }
            }
            true
        }

        val prefs = getSharedPreferences("auth", Context.MODE_PRIVATE)
        val token = prefs.getString("access_token", null)

        if (token.isNullOrBlank()) {
            // Not logged in
            return
        }

        // Observe local notes
        repository.getLocalNotesLive().observe(this, Observer { notes ->
            allNotes = notes
            adapter.updateNotes(filterNotes(searchInput.text.toString().trim(), allNotes))
        })

        // Real-time search
        searchInput.addTextChangedListener { editable ->
            val query = editable?.toString()?.trim() ?: ""
            adapter.updateNotes(filterNotes(query, allNotes))
        }
    }

    private fun filterNotes(query: String, notes: List<LocalNote>): List<LocalNote> {
        if (query.isEmpty()) return notes
        val lower = query.lowercase()
        return notes.filter {
            it.title.lowercase().contains(lower) || it.description.lowercase().contains(lower)
        }
    }

    // Unfocus search bar when tapping outside
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
