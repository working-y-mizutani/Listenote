package com.example.listenote

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.listenote.ui.theme.ListenoteTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ListenoteTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    TopView(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun TopView(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(
            space = 200.dp,
            alignment = Alignment.CenterVertically
        ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = { /* TODO: メモ作成の処理 */ },
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(100.dp)
        ) {
            Text(text = "メモ作成")
        }

        Button(
            onClick = { /* TODO: メモ一覧の処理 */ },
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(100.dp)
        ) {
            Text(text = "メモ一覧")
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ListenoteTheme {
        TopView()
    }
}