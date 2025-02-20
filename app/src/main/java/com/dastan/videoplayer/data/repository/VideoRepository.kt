package com.dastan.videoplayer.data.repository

import android.util.Log
import com.dastan.videoplayer.data.RetrofitInstance
import com.dastan.videoplayer.data.model.VideoCaching
import com.dastan.videoplayer.data.VideoDao
import com.dastan.videoplayer.data.model.Video
import kotlinx.coroutines.flow.first

private const val REFRESH_INTERVAL = 24 * 60 * 60 * 1000L

class VideoRepository(private val videoDao: VideoDao) {
    suspend fun getData(): List<Video> {
        return try {
            val cachedVideos = getCachedVideosForDomain()
            if (cachedVideos.isNotEmpty() && isCacheValid()) {
                Log.d("VideoRepository", "get data from database")
                return cachedVideos
            }
            val newVideos = fetchVideosFromNetwork()
            saveVideosToCache(newVideos)
            Log.d("VideoRepository", "get data from network")
            newVideos
        } catch (e: Exception) {
            videoDao.getAllVideos().first().map { it.toDomain() }
        }
    }

    private suspend fun isCacheValid():Boolean{
        val lastUpdated = videoDao.getLastUpdated() ?: 0L
        val currentTime = System.currentTimeMillis()
        return (currentTime - lastUpdated) < REFRESH_INTERVAL
    }

    private suspend fun getCachedVideos(): List<VideoCaching> {
        return videoDao.getAllVideos().first()
    }

    private suspend fun getCachedVideosForDomain(): List<Video> {
        return getCachedVideos().map { it.toDomain() }
    }

    private suspend fun fetchVideosFromNetwork(): List<Video> {
        val response = RetrofitInstance.api.retrieveData().categories.flatMap { it.videos }
        return response
    }

    private suspend fun saveVideosToCache(videos: List<Video>) {
        try {
            videoDao.clearAll()
            videos.forEach { video ->
                videoDao.addAVideo(videoCaching = video.toCaching())
            }
        } catch (e: Exception) {
            Log.d("VideoRepository", "Error caching videos")
        }
    }


    private fun Video.toCaching(): VideoCaching {
        return VideoCaching(
            title = this.title,
            description = this.description,
            sources = this.sources,
            subtitle = this.subtitle,
            thumb = this.thumb,
            timeline = this.timeline
        )
    }

    private fun VideoCaching.toDomain(): Video {
        return Video(
            title = this.title,
            description = this.description,
            sources = this.sources,
            subtitle = this.subtitle,
            thumb = this.thumb,
            timeline = this.timeline
        )
    }
}