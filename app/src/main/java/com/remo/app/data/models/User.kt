package com.remo.app.data.models

data class User(
    val id: String = "",
    val email: String = "",
    val displayName: String = "",
    val profileImageUrl: String = "",
    val followingArtists: List<String> = emptyList(),
    val likedSongs: List<String> = emptyList(),
    val savedAlbums: List<String> = emptyList(),
    val playlists: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
)
