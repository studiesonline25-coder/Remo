package com.remo.app.data.models

data class Playlist(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val ownerId: String = "",
    val ownerName: String = "",
    val coverArtUrl: String = "",
    val songs: List<String> = emptyList(),
    val isPublic: Boolean = true,
    val followerCount: Long = 0L,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
