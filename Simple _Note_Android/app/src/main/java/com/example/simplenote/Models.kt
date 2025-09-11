package com.example.simplenote.local

import com.google.gson.annotations.SerializedName

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
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String,
    @SerializedName("creator_name") val creatorName: String,
    @SerializedName("creator_username") val creatorUsername: String
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
