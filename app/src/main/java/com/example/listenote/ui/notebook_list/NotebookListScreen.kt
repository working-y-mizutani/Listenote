package com.example.listenote.ui.notebook_list

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
        modifier = modifier.padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(notebooks) { notebook ->

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { navController.navigate("notebook/${notebook.id}") },
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                ) {
                    Text(text = notebook.title)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = formatTimestampToDateTime(notebook.createdAt),
                        modifier = Modifier.align(Alignment.End)
                    )
                }
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