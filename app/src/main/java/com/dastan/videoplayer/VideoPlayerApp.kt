package com.dastan.videoplayer

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class VideoPlayerApp: Application(){
    override fun onCreate() {
        super.onCreate()
        Graph.provide(this)
    }
}