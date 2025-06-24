package com.example.listenote.ui.memo_create_edit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.listenote.player.AudioPlayerViewModel
import com.example.listenote.player.PlayerUI
import kotlinx.coroutines.flow.collectLatest


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemoCreateEditScreen(
    modifier: Modifier = Modifier,
    audioPlayerViewModel: AudioPlayerViewModel,
    navController: NavController,
    viewModel: MemoCreateEditViewModel
) {

    val uiState = viewModel.uiState
    val currentPosition by audioPlayerViewModel.currentPosition.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.navigateBack.collectLatest {
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (uiState.isEditing) "メモの編集" else "メモの作成") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "戻る"
                        )
                    }
                },
                actions = {
                    // 保存ボタン
                    IconButton(onClick = { viewModel.saveMemo(currentPosition.toLong()) }) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "保存"
                        )
                    }
                    // 削除ボタンは編集モード時のみ表示
                    // 既存のメモを選択した際に編集モードとなる
                    if (uiState.isEditing) {
                        IconButton(onClick = { viewModel.deleteMemo() }) {
                            Icon(Icons.Default.Delete, contentDescription = "削除")
                        }
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
                        value = uiState.impression,
                        onValueChange = { viewModel.onImpressionChange(it) },
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
                        value = uiState.toDo,
                        onValueChange = { viewModel.onToDoChange(it) },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            PlayerUI(
                audioUri = null,
                modifier = Modifier.weight(0.3f),
                viewModel = audioPlayerViewModel
            )
        }
    }
}

