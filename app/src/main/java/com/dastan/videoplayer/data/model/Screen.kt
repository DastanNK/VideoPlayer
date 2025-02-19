package com.dastan.videoplayer.data.model

import android.net.Uri
import com.google.gson.Gson

sealed class Screen (val route:String){
    object VideoPlayerScreen : Screen("VideoPlayer/{video}") {
        fun createRoute(video: Video): String = "VideoPlayer/${Uri.encode(Gson().toJson(video))}"
    }
    object VideoCollectionScreen: Screen("VideoCollection")

}