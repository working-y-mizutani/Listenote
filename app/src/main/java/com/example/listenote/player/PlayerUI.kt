package com.example.listenote.player

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.listenote.ui.util.formatDuration


@Composable
fun PlayerUI(
    audioUri: String?,
    modifier: Modifier = Modifier,
    viewModel: AudioPlayerViewModel = viewModel()
) {
    val isPlaying by viewModel.isPlaying.collectAsState()
    val currentPosition by viewModel.currentPosition.collectAsState()
    val totalDuration by viewModel.totalDuration.collectAsState()

    LaunchedEffect(audioUri) {
        audioUri?.let {
            viewModel.loadAudio(it.toUri())
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth() // 横幅は最大に
            .wrapContentHeight(), // 高さはコンテンツに合わせる
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(0.8f),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 3秒戻るボタン
            Button(
                onClick = { viewModel.seekBackward() },
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.FastRewind,
                    contentDescription = "3秒戻る"
                )
            }

            // 再生/一時停止ボタン
            Button(
                onClick = { viewModel.playPause() },
                modifier = Modifier.size(64.dp)
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    contentDescription = if (isPlaying) "一時停止" else "再生"
                )
            }

            // 3秒進むボタン
            Button(
                onClick = { viewModel.seekForward() },
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.FastForward,
                    contentDescription = "3秒進む"
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // シークバー
        Slider(
            value = if (totalDuration > 0) currentPosition.toFloat() / totalDuration.toFloat() else 0f,
            onValueChange = { newValue -> viewModel.onSliderValueChange(newValue) },
            onValueChangeFinished = { viewModel.onSliderValueChangeFinished() },
            modifier = Modifier.fillMaxWidth(0.8f)
        )

        Spacer(modifier = Modifier.height(4.dp))

        // 再生時間表示
        Row(modifier = Modifier.fillMaxWidth(0.8f)) {
            Text(text = formatDuration(currentPosition.toLong()))
            Spacer(modifier = Modifier.weight(1f))
            Text(text = formatDuration(totalDuration.toLong()))
        }
    }
}
