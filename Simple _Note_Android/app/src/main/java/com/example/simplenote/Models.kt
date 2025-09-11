package com.example.simplenote.models

// --- Authentication ---

data class LoginRequest(
    val username: String,
    val password: String
)

data class TokenResponse(
    val access: String,
    val refresh: String
)

data class RegisterRequest(
    val username: String,
    val password: String,
    val email: String,
    val first_name: String,
    val last_name: String
)

// --- Notes ---

data class Note(
    val id: Int,
    val title: String,
    val description: String,
    val created_at: String,
    val updated_at: String,
    val creator_name: String,
    val creator_username: String
)

data class NotesResponse(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<Note>
)


data class NoteRequest(
    val title: String,
    val description: String
)
