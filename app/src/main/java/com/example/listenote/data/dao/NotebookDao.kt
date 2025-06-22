package com.example.listenote.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.listenote.data.model.Notebook
import kotlinx.coroutines.flow.Flow

@Dao
interface NotebookDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(notebook: Notebook): Long

    @Update
    suspend fun update(notebook: Notebook)

    @Query("SELECT * FROM notebooks")
    fun getAllNotebooks(): Flow<List<Notebook>>

    @Query("SELECT * FROM notebooks WHERE id = :notebookId")
    suspend fun getNotebookById(notebookId: Long): Notebook?

    @Query("DELETE FROM notebooks WHERE id = :notebookId")
    suspend fun deleteById(notebookId: Long)

    @Query("SELECT * FROM notebooks WHERE audio_source_id = :audioSourceId")
    suspend fun getNotebooksForAudioSource(audioSourceId: Long): List<Notebook>




}