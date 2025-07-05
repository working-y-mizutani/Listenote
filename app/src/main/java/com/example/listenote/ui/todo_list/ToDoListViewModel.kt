package com.example.listenote.ui.todo_list

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.listenote.data.AppDatabase
import com.example.listenote.data.model.Memo
import com.example.listenote.data.model.Notebook
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ToDoListViewModel(application: Application, private val notebookId: Long) :
    AndroidViewModel(application) {

    private val notebookDao = AppDatabase.getDatabase(application).notebookDao()
    private val memoDao = AppDatabase.getDatabase(application).memoDao()

    private val _notebook = MutableStateFlow<Notebook?>(null)
    val notebook = _notebook.asStateFlow()


    private val _memos = MutableStateFlow<List<Memo>>(emptyList())
    val memos = _memos.asStateFlow()

    init {
        viewModelScope.launch {
            // Notebookの情報を取得
            val currentNotebook = notebookDao.getNotebookById(notebookId)
            _notebook.value = currentNotebook


            // メモ一覧を監視
            memoDao.getMemosForNotebook(notebookId).collect { memoList ->
                _memos.value = memoList
            }
        }
    }

    // List内でMemoを移動させる
    fun moveItem(fromIndex: Int, toIndex: Int) {
        val updatedList = _memos.value.toMutableList()
        val movedItem = updatedList.removeAt(fromIndex)
        updatedList.add(toIndex, movedItem)
        _memos.value = updatedList
    }

    // 現在のリストの順序をDBに保存する
    fun saveOrder() {
        viewModelScope.launch {
            _memos.value.forEachIndexed { index, memo ->
                // 新しいtoDoPositionで更新
                if (memo.toDoPosition != index) {
                    memoDao.update(memo.copy(toDoPosition = index))
                }
            }
        }
    }

    fun updateCompletion(memoId: Long, isCompleted: Boolean) {
        viewModelScope.launch {
            val memo = _memos.value.find { it.id == memoId }
            memo?.let {
                memoDao.update(it.copy(isCompleted = isCompleted))
            }
        }
    }

    //Test用
    fun updateAllComplete() {
        viewModelScope.launch {
            _memos.value.forEach { memo ->
                if (!memo.isCompleted) {
                    memoDao.update(memo.copy(isCompleted = true))
                }
            }
        }
    }

    //Test用
    fun updateAllIncomplete() {
        viewModelScope.launch {
            _memos.value.forEach { memo ->
                if (memo.isCompleted) {
                    memoDao.update(memo.copy(isCompleted = false))
                }
            }
        }
    }
}

class ToDoListViewModelFactory(
    private val application: Application,
    private val notebookId: Long
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ToDoListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ToDoListViewModel(application, notebookId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}