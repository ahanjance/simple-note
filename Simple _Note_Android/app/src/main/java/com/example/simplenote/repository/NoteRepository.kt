package com.example.simplenote.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.example.simplenote.local.AppDatabase
import com.example.simplenote.local.LocalNote
import com.example.simplenote.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.util.Log

class NoteRepository(context: Context) {
    private val noteDao = AppDatabase.getInstance(context).noteDao()

    // LiveData for automatic updates
    fun getLocalNotesLive(): LiveData<List<LocalNote>> = noteDao.getAllNotesLive()

    suspend fun getLocalNotes(): List<LocalNote> = noteDao.getAllNotes()

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
}
