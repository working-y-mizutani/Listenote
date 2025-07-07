package com.example.listenote.ui.notebook_list

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.exponentialDecay
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt


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
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items = notebooks, key = { it.id }) { notebook ->

            DeletableCard(
                onClick = {
                    navController.navigate("notebook/${notebook.id}")
                },
                onDelete = { viewModel.deleteNotebook(notebook) }
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = notebook.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = formatTimestampToDateTime(notebook.createdAt),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.align(Alignment.End)
                    )
                }
            }
        }
    }
}

    // DateTimeFormatter.ofPattern()がAPI26以前で動かないので@RequiresApi
@RequiresApi(Build.VERSION_CODES.O)
fun formatTimestampToDateTime(timestamp: Long): String {

    val formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")

    val instant = Instant.ofEpochMilli(timestamp)
    val localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())

    return formatter.format(localDateTime)
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun DeletableCard(
    onClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val density = LocalDensity.current

    // スワイプで止まる位置(Anchor)を先に定義します
    val anchors = remember {
        DraggableAnchors {
            "Closed" at 0f
            "Open" at with(density) { -100.dp.toPx() } // 100dp分左にスワイプした位置
        }
    }

    // スワイプの状態を管理します
    val anchoredDraggableState = remember {
        AnchoredDraggableState(
            initialValue = "Closed",
            anchors = anchors,
            positionalThreshold = { distance: Float -> distance * 1f },
            velocityThreshold = { 50f },
            snapAnimationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
            decayAnimationSpec = exponentialDecay()
        )
    }

    Box(
        modifier = modifier
            .clip(CardDefaults.shape)
    ) {
        // --- 背景：削除ボタン ---
        Box(
            modifier = modifier
                .fillMaxWidth()
                .clip(CardDefaults.shape) // 全体をカードの形でクリップ
        ) {
            // --- 1. 背景 (赤い削除ボタンエリア) ---
            // Boxがコンテナ全体に広がり、赤い背景
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(MaterialTheme.colorScheme.error)
                    .clickable(onClick = onDelete),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "削除",
                    tint = MaterialTheme.colorScheme.onError,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )
            }

            // --- 2. 前景 (スワイプで動く白いカード部分) ---
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset {
                        IntOffset(
                            x = anchoredDraggableState
                                .requireOffset()
                                .roundToInt(),
                            y = 0
                        )
                    }
                    .anchoredDraggable(
                        state = anchoredDraggableState,
                        orientation = Orientation.Horizontal,
                        reverseDirection = false
                    )
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = {
                                if (anchoredDraggableState.currentValue == "Closed") {
                                    onClick()
                                }
                            },
                            onPress = {
                                // 削除ボタンの領域をタップした場合は、前景のカードがタップイベントを奪わないようにする
                                // requireOffset()が0でない（スワイプされている）場合は何もしない
                                if (anchoredDraggableState.requireOffset() == 0f) {
                                    awaitRelease()
                                }
                            }
                        )
                    },
                // 前景のカードのcontent（本来の表示内容）
                content = content
            )
        }
    }
}