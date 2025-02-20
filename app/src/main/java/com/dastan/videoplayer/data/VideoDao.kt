package com.dastan.videoplayer.data

import androidx.room.*
import com.dastan.videoplayer.data.model.VideoCaching
import kotlinx.coroutines.flow.Flow

@Dao
abstract class VideoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun addAVideo(videoCaching: VideoCaching)

    @Query("Select * from `video-table`")
    abstract fun getAllVideos(): Flow<List<VideoCaching>>

    @Query("DELETE FROM `video-table`")
    abstract suspend fun clearAll()

    @Query("Select MAX(last_updated) from 'video-table'")
    abstract suspend fun getLastUpdated(): Long?
}