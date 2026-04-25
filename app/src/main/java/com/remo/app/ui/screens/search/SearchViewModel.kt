package com.remo.app.ui.screens.search

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

data class SearchUiState(
    val query: String = "",
    val results: List<Song> = emptyList(),
    val isLoading: Boolean = false,
    val genres: List<String> = listOf(
        "Pop", "Hip-Hop", "Rock", "R&B",
        "Electronic", "Afrobeats", "Gospel", "Jazz",
        "Classical", "Country", "Reggae", "Latin"
    )
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    fun onQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(query = query)
        if (query.length >= 2) search(query) else clearResults()
    }

    private fun clearResults() {
        _uiState.value = _uiState.value.copy(results = emptyList())
    }

    private fun search(query: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val snapshot = firestore.collection("songs")
                    .orderBy("title")
                    .startAt(query)
                    .endAt(query + "\uf8ff")
                    .limit(30)
                    .get().await()
                val songs = snapshot.documents.mapNotNull { it.toObject(Song::class.java) }
                _uiState.value = _uiState.value.copy(results = songs, isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }
}
