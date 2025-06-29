package com.example.listenote.ui.notebook_list

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
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
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NotebookListScreen(
    modifier: Modifier = Modifier,
    viewModel: NotebookListViewModel = viewModel(),
    navController: NavController,
) {
    val notebooks by viewModel.notebooks.collectAsState()

    LazyColumn(
        modifier = modifier,
    ) {
        items(notebooks) { notebook ->

            Row(modifier = Modifier.clickable {
                navController.navigate("notebook/${notebook.id}")
            }) {

                Text(
                    text = notebook.title + " " +
                            // DBではlong型で扱っているので日付文字列に変換
                            formatTimestampToDateTime(notebook.createdAt)
                )
            }
        }

    }
}

// DateTimeFormatter.ofPattern()がAPI26以降で動かないので@RequiresApi
@RequiresApi(Build.VERSION_CODES.O)
fun formatTimestampToDateTime(timestamp: Long): String {

    val formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")

    val instant = Instant.ofEpochMilli(timestamp)
    val localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())

    return formatter.format(localDateTime)
}