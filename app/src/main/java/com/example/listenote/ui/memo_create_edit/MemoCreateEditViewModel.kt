package com.example.listenote.ui.memo_create_edit

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.listenote.data.AppDatabase
import com.example.listenote.data.model.Memo
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

// 画面の状態を管理するデータクラス
data class MemoEditUiState(
    val timestamp: Long = 0,
    val impression: String = "",
    val toDo: String = "",

    // 編集モードか否か。既存のメモを変種る際は編集モードとなる。
    // 削除ボタンを表示させるかどうかに使用する。
    val isEditing: Boolean = false
)

class MemoCreateEditViewModel(
    application: Application,
    private val notebookId: Long,
    private val memoId: Long,
    private val timestamp: Long,
) : ViewModel() {

    private val memoDao = AppDatabase.getDatabase(application).memoDao()

    var uiState by mutableStateOf(MemoEditUiState())
        private set

    // saveMemo/deleteしたときにnavController.popBackStack()を呼ぶための変数
    // こいつの変更をui側に通知してnavController.popBackStack()を発火させる
    private val _navigateBack = MutableSharedFlow<Unit>()
    val navigateBack = _navigateBack.asSharedFlow()

    init {
        if (memoId != -1L) {
            // 編集モードの場合、既存のメモをDBから読み込む
            viewModelScope.launch {
                val memo = memoDao.getMemoById(memoId)
                if (memo != null) {
                    uiState = uiState.copy(
                        timestamp = memo.timestamp,
                        impression = memo.impression ?: "",
                        toDo = memo.toDo ?: "",
                        isEditing = true
                    )
                }
            }
        }else{
            // 新規作成の場合timestampだけ設定する
            uiState = uiState.copy(timestamp = timestamp)
        }
    }

    // これらをTextFieldのonChangeValueなどに設定して、uiStateの状態を保持する
    // ここ(ViewModel)でビジネスロジックを書いて関心の分離をおこなう
    fun onImpressionChange(text: String) {
        uiState = uiState.copy(impression = text)
    }

    fun onToDoChange(text: String) {
        uiState = uiState.copy(toDo = text)
    }

    fun saveMemo(currentTimestamp: Long) {
        //DB操作なため viewModelScope.launch
        viewModelScope.launch {
            if (uiState.isEditing) {
                // 更新処理
                val updatedMemo = Memo(
                    id = memoId,
                    notebookId = notebookId,
                    timestamp = currentTimestamp,
                    impression = uiState.impression,
                    toDo = uiState.toDo
                    // isCompletedとtoDoPositionは既存の値を引き継ぐ必要があるため、
                    // ここでは省略。完全な実装では読み込んで設定する。
                )
                memoDao.update(updatedMemo)
            } else {
                // 新規作成処理
                val newMemo = Memo(
                    notebookId = notebookId,
                    timestamp = currentTimestamp,
                    impression = uiState.impression,
                    toDo = uiState.toDo
                )
                memoDao.insert(newMemo)
            }
            // 保存が完了したら前の画面に戻るイベントを通知
            // 具体的な処理(戻る処理)はui側で行う。ViewModelの役割ではない。
            _navigateBack.emit(Unit)
        }
    }

    fun deleteMemo() {
        viewModelScope.launch {
            if (uiState.isEditing) {
                memoDao.deleteById(memoId)
                _navigateBack.emit(Unit)
            }
        }
    }
}

// ViewModelに引数(依存関係)を渡すときはFactoryでインスタンス作成が必須
class MemoCreateEditViewModelFactory(
    private val application: Application,
    private val notebookId: Long,
    private val memoId: Long,
    private val timestamp: Long
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MemoCreateEditViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MemoCreateEditViewModel(application, notebookId, memoId, timestamp) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}