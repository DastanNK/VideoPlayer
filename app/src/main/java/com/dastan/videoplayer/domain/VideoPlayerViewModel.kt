package com.dastan.videoplayer.domain

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import com.dastan.videoplayer.data.model.Video
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoPlayerViewModel @Inject constructor(
    val player: Player,
) : ViewModel() {

    private val _currentVideo = MutableStateFlow<Video?>(null)
    val currentVideo: StateFlow<Video?> = _currentVideo.asStateFlow()
    private var lastKnownPosition: Long = 0L

    private val _currentPosition = MutableStateFlow(0f)
    val currentPosition: StateFlow<Float> = _currentPosition

    private val _duration = MutableStateFlow(0f)
    val duration: StateFlow<Float> = _duration

    fun updateVideo(newVideo: Video) {
        _currentVideo.value = newVideo
        playVideo()
    }

    fun playVideo() {
        val mediaItem = _currentVideo.value?.sources?.let { MediaItem.fromUri(it) }
        mediaItem?.let {
            player.setMediaItem(it)
            player.prepare()
        }
        viewModelScope.launch {

            while (true) {
                _currentPosition.value = player?.currentPosition?.toFloat() ?: 0f
                _duration.value = player?.duration?.takeIf { it > 0 }?.toFloat() ?: 0f
                delay(500)
            }
        }
    }


    fun clearVideo() {
        lastKnownPosition=0L
        _currentVideo.value = null
        player.clearMediaItems()
    }
    fun pauseVideo() {
        player.pause()
    }

    fun resumeVideo() {
        player.play()
    }

    fun stopVideo() {
        player.stop()
    }

    fun skipForward(seconds: Int = 10) {
        player.seekTo((player.currentPosition + seconds * 1000).coerceAtMost(player.duration))
    }

    fun skipBackward(seconds: Int = 10) {
        player.seekTo((player.currentPosition - seconds * 1000).coerceAtLeast(0))
    }

    fun rememberPosition() {
        if (lastKnownPosition != 0L) {
            lastKnownPosition = player.currentPosition
        }
    }

    fun restorePosition() {
        if (lastKnownPosition > 0) {
            player.seekTo(lastKnownPosition)
        }
    }

    fun seekTo(position: Float) {
        player?.seekTo(position.toLong())
    }

    fun formatTime(millis: Long): String {
        val seconds = (millis / 1000) % 60
        val minutes = (millis / 1000) / 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    override fun onCleared() {
        super.onCleared()
        player.release()
    }
}
