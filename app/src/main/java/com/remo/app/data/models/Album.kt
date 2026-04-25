package com.remo.app.data.models

data class Album(
    val id: String = "",
    val title: String = "",
    val artistId: String = "",
    val artistName: String = "",
    val coverArtUrl: String = "",
    val releaseDate: String = "",
    val genre: String = "",
    val songs: List<String> = emptyList(),
    val totalTracks: Int = 0,
    val type: String = "album" // album, single, ep
)
