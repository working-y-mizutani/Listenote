package com.example.listenote.ui.focus_todo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.listenote.data.model.Memo

@Composable
fun FocusToDoScreen(
    navController: NavController,
    viewModel: FocusToDoViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            when {
                // ロード中
                uiState.isLoading -> CircularProgressIndicator()

                // 全タスク完了
                uiState.tasks.isEmpty() && uiState.initialTaskCount > 0 -> {
                    CompletionView(navController)
                }

                // タスク表示
                uiState.tasks.isNotEmpty() -> {
                    val currentTask = uiState.tasks.first()
                    TaskView(
                        task = currentTask,
                        progressText = "${uiState.completedTaskCount + 1} / ${uiState.initialTaskCount}",
                        onCompleteClick = { viewModel.onCompleteClick() },
                        onPostponeClick = { viewModel.onPostponeClick() }
                    )
                }

                // 開始するタスクがない
                else -> NoTasksView(navController)
            }
        }
    }
}

@Composable
fun TaskView(
    task: Memo,
    progressText: String,
    onCompleteClick: () -> Unit,
    onPostponeClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(text = progressText, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(64.dp))


        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = task.impression ?: "",
                style = MaterialTheme.typography.headlineSmall
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = task.toDo ?: "",
                style = MaterialTheme.typography.headlineSmall
            )
        }



        Spacer(modifier = Modifier.height(64.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = onPostponeClick,
                modifier = Modifier.size(width = 140.dp, height = 60.dp)
            ) {
                Text("後回し")
            }
            Button(
                onClick = onCompleteClick,
                modifier = Modifier.size(width = 140.dp, height = 60.dp)
            ) {
                Text("完了")
            }
        }
    }
}

@Composable
fun CompletionView(navController: NavController) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("すべてのタスクが完了しました！", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = { navController.popBackStack() }) {
            Text("リストに戻る")
        }
    }
}

@Composable
fun NoTasksView(navController: NavController) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("実行できるタスクがありません。", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = { navController.popBackStack() }) {
            Text("リストに戻る")
        }
    }
}