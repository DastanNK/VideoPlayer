package com.dastan.videoplayer.data.model

data class Category(
    val name: String?=null,
    val videos: List<Video> = emptyList()
)
