package com.remo.app.ui.screens.player

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.remo.app.ui.theme.*

@Composable
fun PlayerScreen(
    navController: NavHostController,
    viewModel: PlayerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val song = uiState.currentSong

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RemoDark)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top bar
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Filled.KeyboardArrowDown, "Close", tint = RemoWhite, modifier = Modifier.size(32.dp))
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("PLAYING FROM PLAYLIST", style = MaterialTheme.typography.labelMedium, color = RemoOnSurfaceVariant)
                Text(song?.albumName ?: "Remo", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold), color = RemoWhite)
            }
            IconButton(onClick = {}) {
                Icon(Icons.Filled.MoreVert, "More", tint = RemoWhite)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Album art
        AsyncImage(
            model = song?.albumArtUrl,
            contentDescription = song?.title,
            modifier = Modifier
                .size(300.dp)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Song info + like
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = song?.title ?: "No song playing",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = RemoWhite,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = song?.artistName ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = RemoOnSurfaceVariant,
                    maxLines = 1
                )
            }
            IconButton(onClick = { viewModel.toggleLike() }) {
                Icon(
                    imageVector = if (uiState.isLiked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = "Like",
                    tint = if (uiState.isLiked) RemoGreen else RemoOnSurfaceVariant,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Seek bar
        Slider(
            value = uiState.progress,
            onValueChange = { viewModel.seekTo(it) },
            modifier = Modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(
                thumbColor = RemoWhite,
                activeTrackColor = RemoWhite,
                inactiveTrackColor = RemoGray
            )
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(viewModel.formatTime(uiState.currentPosition), style = MaterialTheme.typography.bodySmall, color = RemoOnSurfaceVariant)
            Text(viewModel.formatTime(uiState.duration), style = MaterialTheme.typography.bodySmall, color = RemoOnSurfaceVariant)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = { viewModel.toggleShuffle() }) {
                Icon(
                    Icons.Filled.Shuffle, "Shuffle",
                    tint = if (uiState.isShuffled) RemoGreen else RemoOnSurfaceVariant,
                    modifier = Modifier.size(24.dp)
                )
            }
            IconButton(onClick = { viewModel.skipPrevious() }) {
                Icon(Icons.Filled.SkipPrevious, "Previous", tint = RemoWhite, modifier = Modifier.size(36.dp))
            }
            IconButton(
                onClick = { viewModel.playPause() },
                modifier = Modifier
                    .size(64.dp)
                    .background(RemoWhite, RoundedCornerShape(32.dp))
            ) {
                Icon(
                    imageVector = if (uiState.isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    contentDescription = "Play/Pause",
                    tint = RemoDark,
                    modifier = Modifier.size(36.dp)
                )
            }
            IconButton(onClick = { viewModel.skipNext() }) {
                Icon(Icons.Filled.SkipNext, "Next", tint = RemoWhite, modifier = Modifier.size(36.dp))
            }
            IconButton(onClick = { viewModel.toggleRepeat() }) {
                Icon(
                    imageVector = if (uiState.repeatMode == 2) Icons.Filled.RepeatOne else Icons.Filled.Repeat,
                    contentDescription = "Repeat",
                    tint = if (uiState.repeatMode != 0) RemoGreen else RemoOnSurfaceVariant,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Volume row
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.VolumeDown, "Volume", tint = RemoOnSurfaceVariant, modifier = Modifier.size(20.dp))
            Slider(
                value = 0.7f,
                onValueChange = {},
                modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                colors = SliderDefaults.colors(
                    thumbColor = RemoWhite,
                    activeTrackColor = RemoWhite,
                    inactiveTrackColor = RemoGray
                )
            )
            Icon(Icons.Filled.VolumeUp, "Volume", tint = RemoOnSurfaceVariant, modifier = Modifier.size(20.dp))
        }
    }
}
