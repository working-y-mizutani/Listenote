package com.example.listenote.ui.focus_todo

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
fun TaskView(task: Memo, progressText: String, onCompleteClick: () -> Unit, onPostponeClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(text = progressText, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(64.dp))

        Text(
            text = task.toDo ?: "",
            fontSize = 24.sp,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(64.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = onPostponeClick, modifier = Modifier.size(width = 140.dp, height = 60.dp)) {
                Text("後回し")
            }
            Button(onClick = onCompleteClick, modifier = Modifier.size(width = 140.dp, height = 60.dp)) {
                Text("完了")
            }
        }
    }
}

@Composable
fun CompletionView(navController: NavController) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("すべてのタスクが完了しました！", fontSize = 20.sp)
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = { navController.popBackStack() }) {
            Text("リストに戻る")
        }
    }
}

@Composable
fun NoTasksView(navController: NavController) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("実行できるタスクがありません。", fontSize = 20.sp)
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = { navController.popBackStack() }) {
            Text("リストに戻る")
        }
    }
}