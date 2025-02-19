package com.dastan.videoplayer.domain

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dastan.videoplayer.Graph
import com.dastan.videoplayer.data.model.ItemState
import com.dastan.videoplayer.data.model.Video
import com.dastan.videoplayer.data.repository.VideoRepository
import kotlinx.coroutines.launch

class VideoCollectionViewModel(private val videoRepository: VideoRepository = Graph.videoRepository) : ViewModel() {
    private val _videoState = mutableStateOf<List<Video>>(emptyList())
    val videoState: State<List<Video>> = _videoState

    init {
        privateFetchCategories()
    }

    private fun privateFetchCategories() {
        viewModelScope.launch {
            val videos = videoRepository.getData()
            _videoState.value = videos
        }
    }

    fun fetchCategories() {
        privateFetchCategories()
    }

}