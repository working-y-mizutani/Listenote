package com.example.listenote.ui.notebook_list

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.listenote.data.AppDatabase
import com.example.listenote.data.model.Notebook
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NotebookListViewModel(application: Application) : AndroidViewModel(application) {


    private val notebookDao = AppDatabase.getDatabase(application).notebookDao()

    private val _notebooks = MutableStateFlow<List<Notebook>>(emptyList())
    val notebooks = _notebooks.asStateFlow()

    init {
        viewModelScope.launch {
            notebookDao.getAllNotebooks().collect {
                _notebooks.value = it
            }
        }
    }

    fun deleteNotebook(notebook: Notebook) {
        viewModelScope.launch {
            notebookDao.deleteById(notebook.id)
        }
    }

}