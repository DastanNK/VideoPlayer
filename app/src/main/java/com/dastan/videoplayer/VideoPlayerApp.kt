package com.dastan.videoplayer

import android.app.Application
import com.dastan.videoplayer.di.Graph
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class VideoPlayerApp: Application(){
    override fun onCreate() {
        super.onCreate()
        Graph.provide(this)
    }
}