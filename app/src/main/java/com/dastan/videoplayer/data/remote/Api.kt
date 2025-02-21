package com.dastan.videoplayer.data.remote

import com.dastan.videoplayer.data.model.VideoData
import retrofit2.Response
import retrofit2.http.GET

interface Api {
    @GET("DastanNK/VideoPlayerAPi/refs/heads/main/api.json")
    suspend fun retrieveData(): Response<VideoData>
}