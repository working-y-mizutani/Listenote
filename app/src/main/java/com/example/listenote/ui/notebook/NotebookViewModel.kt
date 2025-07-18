package com.example.listenote.ui.notebook

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.listenote.data.AppDatabase
import com.example.listenote.data.model.AudioSource
import com.example.listenote.data.model.Memo
import com.example.listenote.data.model.Notebook
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NotebookViewModel(application: Application, private val notebookId: Long) :
    AndroidViewModel(application) {

    private val notebookDao = AppDatabase.getDatabase(application).notebookDao()
    private val audioSourceDao = AppDatabase.getDatabase(application).audioSourceDao()
    private val memoDao = AppDatabase.getDatabase(application).memoDao()

    private val _notebook = MutableStateFlow<Notebook?>(null)
    val notebook = _notebook.asStateFlow()

    private val _audioSource = MutableStateFlow<AudioSource?>(null)
    val audioSource = _audioSource.asStateFlow()

    private val _memos = MutableStateFlow<List<Memo>>(emptyList())
    val memos = _memos.asStateFlow()

    init {
        viewModelScope.launch {
            // Notebookの情報を取得
            val currentNotebook = notebookDao.getNotebookById(notebookId)
            _notebook.value = currentNotebook

            // AudioSourceの情報を取得
            currentNotebook?.let {
                _audioSource.value = audioSourceDao.getAudioSourceById(it.audioSourceId)
            }

            // メモ一覧をcollectで監視
            // メモ作成/編集画面から popBackStack()で戻っても自動で最新のメモを読み込める
            memoDao.getMemosForNotebook(notebookId).collect { memoList ->
                _memos.value = memoList
            }
        }
    }
}

// ViewModelに引数(notebookId)を渡すためのFactory
class NotebookViewModelFactory(private val application: Application, private val notebookId: Long) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotebookViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NotebookViewModel(application, notebookId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}