package com.example.simplenote

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject




class NotelistActivity : AppCompatActivity() {

    private lateinit var repository: NoteRepository
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchInput: EditText
    private lateinit var addNoteButton: ImageView
    private lateinit var settingsButton: ImageView
    private lateinit var adapter: NoteAdapter
    private var allNotes: MutableList<LocalNote> = mutableListOf()
    private val ITEM_HEIGHT_RATIO = 0.3f

    private val PAGE_SIZE = 20
    private var isLoading = false
    private var allLoaded = false
    private var currentOffset = 0

    private lateinit var username: String

    private val noteActivityLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val saved = result.data?.getBooleanExtra("note_saved", false) ?: false
                if (saved) {
                    searchInput.setText("")
                }
            }
        }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_notelist)

        recyclerView = findViewById(R.id.recyclerViewNotes)
        searchInput = findViewById(R.id.searchInput)
        addNoteButton = findViewById(R.id.addNoteButton)
        settingsButton = findViewById(R.id.settingsButton) // ✅ grab settings button

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

        recyclerView.post {
            val h = recyclerView.height
            if (h > 0) {
                adapter.setItemHeight((h * ITEM_HEIGHT_RATIO).toInt())
            }
        }

        // Add-note button
        addNoteButton.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> v.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100).start()
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

        // ✅ Settings button → open SettingsActivity
        settingsButton.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        val prefs = getSharedPreferences("auth", Context.MODE_PRIVATE)
        username = prefs.getString("username", "me") ?: "me"
        val token = prefs.getString("access_token", null)

        if (!token.isNullOrBlank()) {
            fetchAndSaveUserInfo(token)
        }


        // Observe local notes via LiveData filtered by logged-in user
        repository.getLocalNotesLive().observe(this, Observer { notes ->
            allNotes = notes.filter { it.creatorUsername == username }.toMutableList()
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
        return notes.filter { it.title.lowercase().contains(lower) || it.description.lowercase().contains(lower) }
    }

    // Hide keyboard on outside touch
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

    private fun loadNextPage() {
        val prefs = getSharedPreferences("auth", Context.MODE_PRIVATE)
        val currentUser = prefs.getString("username", "me") ?: "me"

        isLoading = true
        CoroutineScope(Dispatchers.IO).launch {
            val notesPage = repository.getLocalNotesPaginated(PAGE_SIZE, currentOffset)
                .filter { it.creatorUsername == currentUser }

            if (notesPage.isEmpty()) allLoaded = true
            currentOffset += notesPage.size

            withContext(Dispatchers.Main) {
                allNotes.addAll(notesPage)
                adapter.updateNotes(filterNotes(searchInput.text.toString().trim(), allNotes))
                isLoading = false
            }
        }
    }

    private fun fetchAndSaveUserInfo(token: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val client = OkHttpClient()
                val request = Request.Builder()
                    .url("https://simple.darkube.app/api/auth/userinfo/")
                    .addHeader("Authorization", "Bearer $token")
                    .build()

                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val body = response.body?.string()
                    if (!body.isNullOrEmpty()) {
                        val json = JSONObject(body)
                        val username = json.getString("username")
                        val email = json.getString("email")

                        // Save to SharedPreferences
                        val prefs = getSharedPreferences("auth", Context.MODE_PRIVATE)
                        prefs.edit()
                            .putString("username", username)
                            .putString("email", email)
                            .apply()

                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@NotelistActivity,
                            "Failed to fetch user info",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@NotelistActivity,
                        "Error: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

}
