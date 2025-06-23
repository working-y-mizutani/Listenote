package com.example.listenote

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.listenote.player.PlayerUI
import com.example.listenote.ui.memo_edit.NotebookViewModel

@Composable
fun NotebookScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: NotebookViewModel
) {
    val notebook by viewModel.notebook.collectAsState()
    val audioSource by viewModel.audioSource.collectAsState()
    val memos by viewModel.memos.collectAsState()

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.weight(0.7f)
        ) {
            notebook?.let {
                Text(
                    text = "ノート: ${it.title}",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(16.dp)
                )
            }
            audioSource?.let {
                Text(
                    text = "音源: ${it.title}",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            // メモ一覧を表示
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(memos) { memo ->
                    Text(
                        text = memo.impression ?: "感想なし",
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                // 既存のメモをタップしたら編集できるようにする
                                navController.navigate("memo_create_edit/${memo.notebookId}?memoId=${memo.id}")
                            }
                            .padding(16.dp)
                    )
                }
            }

            Button(
                onClick = {
                    notebook?.let {
                        navController.navigate("memo_create_edit/${it.id}")
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
            modifier = Modifier.weight(0.3f)
        )
        // --- ここまで ---
    }
}
