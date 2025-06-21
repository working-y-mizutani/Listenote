package com.example.listenote

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.listenote.ui.theme.ListenoteTheme

import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.delay
import androidx.compose.runtime.LaunchedEffect


@Composable
fun MemoEditScreen(
    audioResourceId: Int,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isPlaying by remember { mutableStateOf(false) }
    var currentPosition by remember { mutableFloatStateOf(0f) }
    var totalDuration by remember { mutableFloatStateOf(0f) }

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val audioUri = Uri.parse("android.resource://${context.packageName}/$audioResourceId")
            setMediaItem(MediaItem.fromUri(audioUri))

            addListener(object : Player.Listener {
                override fun onIsPlayingChanged(currentIsPlaying: Boolean) {
                    isPlaying = currentIsPlaying
                }

                override fun onPlaybackStateChanged(playbackState: Int) {
                    // 再生終了時
                    if (playbackState == Player.STATE_ENDED) {
                        isPlaying = false
                        currentPosition = 0f // シークバーを最初に戻す
                        seekTo(0) // ExoPlayerも最初に戻す
                        playWhenReady = false // 停止状態にする
                    }
                    // 準備完了時やバッファリング中も現在の位置を更新したいので、
                    // totalDurationが設定されていない場合にdurationを取得する
                    if (totalDuration == 0f && duration > 0) {
                        totalDuration = duration.toFloat()
                    }
                }

                override fun onTimelineChanged(timeline: androidx.media3.common.Timeline, reason: Int) {
                    if (timeline.isEmpty) {
                        totalDuration = 0f
                    } else {
                        val durationMs = timeline.getPeriod(0, androidx.media3.common.Timeline.Period()).durationMs
                        totalDuration = if (durationMs == C.TIME_UNSET) 0f else durationMs.toFloat()
                    }
                }
            })
            prepare()
        }
    }

    // ★ LaunchedEffectの修正: isPlayingだけでなく、ExoPlayerの準備状態も考慮
    // exoPlayerがnullでなく、かつ準備完了 (STATE_READY) か再生中 (isPlaying) であれば更新を続ける
    LaunchedEffect(isPlaying, exoPlayer.playbackState) { // playbackState をキーに追加
        while (exoPlayer.playbackState == Player.STATE_READY || exoPlayer.isPlaying) {
            currentPosition = exoPlayer.currentPosition.toFloat()
            // totalDurationがまだ不明な場合に更新を試みる
            if (totalDuration == 0f && exoPlayer.duration > 0) {
                totalDuration = exoPlayer.duration.toFloat()
            }
            delay(100) // 100ミリ秒ごとに更新
        }
        // ループ終了時（停止時など）に、念のため最終的な位置を更新
        currentPosition = exoPlayer.currentPosition.toFloat()
    }


    DisposableEffect(exoPlayer) {
        onDispose {
            exoPlayer.release()
        }
    }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(64.dp))

        Row(
            modifier = Modifier.fillMaxWidth(0.8f),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 3秒戻るボタン
            Button(
                onClick = {
                    val newPosition = (exoPlayer.currentPosition - 3000).coerceAtLeast(0L)
                    exoPlayer.seekTo(newPosition)
                    currentPosition = newPosition.toFloat() // ★ シークバーの即時更新
                },
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.FastRewind,
                    contentDescription = "3秒戻る"
                )
            }

            // 再生/一時停止ボタン
            Button(
                onClick = {
                    if (exoPlayer.isPlaying) {
                        exoPlayer.pause()
                    } else {
                        exoPlayer.play()
                    }
                    currentPosition = exoPlayer.currentPosition.toFloat() // ★ シークバーの即時更新
                },
                modifier = Modifier.size(64.dp)
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    contentDescription = if (isPlaying) "一時停止" else "再生"
                )
            }

            // 3秒進むボタン
            Button(
                onClick = {
                    val newPosition = (exoPlayer.currentPosition + 3000).coerceAtMost(exoPlayer.duration.coerceAtLeast(0L))
                    exoPlayer.seekTo(newPosition)
                    currentPosition = newPosition.toFloat() // ★ シークバーの即時更新
                },
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.FastForward,
                    contentDescription = "3秒進む"
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // シークバー
        Slider(
            value = if (totalDuration > 0) currentPosition / totalDuration else 0f,
            onValueChange = { newValue ->
                // ドラッグ中はexoPlayer.currentPositionは更新されないが、
                // UI上のcurrentPositionはリアルタイムで反映させる
                currentPosition = newValue * totalDuration
            },
            onValueChangeFinished = {
                exoPlayer.seekTo((currentPosition).toLong())
            },
            modifier = Modifier.fillMaxWidth(0.8f)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 再生時間表示
        Row(modifier = Modifier.fillMaxWidth(0.8f)) {
            Text(text = formatDuration(currentPosition.toLong()))
            Spacer(modifier = Modifier.weight(1f))
            Text(text = formatDuration(totalDuration.toLong()))
        }
    }
}

fun formatDuration(ms: Long): String {
    if (ms < 0) return "00:00"
    val minutes = (ms / 1000) / 60
    val seconds = (ms / 1000) % 60
    return String.format("%02d:%02d", minutes, seconds)
}


@Preview(showBackground = true)
@Composable
fun MemoEditPreview() {
    ListenoteTheme {
        MemoEditScreen(audioResourceId = R.raw.test)
    }
}