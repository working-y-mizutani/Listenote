package com.example.listenote.ui.notebook_list

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.listenote.player.AudioPlayerViewModel

@Composable
fun NotebookListScreen(
    modifier: Modifier = Modifier,
    viewModel: NotebookListViewModel = viewModel(),
    navController: NavController,
    audioPlayerViewModel: AudioPlayerViewModel
) {
    val notebooks by viewModel.notebooks.collectAsState()

    LazyColumn(modifier = modifier) {
        items(notebooks) { notebook ->
            Row(modifier = Modifier) {
                Text(text = notebook.title)
                Text(text = notebook.createdAt.toString())
            }
        }

    }
}