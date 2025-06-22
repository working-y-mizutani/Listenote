package com.example.listenote.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.listenote.data.model.Notebook
import kotlinx.coroutines.flow.Flow

@Dao
interface NotebookDao {
    @Insert
    suspend fun insert(notebook: Notebook)

    @Update
    suspend fun update(notebook: Notebook)

    @Query("SELECT * FROM notebooks")
    fun getAllNotebooks(): Flow<List<Notebook>>

    @Query("SELECT * FROM notebooks WHERE id = :notebookId")
    suspend fun getNotebookById(notebookId: Long): Notebook?

    @Query("DELETE FROM notebooks WHERE id = :notebookId")
    suspend fun deleteById(notebookId: Long)


}