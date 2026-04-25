package com.remo.app.ui.screens.album

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.remo.app.data.models.Album
import com.remo.app.data.models.Song
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

data class AlbumUiState(
    val album: Album? = null,
    val songs: List<Song> = emptyList(),
    val isSaved: Boolean = false,
    val isLoading: Boolean = false
)

@HiltViewModel
class AlbumViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _uiState = MutableStateFlow(AlbumUiState())
    val uiState: StateFlow<AlbumUiState> = _uiState.asStateFlow()

    fun loadAlbum(id: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val doc = firestore.collection("albums").document(id).get().await()
                val album = doc.toObject(Album::class.java) ?: return@launch

                val songs = if (album.songs.isNotEmpty()) {
                    firestore.collection("songs").whereIn("id", album.songs.take(10)).get().await()
                        .documents.mapNotNull { it.toObject(Song::class.java) }
                } else emptyList()

                val uid = auth.currentUser?.uid
                val isSaved = if (uid != null) {
                    val userDoc = firestore.collection("users").document(uid).get().await()
                    val saved = (userDoc.get("savedAlbums") as? List<*>)?.map { it.toString() } ?: emptyList()
                    id in saved
                } else false

                _uiState.value = _uiState.value.copy(album = album, songs = songs, isSaved = isSaved, isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun toggleSave() {
        val uid = auth.currentUser?.uid ?: return
        val albumId = _uiState.value.album?.id ?: return
        val nowSaved = !_uiState.value.isSaved
        _uiState.value = _uiState.value.copy(isSaved = nowSaved)
        viewModelScope.launch {
            try {
                val userRef = firestore.collection("users").document(uid)
                val doc = userRef.get().await()
                val list = (doc.get("savedAlbums") as? List<*>)?.map { it.toString() }?.toMutableList() ?: mutableListOf()
                if (nowSaved) list.add(albumId) else list.remove(albumId)
                userRef.update("savedAlbums", list).await()
            } catch (_: Exception) {}
        }
    }
}
