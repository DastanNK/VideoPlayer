package com.dastan.videoplayer.data.model

data class ItemState(
    val listVideo: List<Video>? = null,
    val loading: Boolean = true,
    val error: String? = null
)
