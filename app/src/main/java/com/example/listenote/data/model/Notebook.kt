package com.example.listenote.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "notebooks",
    foreignKeys = [
        ForeignKey(
            entity = AudioSource::class,
            parentColumns = ["id"],
            childColumns = ["audio_source_id"],
            onDelete = ForeignKey.CASCADE // AudioSourceが削除されたらNotebookも削除
        )
    ]
)
data class Notebook(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "audio_source_id", index = true)
    val audioSourceId: Long,
    val title: String,
    val createdAt: Long = System.currentTimeMillis()
)