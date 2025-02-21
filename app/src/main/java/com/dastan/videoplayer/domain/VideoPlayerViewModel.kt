package com.dastan.videoplayer.domain

import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import com.dastan.videoplayer.data.model.Video
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class VideoPlayerViewModel @Inject constructor(
    val player: Player,
) : ViewModel() {

    private val _currentVideo = MutableStateFlow<Video?>(null)
    val currentVideo: StateFlow<Video?> = _currentVideo.asStateFlow()
    private var lastKnownPosition: Long = 0L


    fun updateVideo(newVideo: Video) {
        _currentVideo.value = newVideo
        playVideo()
    }

    fun playVideo() {
        val mediaItem = _currentVideo.value?.sources?.let { MediaItem.fromUri(it) }
        mediaItem?.let {
            player.setMediaItem(it)
            player.prepare()
            //player.play()

        }
    }
    fun clearVideo() {
        _currentVideo.value = null
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
        lastKnownPosition = player.currentPosition
    }

    fun restorePosition() {
        if (lastKnownPosition > 0) {
            player.seekTo(lastKnownPosition)
        }
    }

    override fun onCleared() {
        super.onCleared()
        player.release()
    }
}
