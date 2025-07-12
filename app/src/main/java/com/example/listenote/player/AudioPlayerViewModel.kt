package com.example.listenote.player

import android.app.Application
import android.content.ComponentName
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AudioPlayerViewModel(application: Application) : AndroidViewModel(application) {

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying = _isPlaying.asStateFlow()

    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition = _currentPosition.asStateFlow()

    private val _totalDuration = MutableStateFlow(0L)
    val totalDuration = _totalDuration.asStateFlow()

    private val _isBuffering = MutableStateFlow(false)
    val isBuffering = _isBuffering.asStateFlow()

    // 再生エラーメッセージを管理するStateFlow
    private val _playbackError = MutableStateFlow<String?>(null)
    val playbackError = _playbackError.asStateFlow()

    private var positionUpdateJob: Job? = null
    private var mediaController: MediaController? = null

    private var currentLoadedUri: Uri? = null


    init {
        val sessionToken = SessionToken(
            getApplication(),
            ComponentName(getApplication(), PlaybackService::class.java)
        )
        val controllerFuture = MediaController.Builder(getApplication(), sessionToken).buildAsync()
        controllerFuture.addListener(
            {
                mediaController = controllerFuture.get()
                mediaController?.addListener(playerListener)
                updateState()
            },
            MoreExecutors.directExecutor()
        )
    }

    // "object : AnyClass" はkotlinの匿名クラスの書き方
    private val playerListener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _isPlaying.value = isPlaying
            if (isPlaying) {
                _playbackError.value = null
                startPositionUpdates()
            } else {
                stopPositionUpdates()
            }
        }

        override fun onPlayerError(error: PlaybackException) {
            _playbackError.value = "音源を再生できません。ネットワーク接続を確認してください。"
            Log.e("AudioPlayerViewModel", "Playback error: ", error)
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            _isBuffering.value = playbackState == Player.STATE_BUFFERING
            updateState()
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {

            if (mediaItem == null) {
                currentLoadedUri = null
            }
            updateState()
        }
    }

    private fun updateState() {
        mediaController?.let {
            _isPlaying.value = it.isPlaying
            _totalDuration.value = if (it.duration > 0) it.duration else 0L
            _currentPosition.value = if (it.currentPosition > 0) it.currentPosition else 0L
            if (it.isPlaying) {
                startPositionUpdates()
            }
        }
    }

    private fun startPositionUpdates() {
        // これが何度も呼ばれた場合、古いものと同時に動いてしまうかもしれないから
        // ここでキャンセル
        positionUpdateJob?.cancel()
        // launchの中で再生位置更新の処理を書いている
        // positionUpdateJobを握っておき、止められる準備をしている
        positionUpdateJob = viewModelScope.launch {
            while (true) {
                mediaController?.currentPosition?.let {
                    _currentPosition.value = it
                }
                delay(100) // 100ミリ秒ごとに再生位置を更新
            }
        }
    }

    private fun stopPositionUpdates() {
        positionUpdateJob?.cancel()
    }

    fun loadAudio(uri: Uri) {

        // これがないとメモ保存時などに再生位置が0になってしまう
        if (currentLoadedUri == uri) {
            // 同じ音源が既にロードされていれば、何もしない
            return
        }
        mediaController?.setMediaItem(MediaItem.fromUri(uri))
        // prepare()で再生準備をさせる
        mediaController?.prepare()

        currentLoadedUri = uri
    }

    fun playPause() {
        mediaController?.let {
            if (it.isPlaying) {
                it.pause()
            } else {
                it.play()
            }
        }
    }

    fun pause() {
        mediaController?.let {
            if (it.isPlaying) {
                it.pause()
            }
        }
    }

    private val seekInterval = 3000L

    fun seekForward() {
        mediaController?.let {
            val newPosition = (it.currentPosition + seekInterval).coerceAtMost(it.duration)
            it.seekTo(newPosition)
        }
    }

    fun seekBackward() {
        mediaController?.let {
            val newPosition = (it.currentPosition - seekInterval).coerceAtLeast(0L)
            it.seekTo(newPosition)
        }
    }

    fun onSliderValueChange(newPosition: Float) {
        // スライダーをドラッグ中はUI上の表示だけを更新
        positionUpdateJob?.cancel()
        _currentPosition.value = (newPosition * _totalDuration.value).toLong()
    }

    fun onSliderValueChangeFinished() {
        // ドラッグが終わったら、実際のプレイヤーの再生位置を変更
        mediaController?.seekTo(_currentPosition.value)
        if (isPlaying.value) {
            startPositionUpdates()
        }

    }

    fun onErrorMessageShown() {
        _playbackError.value = null
    }

    override fun onCleared() {
        super.onCleared()
        // ViewModelが破棄されるときにMediaControllerを解放する
        currentLoadedUri = null
        mediaController?.release()
        mediaController = null
    }
}