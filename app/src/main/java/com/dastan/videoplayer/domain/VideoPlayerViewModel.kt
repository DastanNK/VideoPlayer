package com.dastan.videoplayer.domain

import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import com.dastan.videoplayer.data.model.Video
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class VideoPlayerViewModel @Inject constructor(
    val player: Player,
) : ViewModel() {

    private val _currentVideo = MutableStateFlow<Video?>(null)
    //val currentVideo: StateFlow<Video?> = _currentVideo.asStateFlow()

    init {
        player.prepare()
    }

    fun updateVideo(newVideo: Video) {
        _currentVideo.value = newVideo
        playVideo()
    }

    fun playVideo(
        //uri: Uri
    ) {
        val mediaItem = _currentVideo.value?.sources?.let { MediaItem.fromUri(it) }
        mediaItem?.let {
            player.setMediaItem(it)
            player.play()
        }
    }

    fun pauseVideo() {
        player.pause()
    }

    fun resumeVideo() {
        player.play()
    }

    /*fun stopVideo() {
        player.stop()
    }*/

    fun skipForward(seconds: Int = 10) {
        player.seekTo((player.currentPosition + seconds * 1000).coerceAtMost(player.duration))
    }

    fun skipBackward(seconds: Int = 10) {
        player.seekTo((player.currentPosition - seconds * 1000).coerceAtLeast(0))
    }

    override fun onCleared() {
        super.onCleared()
        player.release()
    }
}
