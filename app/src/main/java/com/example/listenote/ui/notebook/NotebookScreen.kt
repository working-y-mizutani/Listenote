package com.example.listenote.ui.notebook

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.listenote.player.AudioPlayerViewModel
import com.example.listenote.player.PlayerUI
import com.example.listenote.ui.util.formatDuration

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

    val playbackError by audioPlayerViewModel.playbackError.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(playbackError) {
        playbackError?.let { message ->
            snackbarHostState.showSnackbar(message)
            // 表示後にViewModelのエラー状態をクリアする
            audioPlayerViewModel.onErrorMessageShown()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    notebook?.let {
                        Text(
                            it.title,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        audioPlayerViewModel.playPause()
                        navController.popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "戻る"
                        )
                    }
                },
            )
        },
        bottomBar = {
            Surface(
                // Surfaceに影をつけて、コンテンツとの境界を明確にする
                shadowElevation = 8.dp
            ) {
                Column {
                    Button(
                        onClick = {
                            audioPlayerViewModel.pause()
                            notebook?.let { navController.navigate("todo_list/${it.id}") }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp),
                        shape = RectangleShape
                    ) {
                        Text("TODOモードへ")
                    }

                    PlayerUI(
                        audioUri = audioSource?.uri,
                        viewModel = audioPlayerViewModel
                    )
                }

            }
        },

        floatingActionButton = {
            FloatingActionButton(onClick = {
                audioPlayerViewModel.pause()
                notebook?.let {
                    navController.navigate("memo_create_edit/${it.id}?timestamp=${currentPosition}")
                }
            }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "停止してメモを取る"
                )
            }
        }

    ) { innerPadding ->

        if (memos.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "メモがありません。+ ボタンからメモを作成できます。",
                    modifier = Modifier
                        .padding(8.dp),
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            // メモ一覧を表示
            LazyColumn(modifier = Modifier.padding(innerPadding)) {
                items(memos) { memo ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                // 既存のメモをタップしたら編集できるようにする
                                navController.navigate(
                                    "memo_create_edit/${memo.notebookId}" +
                                            "?memoId=${memo.id}&timestamp=${memo.timestamp}"
                                )
                            }
                            .padding(8.dp),
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                        ) {
                            Text(text = formatDuration(memo.timestamp))
                            Row {
                                Text(
                                    text = if (memo.impression.isNullOrEmpty()) {
                                        "感想なし"
                                    } else {
                                        memo.impression
                                    },
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f),
                                )
                                Text(
                                    text = if (memo.toDo.isNullOrEmpty()) {
                                        "ToDo無し"
                                    } else {
                                        memo.toDo
                                    },
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f),
                                )
                            }

                        }
                    }

                }
            }
        }


    }


}
