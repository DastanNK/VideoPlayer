package com.dastan.videoplayer

import android.content.Context
import androidx.room.Room
import com.dastan.videoplayer.data.VideoCacheDatabase
import com.dastan.videoplayer.data.repository.VideoRepository

object Graph {
    lateinit var videoCacheDatabase: VideoCacheDatabase

    val videoRepository by lazy{
        VideoRepository(videoDao = videoCacheDatabase.videoDao())
    }

    fun provide(context: Context){
        videoCacheDatabase = Room.databaseBuilder(context, VideoCacheDatabase::class.java, "VideoCache3.db").build()
    }
}