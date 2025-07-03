package com.example.listenote.ui.focus_todo

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.listenote.data.AppDatabase
import com.example.listenote.data.model.Memo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// 画面の状態を管理するデータクラス
data class FocusToDoUiState(
    val tasks: List<Memo> = emptyList(),
    val isLoading: Boolean = true,
    val initialTaskCount: Int = 0,
    val completedTaskCount: Int = 0
)

class FocusToDoViewModel(application: Application, private val notebookId: Long) : ViewModel() {

    private val memoDao = AppDatabase.getDatabase(application).memoDao()

    private val _uiState = MutableStateFlow(FocusToDoUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            // isCompletedがfalseで、toDoが空でないメモを読み込む
            val initialTasks = memoDao.getUncompletedMemosForNotebook(notebookId)
            _uiState.update {
                it.copy(
                    tasks = initialTasks,
                    isLoading = false,
                    initialTaskCount = initialTasks.size
                )
            }
        }
    }

    // 仕様書3.6: [完了]ボタンの機能
    fun onCompleteClick() {
        if (_uiState.value.tasks.isEmpty()) return

        val currentTask = _uiState.value.tasks.first()

        viewModelScope.launch {
            // 1. DBのisCompletedをtrueに更新
            memoDao.update(currentTask.copy(isCompleted = true))

            // 2. Stateからタスクを削除し、完了カウントを増やす
            _uiState.update {
                it.copy(
                    tasks = it.tasks.drop(1), // 先頭のタスクをリストから除く
                    completedTaskCount = it.completedTaskCount + 1
                )
            }
        }
    }

    // 仕様書3.6: [後回し]ボタンの機能
    fun onPostponeClick() {
        if (_uiState.value.tasks.size <= 1) return // タスクが1つ以下なら何もしない

        // Stateのリストの先頭要素を末尾に移動させる
        _uiState.update {
            val updatedTasks = it.tasks.toMutableList()
            val postponedTask = updatedTasks.removeAt(0)
            updatedTasks.add(postponedTask)
            it.copy(tasks = updatedTasks)
        }
    }
}

// ViewModelに引数を渡すためのFactory
class FocusToDoViewModelFactory(private val application: Application, private val notebookId: Long) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FocusToDoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FocusToDoViewModel(application, notebookId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}