package com.example.listenote.ui.notebook

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    notebook?.let {
                        Text(
                            it.title,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            fontSize = 16.sp,
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "戻る"
                        )
                    }
                },
                actions = {
                    TextButton(onClick = {
                        if (audioPlayerViewModel.isPlaying.value) {
                            audioPlayerViewModel.playPause()
                        }
                        notebook?.let { navController.navigate("todo_list/${it.id}") }
                    }) {
                        Spacer(modifier = Modifier.width(4.dp)) // アイコンとテキストの間に少し余白
                        Text(
                            "ToDo\nモード",
                            fontSize = 18.sp
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null // ボタン全体の意味は伝わるのでnullでOK
                        )
                    }
                }

            )
        },
        bottomBar = {
            Surface(
                // Surfaceに影をつけて、コンテンツとの境界を明確にする
                shadowElevation = 8.dp
            ) {
                PlayerUI(
                    audioUri = audioSource?.uri,
                    viewModel = audioPlayerViewModel
                )
            }
        },

        floatingActionButton = {
            FloatingActionButton(onClick = {
                if (audioPlayerViewModel.isPlaying.value) {
                    audioPlayerViewModel.playPause()
                }
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // メモ一覧を表示
            LazyColumn(modifier = Modifier.weight(0.8f)) {
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
