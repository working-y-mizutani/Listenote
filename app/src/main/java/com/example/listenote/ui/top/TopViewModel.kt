// app/src/main/java/com/example/listenote/ui/top/TopViewModel.kt
package com.example.listenote.ui.top

import android.app.Application
import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.listenote.data.AppDatabase
import com.example.listenote.data.model.AudioSource
import com.example.listenote.data.model.Notebook
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

class TopViewModel(application: Application) : AndroidViewModel(application) {
    // DAOへの参照を保持
    private val audioSourceDao = AppDatabase.getDatabase(application).audioSourceDao()
    private val notebookDao = AppDatabase.getDatabase(application).notebookDao()

    // 画面遷移のトリガーとなるStateFlow
    private val _createdNotebookId = MutableStateFlow<Long?>(null)
    val createdNotebookId = _createdNotebookId.asStateFlow()

    fun createNotebookFromUri(uri: Uri) {
        viewModelScope.launch {

            val context = getApplication<Application>().applicationContext
            val title = getFileName(uri) ?: "Untitled"
            val duration = getAudioDuration(context, uri) ?: 0L
            val audioSource = AudioSource(
                uri = uri.toString(),
                title = title,
                duration = duration // durationは後で取得・更新する必要がある
            )
            val audioSourceId = audioSourceDao.insert(audioSource)


            val notebookTitle = findUniqueNotebookTitle(audioSourceId, title)
            val notebook = Notebook(
                audioSourceId = audioSourceId,
                title = notebookTitle
            )
            val notebookId = notebookDao.insert(notebook)


            _createdNotebookId.value = notebookId
        }
    }

    private fun getAudioDuration(context: Context, uri: Uri): Long? {
        val retriever = MediaMetadataRetriever()
        return try {
            retriever.setDataSource(context, uri)
            val durationStr =
                retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            retriever.release()
            durationStr?.toLong()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // URIからファイル名を取得するヘルパー関数
    private fun getFileName(uri: Uri): String? {
        return uri.path?.let { File(it).name }
    }

    // 同じ音源からNoteを生成する場合Titleを 音源名_n とするため使用
    private suspend fun findUniqueNotebookTitle(audioSourceId: Long, baseTitle: String): String {
        var newTitle = baseTitle
        var count = 2
        // 同じAudioSourceに紐づく既存のNotebookを取得
        val existingNotebooks = notebookDao.getNotebooksForAudioSource(audioSourceId)
        while (existingNotebooks.any { it.title == newTitle }) {
            newTitle = "${baseTitle}_${count++}"
        }
        return newTitle
    }


    // 画面遷移が完了した後に呼ぶ
    fun onNavigationComplete() {
        _createdNotebookId.value = null
    }
}