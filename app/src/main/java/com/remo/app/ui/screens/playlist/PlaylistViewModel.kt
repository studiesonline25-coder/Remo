package com.remo.app.ui.screens.playlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.remo.app.data.models.Playlist
import com.remo.app.data.models.Song
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

data class PlaylistUiState(
    val playlist: Playlist? = null,
    val songs: List<Song> = emptyList(),
    val isFollowing: Boolean = false,
    val isLoading: Boolean = false
)

@HiltViewModel
class PlaylistViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlaylistUiState())
    val uiState: StateFlow<PlaylistUiState> = _uiState.asStateFlow()

    fun loadPlaylist(id: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val doc = firestore.collection("playlists").document(id).get().await()
                val playlist = doc.toObject(Playlist::class.java) ?: return@launch

                val songs = if (playlist.songs.isNotEmpty()) {
                    firestore.collection("songs").whereIn("id", playlist.songs.take(10)).get().await()
                        .documents.mapNotNull { it.toObject(Song::class.java) }
                } else emptyList()

                val uid = auth.currentUser?.uid
                val isFollowing = if (uid != null) {
                    val userDoc = firestore.collection("users").document(uid).get().await()
                    val playlists = (userDoc.get("playlists") as? List<*>)?.map { it.toString() } ?: emptyList()
                    id in playlists
                } else false

                _uiState.value = _uiState.value.copy(playlist = playlist, songs = songs, isFollowing = isFollowing, isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun toggleFollow() {
        val uid = auth.currentUser?.uid ?: return
        val playlistId = _uiState.value.playlist?.id ?: return
        val nowFollowing = !_uiState.value.isFollowing
        _uiState.value = _uiState.value.copy(isFollowing = nowFollowing)
        viewModelScope.launch {
            try {
                val userRef = firestore.collection("users").document(uid)
                val doc = userRef.get().await()
                val list = (doc.get("playlists") as? List<*>)?.map { it.toString() }?.toMutableList() ?: mutableListOf()
                if (nowFollowing) list.add(playlistId) else list.remove(playlistId)
                userRef.update("playlists", list).await()
            } catch (_: Exception) {}
        }
    }
}
