package com.example.simplenote.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class LocalNote(
    @PrimaryKey val id: Int,
    val title: String,
    val description: String,
    val createdAt: String,
    val updatedAt: String,
    val creatorName: String,
    val creatorUsername: String,
    val isSynced: Boolean = false,
    val idOnServer: Int? = null  // null if not yet on server
)

