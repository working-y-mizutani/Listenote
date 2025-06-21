package com.example.listenote

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.listenote.player.PlayerUI

@Composable
fun MemoEditScreen(modifier: Modifier = Modifier) {

    Column(
        modifier = modifier.fillMaxSize()
    ) {

        Column(
            modifier = Modifier.weight(0.7f)
        ) {

            Column(
                modifier = Modifier
                    .weight(0.7f)
                    .background(Color.Cyan) // 可視化のための背景色
            ) {
                Text(
                    text = "このノートに対するメモ一覧フィールド",
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.7f)

                )

                Button(
                    onClick = { /* TODO: メモ作成の処理をここに記述 */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.3f)
                        .padding(horizontal = 40.dp, vertical = 40.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Yellow,
                        contentColor = Color.Black
                    )
                ) {
                    Text("ToDoモードへ")
                }

            }

            Button(
                onClick = { /* TODO: メモ作成の処理をここに記述 */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.3f) // 可変領域の2割を占める
                    .padding(horizontal = 32.dp, vertical = 32.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Yellow,
                    contentColor = Color.Black
                )
            ) {
                Text("メモを作成")
            }
        }

        PlayerUI(
            audioResourceId = R.raw.test_my_song,
            Modifier
                .weight(0.3f)
                .background(Color.Green)
        )
    }
}

@Composable
fun MemoDetailScreen(modifier: Modifier = Modifier) {

    var impression by remember { mutableStateOf("") }
    var toDo by remember { mutableStateOf("") }

    Column(
        modifier = modifier.fillMaxSize()
    ) {

        Column(
            modifier = Modifier
                .weight(0.7f)
                .padding(24.dp)

        ) {

            val labelWidth = 72.dp
            val spaceBetween = 12.dp

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "時間",
                    modifier = Modifier.width(labelWidth),
                )
                Text(text = "00:00")
            }

            Spacer(modifier = Modifier.height(spaceBetween))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = "感想",
                    modifier = Modifier.width(labelWidth),
                )
                TextField(
                    value = impression,
                    onValueChange = { newText ->
                        impression = newText
                    },
                    modifier = Modifier.fillMaxSize(),

                    )
            }

            Spacer(modifier = Modifier.height(spaceBetween))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = "ToDo",
                    modifier = Modifier.width(labelWidth),
                )
                TextField(
                    value = toDo,
                    onValueChange = { newText ->
                        toDo = newText
                    },
                    modifier = Modifier.fillMaxSize(),
                )
            }


        }

        PlayerUI(
            audioResourceId = R.raw.test_my_song,
            Modifier
                .weight(0.3f)
                .background(Color.Green)
        )
    }
}

