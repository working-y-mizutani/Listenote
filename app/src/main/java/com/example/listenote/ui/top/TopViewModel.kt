// app/src/main/java/com/example/listenote/ui/top/TopViewModel.kt
package com.example.listenote.ui.top

import android.app.Application
import android.content.Context
import android.database.Cursor
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.OpenableColumns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.listenote.data.AppDatabase
import com.example.listenote.data.model.AudioSource
import com.example.listenote.data.model.Notebook
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class TopViewModel(application: Application) : AndroidViewModel(application) {

    private val audioSourceDao = AppDatabase.getDatabase(application).audioSourceDao()
    private val notebookDao = AppDatabase.getDatabase(application).notebookDao()

    private val _createdNotebookId = MutableStateFlow<Long?>(null)

    // 画面遷移のトリガー役。LaunchedEffectの引数
    val createdNotebookId = _createdNotebookId.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun createNotebookFromUri(uri: Uri) {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                val context = getApplication<Application>().applicationContext
                val title = getFileName(uri) ?: "Untitled"
                val duration = getAudioDuration(context, uri) ?: 0L
                val uriString = uri.toString()

                var existingAudioSource = audioSourceDao.getAudioSourceByUri(uriString)
                val audioSourceId: Long
                if (existingAudioSource == null) {
                    // 2. なければ、新しいAudioSourceを作成してDBに保存
                    val duration = getAudioDuration(context, uri) ?: 0L
                    val newAudioSource = AudioSource(
                        uri = uriString,
                        title = title,
                        duration = duration
                    )
                    audioSourceId = audioSourceDao.insert(newAudioSource)
                } else {
                    // 3. あれば、そのIDをそのまま使う
                    audioSourceId = existingAudioSource.id
                }
                val notebookTitle = findUniqueNotebookTitle(audioSourceId, title)
                val notebook = Notebook(
                    audioSourceId = audioSourceId,
                    title = notebookTitle
                )
                val notebookId = notebookDao.insert(notebook)

                //こいつの変更をトリガーにNotebookScreenに遷移
                _createdNotebookId.value = notebookId
            } finally {

                _isLoading.value = false
            }


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
        // AndroidViewModelからアプリケーションのContextを取得
        val context = getApplication<Application>().applicationContext
        var fileName: String? = null

        // ContentResolverを使って、URIからファイル情報を問い合わせ
        val cursor: Cursor? = context.contentResolver.query(
            uri, null, null, null, null, null
        )

        // cursorを使って、DISPLAY_NAME (表示名) カラムからファイル名を取得
        cursor?.use {
            if (it.moveToFirst()) {
                val displayNameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (displayNameIndex != -1) {
                    fileName = it.getString(displayNameIndex)
                }
            }
        }

        return fileName
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