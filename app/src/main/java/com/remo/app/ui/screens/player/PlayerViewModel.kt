package com.remo.app.ui.screens.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.remo.app.data.models.Song
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

data class PlayerUiState(
    val currentSong: Song? = null,
    val isPlaying: Boolean = false,
    val progress: Float = 0f,
    val currentPosition: Long = 0L,
    val duration: Long = 0L,
    val isShuffled: Boolean = false,
    val repeatMode: Int = 0, // 0=off, 1=all, 2=one
    val isLiked: Boolean = false
)

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    fun setCurrentSong(song: Song) {
        _uiState.value = _uiState.value.copy(currentSong = song, progress = 0f, currentPosition = 0L, duration = song.duration)
        checkIfLiked(song.id)
    }

    fun playPause() {
        _uiState.value = _uiState.value.copy(isPlaying = !_uiState.value.isPlaying)
    }

    fun skipNext() {
        _uiState.value = _uiState.value.copy(progress = 0f, currentPosition = 0L)
    }

    fun skipPrevious() {
        _uiState.value = _uiState.value.copy(progress = 0f, currentPosition = 0L)
    }

    fun seekTo(position: Float) {
        val newPosition = (position * _uiState.value.duration).toLong()
        _uiState.value = _uiState.value.copy(progress = position, currentPosition = newPosition)
    }

    fun toggleShuffle() {
        _uiState.value = _uiState.value.copy(isShuffled = !_uiState.value.isShuffled)
    }

    fun toggleRepeat() {
        val next = (_uiState.value.repeatMode + 1) % 3
        _uiState.value = _uiState.value.copy(repeatMode = next)
    }

    fun toggleLike() {
        val song = _uiState.value.currentSong ?: return
        val uid = auth.currentUser?.uid ?: return
        val nowLiked = !_uiState.value.isLiked
        _uiState.value = _uiState.value.copy(isLiked = nowLiked)
        viewModelScope.launch {
            try {
                val userRef = firestore.collection("users").document(uid)
                val doc = userRef.get().await()
                val liked = (doc.get("likedSongs") as? List<*>)?.map { it.toString() }?.toMutableList() ?: mutableListOf()
                if (nowLiked) liked.add(song.id) else liked.remove(song.id)
                userRef.update("likedSongs", liked).await()
            } catch (_: Exception) {}
        }
    }

    private fun checkIfLiked(songId: String) {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                val doc = firestore.collection("users").document(uid).get().await()
                val liked = (doc.get("likedSongs") as? List<*>)?.map { it.toString() } ?: emptyList()
                _uiState.value = _uiState.value.copy(isLiked = songId in liked)
            } catch (_: Exception) {}
        }
    }

    fun formatTime(ms: Long): String {
        val totalSeconds = ms / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return "%d:%02d".format(minutes, seconds)
    }
}
