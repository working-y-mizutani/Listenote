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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.listenote.R
import com.example.listenote.player.AudioPlayerViewModel
import com.example.listenote.player.PlayerUI
import com.example.listenote.ui.util.formatDuration

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemoCreateEditScreen(
    modifier: Modifier = Modifier,
    audioPlayerViewModel: AudioPlayerViewModel,
    navController: NavController,
    viewModel: MemoCreateEditViewModel
) {

    val uiState = viewModel.uiState

    // メモ削除時の確認ダイアログ
    if (uiState.showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.onDismissDeleteDialog() },
            title = { Text(stringResource(id = R.string.memo_delete_dialog_title)) },
            text = { Text(stringResource(id = R.string.memo_delete_dialog_text)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteMemo()
                    }
                ) {
                    Text(stringResource(id = R.string.common_yes))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.onDismissDeleteDialog() }
                ) {
                    Text(stringResource(id = R.string.common_no))
                }
            }
        )
    }

    // Unitは変化しない値なので初回だけ中の処理が行われる
    // viewModelの_navigateBack.emit(Unit)が呼ばれると処理される
    LaunchedEffect(Unit) {
        // collectでnavigateBackの変化を監視
        viewModel.navigateBack.collect {
            navController.popBackStack()
        }
    }


    //ScaffoldならTopAppBarを使えるため使用
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        if (uiState.isEditing)
                            stringResource(id = R.string.memo_edit_title)
                        else
                            stringResource(id = R.string.memo_create_title)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.common_back)
                        )
                    }
                },
                actions = {
                    // 保存ボタン
                    IconButton(onClick = { viewModel.saveMemo() }) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = stringResource(id = R.string.common_save)
                        )
                    }
                    // 削除ボタンは編集モード時のみ表示
                    // 既存のメモを選択した際に編集モードとなる
                    if (uiState.isEditing) {
                        IconButton(onClick = { viewModel.onDeleteRequest() }) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = stringResource(id = R.string.common_delete)
                            )
                        }
                    }
                })


        },

        bottomBar = {
            Surface(
                // Surfaceに影をつけて、コンテンツとの境界を明確にする
                shadowElevation = 8.dp
            ) {

                PlayerUI(
                    audioUri = null,
                    viewModel = audioPlayerViewModel
                )
            }
        }

    ) { innerPadding ->

        Column(
            modifier = Modifier
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
                    Text(text = stringResource(id = R.string.memo_label_time), modifier = Modifier.width(labelWidth))
                    Text(text = formatDuration(uiState.timestamp))
                }
                Spacer(modifier = Modifier.height(spaceBetween))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f), verticalAlignment = Alignment.Top
                ) {
                    Text(text = stringResource(id = R.string.memo_label_impression), modifier = Modifier.width(labelWidth))
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
                        .weight(1f), verticalAlignment = Alignment.Top
                ) {
                    Text(text = "ToDo", modifier = Modifier.width(labelWidth))
                    TextField(
                        value = uiState.toDo,
                        onValueChange = { viewModel.onToDoChange(it) },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

        }
    }
}

