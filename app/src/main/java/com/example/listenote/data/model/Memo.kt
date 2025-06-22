package com.example.listenote.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "memos",
    foreignKeys = [
        ForeignKey(
            entity = Notebook::class,
            parentColumns = ["id"],
            childColumns = ["notebook_id"],
            onDelete = ForeignKey.CASCADE // Notebookが削除されたらMemoも削除
        )
    ]
)
data class Memo(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "notebook_id", index = true)
    val notebookId: Long,
    val timestamp: Long,
    val impression: String?,
    @ColumnInfo(name = "to_do")
    val toDo: String?,
    @ColumnInfo(name = "is_completed", defaultValue = "false")
    val isCompleted: Boolean = false,
    @ColumnInfo(name = "to_do_position")
    val toDoPosition: Int = 0
)