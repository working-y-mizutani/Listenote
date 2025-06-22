package com.example.listenote

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
import com.example.listenote.player.PlayerUI
import com.example.listenote.ui.memo_edit.MemoEditViewModel

@Composable
fun MemoEditScreen(
    modifier: Modifier = Modifier,
    viewModel: MemoEditViewModel
) {
    // ViewModelからStateを収集
    val notebook by viewModel.notebook.collectAsState()
    val audioSource by viewModel.audioSource.collectAsState()
    val memos by viewModel.memos.collectAsState()

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // --- ここからが主要な変更箇所 ---
        Column(
            modifier = Modifier.weight(0.7f)
        ) {
            // とりあえずタイトルを表示
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
                    Text(text = memo.impression ?: "感想なし", modifier = Modifier.padding(16.dp))
                }
            }

            Button(
                onClick = { /* TODO: メモ作成の処理 */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            ) {
                Text("メモを作成")
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
