package com.example.simplenote.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import com.example.simplenote.local.AppDatabase
import com.example.simplenote.local.LocalNote
import com.example.simplenote.local.NoteRequest
import com.example.simplenote.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class NoteRepository(context: Context) {

    private val noteDao = AppDatabase.getInstance(context).noteDao()

    // LiveData for automatic updates in UI
    fun getLocalNotesLive(): LiveData<List<LocalNote>> = noteDao.getAllNotesLive()

    suspend fun getLocalNotes(): List<LocalNote> = noteDao.getAllNotes()

    suspend fun saveNotesLocally(notes: List<LocalNote>) {
        noteDao.insertNotes(notes)
    }

    // Fetch notes from server and replace local copies (local always wins on conflicts later)
    suspend fun fetchAndCacheNotes(token: String) {
        try {
            val response = RetrofitClient.apiService.getNotes("Bearer $token")
            if (response.isSuccessful) {
                val notes = response.body()?.results?.map { apiNote ->
                    LocalNote(
                        id = apiNote.id, // <-- server id
                        title = apiNote.title,
                        description = apiNote.description,
                        createdAt = apiNote.createdAt,
                        updatedAt = apiNote.updatedAt,
                        creatorName = apiNote.creatorName,
                        creatorUsername = apiNote.creatorUsername,
                        isSynced = true
                    )
                } ?: emptyList()
                noteDao.insertNotes(notes)
            } else {
                Log.e("NoteRepository", "Error fetching notes: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e("NoteRepository", "Exception fetching notes: ${e.message}")
        }
    }

    // Push only unsynced notes to server
    suspend fun syncUnsyncedNotes(token: String) {
        val unsyncedNotes = noteDao.getAllNotes().filter { !it.isSynced }
        for (note in unsyncedNotes) {
            try {
                val request = NoteRequest(
                    title = note.title,
                    description = note.description
                )

                val response = if (note.id == 0) {
                    // New note → POST
                    RetrofitClient.apiService.createNote("Bearer $token", request)
                } else {
                    // Existing note → PUT
                    RetrofitClient.apiService.updateNote("Bearer $token", note.id, request)
                }

                if (response.isSuccessful) {
                    val body = response.body()!!

                    // Update local database with server response
                    val updatedNote = note.copy(
                        id = body.id,
                        title = body.title,
                        description = body.description,
                        createdAt = body.createdAt,
                        updatedAt = body.updatedAt,
                        creatorName = body.creatorName,
                        creatorUsername = body.creatorUsername,
                        isSynced = true
                    )
                    noteDao.insertNote(updatedNote)

                } else {
                    Log.e("NoteRepository", "Sync failed for note ${note.id}: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("NoteRepository", "Exception syncing note ${note.id}: ${e.message}")
            }
        }
    }

    // Start periodic background sync every 20s
    fun startAutoSync(token: String) {
        CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                try {
                    syncUnsyncedNotes(token)
                } catch (e: Exception) {
                    Log.e("NoteRepository", "AutoSync failed: ${e.message}")
                }
                delay(20_000) // 20 seconds
            }
        }
    }
}
