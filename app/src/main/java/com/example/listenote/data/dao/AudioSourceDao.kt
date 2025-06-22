package com.example.listenote.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.listenote.data.model.AudioSource
import kotlinx.coroutines.flow.Flow

@Dao
interface AudioSourceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(audioSource: AudioSource): Long

    @Update
    suspend fun update(audioSource: AudioSource)

    @Query("SELECT * FROM audio_sources")
    fun getAllAudioSources(): Flow<List<AudioSource>>

    @Query("SELECT * FROM audio_sources WHERE id = :audioSourceId")
    suspend fun getAudioSourceById(audioSourceId: Long): AudioSource?

    @Query("DELETE FROM audio_sources WHERE id = :audioSourceId")
    suspend fun deleteById(audioSourceId: Long)

}