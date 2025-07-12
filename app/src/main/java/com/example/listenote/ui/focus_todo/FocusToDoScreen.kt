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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.listenote.R
import com.example.listenote.data.model.Memo
import com.example.listenote.ui.util.formatDuration

@Composable
fun FocusToDoScreen(
    navController: NavController,
    viewModel: FocusToDoViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        bottomBar = {
            if (!uiState.tasks.isNotEmpty()) return@Scaffold

            Surface {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    OutlinedButton(
                        onClick = { viewModel.onPostponeClick() },
                        modifier = Modifier
                            .weight(1f)
                            .height(64.dp),
                        shape = RectangleShape,
                    ) {
                        Text(stringResource(id = R.string.focus_todo_postpone))
                    }
                    Button(
                        onClick = { viewModel.onCompleteClick() },
                        modifier = Modifier
                            .weight(1f)
                            .height(64.dp),
                        shape = RectangleShape,
                    ) {
                        Text(stringResource(id = R.string.focus_todo_complete))
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
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
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        Text(text = progressText, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = formatDuration(task.timestamp),
                style = MaterialTheme.typography.titleMedium
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = task.impression ?: "",
                style = MaterialTheme.typography.titleLarge
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = task.toDo ?: "",
                style = MaterialTheme.typography.titleLarge
            )
        }

    }
}

@Composable
fun CompletionView(navController: NavController) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            stringResource(id = R.string.focus_todo_all_tasks_completed),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = { navController.popBackStack() }) {
            Text(stringResource(id = R.string.focus_todo_back_to_list))
        }
    }
}

@Composable
fun NoTasksView(navController: NavController) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            stringResource(id = R.string.focus_todo_no_tasks),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = { navController.popBackStack() }) {
            Text(stringResource(id = R.string.focus_todo_back_to_list))
        }
    }
}