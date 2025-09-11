package com.example.simplenote.repository

import android.content.Context
import android.util.Log
import com.example.simplenote.local.AppDatabase
import com.example.simplenote.local.LocalNote
import com.example.simplenote.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NoteRepository(context: Context) {
    private val noteDao = AppDatabase.getInstance(context).noteDao()

    suspend fun getLocalNotes(): List<LocalNote> {
        return noteDao.getAllNotes()
    }

    suspend fun saveNotesLocally(notes: List<LocalNote>) {
        noteDao.insertNotes(notes)
    }

    suspend fun fetchAndCacheNotes(token: String) {
        try {
            val response = RetrofitClient.apiService.getNotes("Bearer $token")
            if (response.isSuccessful) {
                val notes = response.body()?.results?.map { apiNote ->
                    LocalNote(
                        id = apiNote.id,
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
            Log.e("NoteRepository", "Exception: ${e.message}")
        }
    }

    suspend fun syncUnsyncedNotes(token: String) {
        val localNotes = noteDao.getAllNotes().filter { !it.isSynced }
        for (note in localNotes) {
            try {
                // TODO: implement POST/PUT to API
                Log.d("NoteRepository", "Would sync note: ${note.title}")
                noteDao.insertNote(note.copy(isSynced = true))
            } catch (e: Exception) {
                Log.e("NoteRepository", "Sync failed for ${note.id}: ${e.message}")
            }
        }
    }
}
