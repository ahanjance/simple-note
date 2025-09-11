package com.example.simplenote.network

import com.example.simplenote.models.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // --- Authentication ---
    @POST("api/auth/token/")
    suspend fun login(@Body request: LoginRequest): Response<TokenResponse>

    @POST("api/auth/register/")
    suspend fun register(@Body request: RegisterRequest): Response<TokenResponse>

    // --- Notes ---
    @GET("api/notes/")
    suspend fun getNotes(
        @Header("Authorization") token: String
    ): Response<NotesResponse>

    @POST("api/notes/")
    suspend fun createNote(
        @Header("Authorization") token: String,
        @Body request: NoteRequest
    ): Response<Note>

    @PUT("api/notes/{id}/")
    suspend fun updateNote(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body request: NoteRequest
    ): Response<Note>

    @DELETE("api/notes/{id}/")
    suspend fun deleteNote(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<Unit>


}
