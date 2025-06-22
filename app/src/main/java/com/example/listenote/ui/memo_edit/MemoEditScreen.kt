package com.example.listenote.ui.memo_edit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.listenote.player.PlayerUI

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemoDetailScreen(modifier: Modifier = Modifier) {

    var impression by remember { mutableStateOf("") }
    var toDo by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("メモの詳細") },
                navigationIcon = {
                    IconButton(onClick = { /* TODO: 戻る処理 */ }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "戻る"
                        )
                    }
                },
                actions = {
                    // 保存ボタン
                    IconButton(onClick = { /* TODO: 保存処理 */ }) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "保存"
                        )
                    }
                    // 削除ボタン
                    IconButton(onClick = { /* TODO: 削除処理 */ }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "削除"
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
                modifier = Modifier
                    .weight(0.7f)
                    .padding(horizontal = 8.dp)
                    .padding(bottom = 16.dp)

            ) {
                val labelWidth = 68.dp
                val spaceBetween = 12.dp

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "時間", modifier = Modifier.width(labelWidth))
                    Text(text = "00:00")
                }
                Spacer(modifier = Modifier.height(spaceBetween))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(text = "感想", modifier = Modifier.width(labelWidth))
                    TextField(
                        value = impression,
                        onValueChange = { impression = it },
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Spacer(modifier = Modifier.height(spaceBetween))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(text = "ToDo", modifier = Modifier.width(labelWidth))
                    TextField(
                        value = toDo,
                        onValueChange = { toDo = it },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

//            PlayerUI(
//                audioResourceId = R.raw.test_my_song,
//                Modifier
//                    .weight(0.3f)
//                    .background(Color.Green)
//            )
        }
    }
}

