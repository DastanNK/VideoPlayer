package com.dastan.videoplayer.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "video-table")
data class VideoCaching(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    @ColumnInfo(name = "video-title")
    val title: String="",
    @ColumnInfo(name = "video-description")
    val description: String="",
    @ColumnInfo(name = "video-sources")
    val sources: String = "",
    @ColumnInfo(name = "video-subtitle")
    val subtitle: String="",
    @ColumnInfo(name = "video-thumb")
    val thumb: String = "",
    @ColumnInfo(name = "last_updated")
    val lastUpdated: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "video-timeline")
    val timeline: String = ""
)
