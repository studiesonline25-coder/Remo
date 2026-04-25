package com.remo.app.ui.screens.artist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.remo.app.ui.components.SongItem
import com.remo.app.ui.navigation.Screen
import com.remo.app.ui.screens.home.AlbumCard
import com.remo.app.ui.screens.home.SectionTitle
import com.remo.app.ui.theme.*

@Composable
fun ArtistScreen(
    navController: NavHostController,
    artistId: String,
    viewModel: ArtistViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(artistId) { viewModel.loadArtist(artistId) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(RemoDark),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        item {
            Box(modifier = Modifier.fillMaxWidth().height(280.dp)) {
                AsyncImage(
                    model = uiState.artist?.headerImageUrl ?: uiState.artist?.imageUrl,
                    contentDescription = uiState.artist?.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.statusBarsPadding().padding(8.dp)
                ) {
                    Icon(Icons.Filled.ArrowBack, "Back", tint = RemoWhite)
                }
                Column(
                    modifier = Modifier.align(Alignment.BottomStart).padding(16.dp)
                ) {
                    Text(
                        text = uiState.artist?.name ?: "",
                        style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = RemoWhite
                    )
                    Text(
                        text = "${uiState.artist?.monthlyListeners?.let { formatCount(it) } ?: "0"} monthly listeners",
                        style = MaterialTheme.typography.bodyMedium,
                        color = RemoWhite
                    )
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { viewModel.toggleFollow() },
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (uiState.isFollowing) RemoSurfaceVariant else RemoGreen,
                        contentColor = if (uiState.isFollowing) RemoWhite else RemoBlack
                    )
                ) {
                    Text(if (uiState.isFollowing) "Following" else "Follow", fontWeight = FontWeight.Bold)
                }
            }
        }

        item { SectionTitle("Popular") }

        items(uiState.popularSongs) { song ->
            SongItem(song = song, onSongClick = { navController.navigate(Screen.Player.route) })
        }

        if (uiState.albums.isNotEmpty()) {
            item { SectionTitle("Albums") }
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.albums) { album ->
                        AlbumCard(album = album, onClick = {
                            navController.navigate(Screen.Album.createRoute(album.id))
                        })
                    }
                }
            }
        }

        if (!uiState.artist?.bio.isNullOrBlank()) {
            item { SectionTitle("About") }
            item {
                Text(
                    text = uiState.artist!!.bio,
                    style = MaterialTheme.typography.bodyMedium,
                    color = RemoOnSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }
        }
    }
}

fun formatCount(count: Long): String = when {
    count >= 1_000_000 -> "%.1fM".format(count / 1_000_000.0)
    count >= 1_000 -> "%.1fK".format(count / 1_000.0)
    else -> count.toString()
}
