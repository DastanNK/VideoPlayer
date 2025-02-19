package com.dastan.videoplayer.data

import com.dastan.videoplayer.data.model.VideoData
import retrofit2.http.GET

interface Api {
    @GET("DastanNK/VideoPlayerAPi/refs/heads/main/api.json")
    suspend fun retrieveData(): VideoData
}