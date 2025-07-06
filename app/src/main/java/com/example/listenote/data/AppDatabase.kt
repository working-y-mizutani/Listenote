package com.example.listenote.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.listenote.data.dao.AudioSourceDao
import com.example.listenote.data.dao.MemoDao
import com.example.listenote.data.dao.NotebookDao
import com.example.listenote.data.model.AudioSource
import com.example.listenote.data.model.Memo
import com.example.listenote.data.model.Notebook

@Database(
    entities = [AudioSource::class, Notebook::class, Memo::class],
    version = 2,
    exportSchema = false
)
// Daoの保持と、初回のDB作成

abstract class AppDatabase : RoomDatabase() {

    abstract fun memoDao(): MemoDao
    abstract fun notebookDao(): NotebookDao
    abstract fun audioSourceDao(): AudioSourceDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "listenote_database"
                )
                    .fallbackToDestructiveMigration(false).build()
                INSTANCE = instance
                instance
            }
        }
    }
}