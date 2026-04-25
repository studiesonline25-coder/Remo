package com.remo.app.ui.screens.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.remo.app.data.models.Album
import com.remo.app.data.models.Artist
import com.remo.app.data.models.Playlist
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

data class LibraryUiState(
    val playlists: List<Playlist> = emptyList(),
    val albums: List<Album> = emptyList(),
    val artists: List<Artist> = emptyList(),
    val isLoading: Boolean = false
)

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _uiState = MutableStateFlow(LibraryUiState())
    val uiState: StateFlow<LibraryUiState> = _uiState.asStateFlow()

    init {
        loadLibrary()
    }

    fun loadLibrary() {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val playlistsSnap = firestore.collection("playlists")
                    .whereEqualTo("ownerId", uid).get().await()
                val playlists = playlistsSnap.documents.mapNotNull { it.toObject(Playlist::class.java) }

                val userDoc = firestore.collection("users").document(uid).get().await()
                val savedAlbumIds = (userDoc.get("savedAlbums") as? List<*>)?.map { it.toString() } ?: emptyList()
                val followingIds = (userDoc.get("followingArtists") as? List<*>)?.map { it.toString() } ?: emptyList()

                val albums = if (savedAlbumIds.isNotEmpty()) {
                    firestore.collection("albums").whereIn("id", savedAlbumIds.take(10)).get().await()
                        .documents.mapNotNull { it.toObject(Album::class.java) }
                } else emptyList()

                val artists = if (followingIds.isNotEmpty()) {
                    firestore.collection("artists").whereIn("id", followingIds.take(10)).get().await()
                        .documents.mapNotNull { it.toObject(Artist::class.java) }
                } else emptyList()

                _uiState.value = _uiState.value.copy(
                    playlists = playlists, albums = albums, artists = artists, isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun createPlaylist(name: String) {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                val ref = firestore.collection("playlists").document()
                val playlist = Playlist(
                    id = ref.id,
                    name = name,
                    ownerId = uid,
                    ownerName = auth.currentUser?.displayName ?: "User"
                )
                ref.set(playlist).await()
                loadLibrary()
            } catch (_: Exception) {}
        }
    }
}
