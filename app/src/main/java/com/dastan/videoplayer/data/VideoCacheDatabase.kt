package com.dastan.videoplayer.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.dastan.videoplayer.data.model.VideoCaching

@Database(
    entities = [VideoCaching::class],
    version = 1,
    exportSchema = false
)
abstract class VideoCacheDatabase:RoomDatabase() {
    abstract fun videoDao(): VideoDao
}