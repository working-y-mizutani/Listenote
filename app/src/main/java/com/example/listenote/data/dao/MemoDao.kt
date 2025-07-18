package com.example.listenote.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.listenote.data.model.Memo
import kotlinx.coroutines.flow.Flow

@Dao
interface MemoDao {
    @Insert
    suspend fun insert(memo: Memo)

    @Update
    suspend fun update(memo: Memo)

    @Query("SELECT * FROM memos WHERE notebook_id = :notebookId ORDER BY to_do_position ASC")
    fun getMemosForNotebook(notebookId: Long): Flow<List<Memo>>

    @Query("SELECT * FROM memos WHERE id = :memoId")
    suspend fun getMemoById(memoId: Long): Memo?

    @Query("DELETE FROM memos WHERE id = :memoId")
    suspend fun deleteById(memoId: Long)

    @Query("SELECT MAX(to_do_position) FROM memos WHERE notebook_id = :notebookId")
    suspend fun getMaxToDoPosition(notebookId: Long): Int?

    @Query("SELECT * FROM memos WHERE notebook_id = :notebookId AND is_completed = 0 ORDER BY to_do_position ASC")
    suspend fun getUncompletedMemosForNotebook(notebookId: Long): List<Memo>


}