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

    // LiveData for UI
    fun getLocalNotesLive(): LiveData<List<LocalNote>> = noteDao.getAllNotesLive()

    suspend fun getLocalNotes(): List<LocalNote> = noteDao.getAllNotes()

    suspend fun saveNotesLocally(notes: List<LocalNote>) {
        noteDao.insertNotes(notes)
    }

    /**
     * Sync unsynced notes to server
     */
    suspend fun syncUnsyncedNotes(token: String) {
        val unsyncedNotes = noteDao.getAllNotes().filter { !it.isSynced }

        for (note in unsyncedNotes) {
            try {
                // Create NoteRequest with only title and description
                val requestBody = NoteRequest(
                    title = note.title,
                    description = note.description
                )

                val response = if (note.idOnServer == null) {
                    // New note → POST
                    RetrofitClient.apiService.createNote("Bearer $token", requestBody)
                } else {
                    // Existing note → PUT
                    RetrofitClient.apiService.updateNote("Bearer $token", note.idOnServer, requestBody)
                }

                if (response.isSuccessful) {
                    val body = response.body()!!

                    // Update local note with server info
                    val updatedNote = note.copy(
                        idOnServer = body.id,
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



    /**
     * Start auto-sync every 20 seconds
     */
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
