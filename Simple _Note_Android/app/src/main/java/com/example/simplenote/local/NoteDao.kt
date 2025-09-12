package com.example.simplenote.local

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes ORDER BY updatedAt DESC")
    fun getAllNotesLive(): LiveData<List<LocalNote>>

    @Query("SELECT * FROM notes")
    suspend fun getAllNotes(): List<LocalNote>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotes(notes: List<LocalNote>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: LocalNote)

    @Delete
    suspend fun deleteNote(note: LocalNote)

    @Query("DELETE FROM notes")
    suspend fun clearAll()

    // Delete a note by its ID
    @Query("DELETE FROM notes WHERE id = :noteId")
    suspend fun deleteNoteById(noteId: Int)
}
