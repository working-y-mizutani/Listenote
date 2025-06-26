package com.example.listenote.ui.notebook

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.listenote.player.AudioPlayerViewModel
import com.example.listenote.player.PlayerUI

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotebookScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: NotebookViewModel,
    audioPlayerViewModel: AudioPlayerViewModel
) {
    val notebook by viewModel.notebook.collectAsState()
    val audioSource by viewModel.audioSource.collectAsState()
    val memos by viewModel.memos.collectAsState()
    val currentPosition by audioPlayerViewModel.currentPosition.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { notebook?.let { Text(it.title) } },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "戻る"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier.weight(0.7f)
            ) {

                // メモ一覧を表示
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(memos) { memo ->
                        Text(
                            text = memo.impression ?: "感想なし",
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    // 既存のメモをタップしたら編集できるようにする
                                    navController.navigate(
                                        "memo_create_edit/${memo.notebookId}" +
                                                "?memoId=${memo.id}&timestamp=${memo.timestamp}"
                                    )
                                }
                                .padding(16.dp)
                        )
                    }
                }

                Button(
                    onClick = {
                        notebook?.let {
                            navController.navigate("memo_create_edit/${it.id}?timestamp=${currentPosition}")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                ) {
                    Text("停止してメモを取る")
                }
            }

            // プレーヤーにAudioSourceのURIを渡す
            PlayerUI(
                audioUri = audioSource?.uri,
                modifier = Modifier.weight(0.3f),
                viewModel = audioPlayerViewModel
            )
        }
    }


}
