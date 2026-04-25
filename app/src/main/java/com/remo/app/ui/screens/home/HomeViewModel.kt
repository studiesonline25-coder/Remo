package com.remo.app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.remo.app.data.models.Album
import com.remo.app.data.models.Playlist
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import javax.inject.Inject

data class HomeUiState(
    val userName: String = "",
    val recentlyPlayed: List<Playlist> = emptyList(),
    val featuredAlbums: List<Album> = emptyList(),
    val madeForYou: List<Playlist> = emptyList(),
    val isLoading: Boolean = false
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHomeData()
    }

    private fun loadHomeData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val uid = auth.currentUser?.uid ?: return@launch
                val userDoc = firestore.collection("users").document(uid).get().await()
                val userName = userDoc.getString("displayName") ?: "User"

                val albumsSnapshot = firestore.collection("albums").limit(10).get().await()
                val albums = albumsSnapshot.documents.mapNotNull { it.toObject(Album::class.java) }

                val playlistsSnapshot = firestore.collection("playlists").limit(10).get().await()
                val playlists = playlistsSnapshot.documents.mapNotNull { it.toObject(Playlist::class.java) }

                _uiState.value = _uiState.value.copy(
                    userName = userName,
                    featuredAlbums = albums,
                    madeForYou = playlists,
                    recentlyPlayed = playlists.take(5),
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun getTimeOfDay(): String {
        return when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
            in 0..11 -> "Morning"
            in 12..16 -> "Afternoon"
            else -> "Evening"
        }
    }
}
