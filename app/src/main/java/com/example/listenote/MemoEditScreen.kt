package com.example.listenote

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
            modifier = Modifier.weight(1f)
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
                .weight(0.4f)
                .background(Color.Green)
        )
    }


}
