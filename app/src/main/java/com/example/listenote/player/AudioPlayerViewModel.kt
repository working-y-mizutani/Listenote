package com.example.listenote.player

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

//再生uiに関するViewModel
class AudioPlayerViewModel(application: Application) : AndroidViewModel(application) {

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying = _isPlaying.asStateFlow()

    //現在の再生位置
    private val _currentPosition = MutableStateFlow(0f)
    val currentPosition = _currentPosition.asStateFlow()

    //音声の長さ
    private val _totalDuration = MutableStateFlow(0f)
    val totalDuration = _totalDuration.asStateFlow()

    private var exoPlayer: ExoPlayer? = null
    private var positionUpdateJob: Job? = null

    init {
        exoPlayer = ExoPlayer.Builder(getApplication()).build().apply {
            addListener(object : Player.Listener {
                override fun onIsPlayingChanged(currentIsPlaying: Boolean) {
                    _isPlaying.value = currentIsPlaying
                    if (currentIsPlaying) {
                        startPositionUpdates()
                    } else {
                        stopPositionUpdates()
                    }
                }

                override fun onPlaybackStateChanged(playbackState: Int) {
                    if (playbackState == Player.STATE_ENDED) {
                        _isPlaying.value = false
                        seekTo(0) // 再生位置を最初に戻す
                        // playWhenReady = false // 自動でfalseになるので不要な場合も
                    }
                }

                override fun onTimelineChanged(timeline: Timeline, reason: Int) {
                    if (!timeline.isEmpty) {
                        val durationMs = timeline.getPeriod(0, Timeline.Period()).durationMs
                        _totalDuration.value = if (durationMs == C.TIME_UNSET) 0f else durationMs.toFloat()
                    }
                }
            })
        }
    }

    fun loadAudio(uri: Uri) {
        exoPlayer?.let {
            // 現在再生中のメディアと違う場合のみ、新しいメディアをセットする
            if (it.currentMediaItem?.localConfiguration?.uri != uri) {
                it.setMediaItem(MediaItem.fromUri(uri))
                it.prepare()
            }
        }
    }




    private fun startPositionUpdates() {
        positionUpdateJob?.cancel()
        positionUpdateJob = viewModelScope.launch {
            while (_isPlaying.value) {
                _currentPosition.value = exoPlayer?.currentPosition?.toFloat() ?: 0f
                delay(100)
            }
        }
    }

    private fun stopPositionUpdates() {
        positionUpdateJob?.cancel()
        _currentPosition.value = exoPlayer?.currentPosition?.toFloat() ?: 0f
    }

    fun playPause() {
        exoPlayer?.let {
            if (it.isPlaying) {
                it.pause()
            } else {
                it.play()
            }
        }
    }

    //ボタンを押して3秒単位で前後できるようにする
    //3秒ぐらいが長すぎず短すぎないため
    private val seekInterval = 3000L

    fun seekForward() {
        exoPlayer?.let {
            val newPosition =
                (it.currentPosition + seekInterval).coerceAtMost(it.duration.coerceAtLeast(0L))
            it.seekTo(newPosition)
            _currentPosition.value = newPosition.toFloat()
        }
    }

    fun seekBackward() {
        exoPlayer?.let {
            val newPosition = (it.currentPosition - seekInterval).coerceAtLeast(0L)
            it.seekTo(newPosition)
            _currentPosition.value = newPosition.toFloat()
        }
    }

    fun onSliderValueChange(newPosition: Float) {
        _currentPosition.value = newPosition * _totalDuration.value
    }

    fun onSliderValueChangeFinished() {
        exoPlayer?.seekTo(_currentPosition.value.toLong())
    }

    override fun onCleared() {
        super.onCleared()
        exoPlayer?.release()
        exoPlayer = null
    }
}