package com.remo.app.ui.screens.album

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import com.remo.app.data.models.Song
import com.remo.app.ui.navigation.Screen
import com.remo.app.ui.theme.*

@Composable
fun AlbumScreen(
    navController: NavHostController,
    albumId: String,
    viewModel: AlbumViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(albumId) { viewModel.loadAlbum(albumId) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(RemoDark),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        item {
            Box(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.fillMaxWidth().background(RemoSurface).padding(bottom = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.statusBarsPadding())
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Filled.ArrowBack, "Back", tint = RemoWhite)
                        }
                    }
                    AsyncImage(
                        model = uiState.album?.coverArtUrl,
                        contentDescription = uiState.album?.title,
                        modifier = Modifier.size(200.dp).clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = uiState.album?.title ?: "",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = RemoWhite
                    )
                    Text(
                        text = uiState.album?.artistName ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = RemoOnSurfaceVariant
                    )
                    Text(
                        text = "${uiState.album?.type?.replaceFirstChar { it.uppercase() } ?: "Album"} • ${uiState.album?.releaseDate?.take(4) ?: ""}",
                        style = MaterialTheme.typography.bodySmall,
                        color = RemoOnSurfaceVariant
                    )
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
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
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = { viewModel.toggleSave() }) {
                    Icon(
                        imageVector = if (uiState.isSaved) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Save",
                        tint = if (uiState.isSaved) RemoGreen else RemoOnSurfaceVariant,
                        modifier = Modifier.size(28.dp)
                    )
                }
                IconButton(onClick = {}) {
                    Icon(Icons.Filled.MoreVert, "More", tint = RemoOnSurfaceVariant)
                }
            }
        }

        itemsIndexed(uiState.songs) { index, song ->
            AlbumSongItem(
                number = index + 1,
                song = song,
                onClick = { navController.navigate(Screen.Player.route) }
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

@Composable
fun AlbumSongItem(number: Int, song: Song, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = number.toString(),
            style = MaterialTheme.typography.bodyMedium,
            color = RemoOnSurfaceVariant,
            modifier = Modifier.width(28.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(song.title, style = MaterialTheme.typography.bodyLarge, color = RemoWhite, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(song.artistName, style = MaterialTheme.typography.bodySmall, maxLines = 1)
        }
        IconButton(onClick = {}) {
            Icon(Icons.Filled.MoreVert, "More", tint = RemoOnSurfaceVariant, modifier = Modifier.size(20.dp))
        }
    }
}
