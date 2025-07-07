package com.example.listenote.ui.todo_list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.listenote.data.model.Memo
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.ReorderableLazyListState
import org.burnoutcrew.reorderable.detectReorder
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable


@Composable
fun ToDoListScreen(
    modifier: Modifier = Modifier, navController: NavController,
    viewModel: ToDoListViewModel = viewModel()
) {

    val memos by viewModel.memos.collectAsState()
    val state = rememberReorderableLazyListState(
        // from,toはリスト内のindex
        onMove = { from, to ->
            viewModel.moveItem(from.index, to.index)
        },
        onDragEnd = { startIndex, endIndex ->
            viewModel.saveOrder()
        })

    // SQL空の取得は順番が確定していないので並べ替えしてLazyColumnに渡す
    val sortedMemos = memos.sortedBy { it.toDoPosition }
    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface) // 背景色
            ) {
                // 「ドラッグで並べ替え」のテキスト
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "並べ替え"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("ドラッグで並べ替え")
                }

                // 「全て完了/未完了にする」ボタン
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = { viewModel.updateAllComplete() },
                        modifier = Modifier.weight(1f),
                        shape = RectangleShape
                    ) {
                        Text(text = "全て完了にする")
                    }
                    VerticalDivider(
                        modifier = Modifier
                            .height(48.dp)
                            .width(1.dp),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                    )
                    Button(
                        onClick = { viewModel.updateAllIncomplete() },
                        modifier = Modifier.weight(1f),
                        shape = RectangleShape
                    ) {
                        Text(text = "全て未完了にする")
                    }
                }
            }
        },
        // --- 下部の固定UI ---
        bottomBar = {
            // BottomAppBarで囲むのをやめ、Buttonを直接配置する
            Button(
                onClick = {
                    val notebookId = viewModel.notebook.value?.id
                    if (notebookId != null) {
                        navController.navigate("focus_todo_screen/$notebookId")
                    }
                },
                enabled = sortedMemos.any { !it.isCompleted },
                // modifierをfillMaxWidth()に戻す
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shape = RectangleShape
            ) {
                Text("TODOモードへ")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            state = state.listState,
            modifier = Modifier
                .padding(innerPadding) // ★Scaffoldからのpaddingを適用
                .fillMaxWidth()
                .reorderable(state)
        ) {
            items(sortedMemos, key = { it.id }) { memo ->
                ReorderableItem(state, key = memo.id) { isDragging ->
                    ToDoItem(
                        memo = memo,
                        state = state,
                        onCheckedChange = { isChecked ->
                            viewModel.updateCompletion(memo.id, isChecked)
                        },
                        modifier = Modifier.shadow(if (isDragging) 8.dp else 0.dp)
                    )
                }
            }
        }

    }


}

@Composable
fun ToDoItem(
    memo: Memo,
    state: ReorderableLazyListState,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. ドラッグハンドル
            Icon(
                imageVector = Icons.Default.DragHandle,
                contentDescription = "並べ替えハンドル",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.detectReorder(state)
            )

            Spacer(modifier = Modifier.width(16.dp))

            // 2. チェックボックス
            Checkbox(
                checked = memo.isCompleted,
                onCheckedChange = onCheckedChange
            )

            Spacer(modifier = Modifier.width(16.dp))


            Text(
                text = getCardText(memo),
                style = if (memo.isCompleted) {
                    // isCompletedがtrueなら打ち消し線とグレー表示
                    MaterialTheme.typography.bodyLarge.copy(
                        textDecoration = TextDecoration.LineThrough,
                        color = Color.Gray
                    )
                } else {
                    MaterialTheme.typography.bodyLarge
                }
            )

        }
    }
}

private fun getCardText(memo: Memo): String {

    return when {
        !memo.toDo.isNullOrEmpty() -> memo.toDo
        !memo.impression.isNullOrEmpty() -> memo.impression
        else -> "メモ未記入"
    }
}