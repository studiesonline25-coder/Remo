package com.remo.app.ui.screens.artist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.remo.app.data.models.Album
import com.remo.app.data.models.Artist
import com.remo.app.data.models.Song
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

data class ArtistUiState(
    val artist: Artist? = null,
    val popularSongs: List<Song> = emptyList(),
    val albums: List<Album> = emptyList(),
    val isFollowing: Boolean = false,
    val isLoading: Boolean = false
)

@HiltViewModel
class ArtistViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _uiState = MutableStateFlow(ArtistUiState())
    val uiState: StateFlow<ArtistUiState> = _uiState.asStateFlow()

    fun loadArtist(id: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val doc = firestore.collection("artists").document(id).get().await()
                val artist = doc.toObject(Artist::class.java) ?: return@launch

                val songs = if (artist.popularSongs.isNotEmpty()) {
                    firestore.collection("songs").whereIn("id", artist.popularSongs.take(5)).get().await()
                        .documents.mapNotNull { it.toObject(Song::class.java) }
                } else emptyList()

                val albums = if (artist.albums.isNotEmpty()) {
                    firestore.collection("albums").whereIn("id", artist.albums.take(10)).get().await()
                        .documents.mapNotNull { it.toObject(Album::class.java) }
                } else emptyList()

                val uid = auth.currentUser?.uid
                val isFollowing = if (uid != null) {
                    val userDoc = firestore.collection("users").document(uid).get().await()
                    val following = (userDoc.get("followingArtists") as? List<*>)?.map { it.toString() } ?: emptyList()
                    id in following
                } else false

                _uiState.value = _uiState.value.copy(artist = artist, popularSongs = songs, albums = albums, isFollowing = isFollowing, isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun toggleFollow() {
        val uid = auth.currentUser?.uid ?: return
        val artistId = _uiState.value.artist?.id ?: return
        val nowFollowing = !_uiState.value.isFollowing
        _uiState.value = _uiState.value.copy(isFollowing = nowFollowing)
        viewModelScope.launch {
            try {
                val userRef = firestore.collection("users").document(uid)
                val doc = userRef.get().await()
                val list = (doc.get("followingArtists") as? List<*>)?.map { it.toString() }?.toMutableList() ?: mutableListOf()
                if (nowFollowing) list.add(artistId) else list.remove(artistId)
                userRef.update("followingArtists", list).await()
            } catch (_: Exception) {}
        }
    }
}
