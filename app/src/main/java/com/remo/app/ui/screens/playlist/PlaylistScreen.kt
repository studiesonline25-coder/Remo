package com.remo.app.ui.screens.playlist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.remo.app.ui.components.SongItem
import com.remo.app.ui.navigation.Screen
import com.remo.app.ui.theme.*

@Composable
fun PlaylistScreen(
    navController: NavHostController,
    playlistId: String,
    viewModel: PlaylistViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(playlistId) { viewModel.loadPlaylist(playlistId) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(RemoDark),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        item {
            Box(modifier = Modifier.fillMaxWidth().height(300.dp)) {
                AsyncImage(
                    model = uiState.playlist?.coverArtUrl,
                    contentDescription = uiState.playlist?.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // Back button
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.statusBarsPadding().padding(8.dp)
                ) {
                    Icon(Icons.Filled.ArrowBack, "Back", tint = RemoWhite)
                }
            }
        }

        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                Text(
                    text = uiState.playlist?.name ?: "",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = RemoWhite
                )
                if (!uiState.playlist?.description.isNullOrBlank()) {
                    Text(uiState.playlist!!.description, style = MaterialTheme.typography.bodyMedium, color = RemoOnSurfaceVariant)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${uiState.playlist?.ownerName ?: ""} • ${uiState.playlist?.followerCount ?: 0} saves",
                    style = MaterialTheme.typography.bodySmall,
                    color = RemoOnSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = {},
                        modifier = Modifier.size(56.dp).background(RemoGreen, RoundedCornerShape(28.dp))
                    ) {
                        Icon(Icons.Filled.PlayArrow, "Play", tint = RemoBlack, modifier = Modifier.size(32.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    IconButton(onClick = {}) {
                        Icon(Icons.Filled.Shuffle, "Shuffle", tint = RemoOnSurfaceVariant, modifier = Modifier.size(28.dp))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedButton(
                        onClick = { viewModel.toggleFollow() },
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = if (uiState.isFollowing) RemoGreen else RemoWhite),
                        border = ButtonDefaults.outlinedButtonBorder.copy()
                    ) {
                        Text(if (uiState.isFollowing) "Following" else "Follow", fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = {}) {
                        Icon(Icons.Filled.MoreVert, "More", tint = RemoOnSurfaceVariant)
                    }
                }
            }
        }

        items(uiState.songs) { song ->
            SongItem(
                song = song,
                onSongClick = { navController.navigate(Screen.Player.route) }
            )
        }

        if (uiState.isLoading) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = RemoGreen)
                }
            }
        }
    }
}
