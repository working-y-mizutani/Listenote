package com.example.listenote.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "audio_sources")
data class AudioSource(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val uri: String,
    val title: String,
    val duration: Long,
    val createdAt: Long = System.currentTimeMillis()
)