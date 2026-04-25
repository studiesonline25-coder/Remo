package com.remo.app.ui.screens.settings

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class SettingsUiState(
    val notificationsEnabled: Boolean = true,
    val audioQuality: String = "High",
    val crossfadeDuration: Int = 0
)

@HiltViewModel
class SettingsViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun toggleNotifications() {
        _uiState.value = _uiState.value.copy(notificationsEnabled = !_uiState.value.notificationsEnabled)
    }

    fun setAudioQuality(quality: String) {
        _uiState.value = _uiState.value.copy(audioQuality = quality)
    }

    fun setCrossfade(seconds: Int) {
        _uiState.value = _uiState.value.copy(crossfadeDuration = seconds)
    }
}
