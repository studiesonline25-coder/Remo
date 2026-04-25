package com.remo.app.data.models

data class Song(
    val id: String = "",
    val title: String = "",
    val artistId: String = "",
    val artistName: String = "",
    val albumId: String = "",
    val albumName: String = "",
    val albumArtUrl: String = "",
    val audioUrl: String = "",
    val duration: Long = 0L,
    val genre: String = "",
    val releaseDate: String = "",
    val playCount: Long = 0L,
    val isExplicit: Boolean = false
)
