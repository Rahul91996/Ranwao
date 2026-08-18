package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: String = "usr_9981",
    val title: String,
    val mediaType: String, // "PHOTO" or "VIDEO"
    val originalPath: String,
    val editedPath: String,
    val backgroundName: String,
    val timestamp: Long = System.currentTimeMillis(),
    val durationSeconds: Int = 0,
    val width: Int = 1080,
    val height: Int = 1080
)
