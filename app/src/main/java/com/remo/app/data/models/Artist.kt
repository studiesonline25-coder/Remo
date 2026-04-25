package com.remo.app.data.models

data class Artist(
    val id: String = "",
    val name: String = "",
    val bio: String = "",
    val imageUrl: String = "",
    val headerImageUrl: String = "",
    val genres: List<String> = emptyList(),
    val monthlyListeners: Long = 0L,
    val followerCount: Long = 0L,
    val albums: List<String> = emptyList(),
    val popularSongs: List<String> = emptyList()
)
