package com.remo.app.ui.screens.library

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import com.remo.app.data.models.Album
import com.remo.app.data.models.Artist
import com.remo.app.data.models.Playlist
import com.remo.app.ui.navigation.Screen
import com.remo.app.ui.theme.*

@Composable
fun LibraryScreen(
    navController: NavHostController,
    viewModel: LibraryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }
    var showCreateDialog by remember { mutableStateOf(false) }
    var newPlaylistName by remember { mutableStateOf("") }

    val tabs = listOf("Playlists", "Albums", "Artists")

    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = { Text("New Playlist", color = RemoWhite) },
            text = {
                OutlinedTextField(
                    value = newPlaylistName,
                    onValueChange = { newPlaylistName = it },
                    label = { Text("Playlist name") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = RemoGreen,
                        unfocusedBorderColor = RemoGray,
                        focusedTextColor = RemoWhite,
                        unfocusedTextColor = RemoWhite,
                        cursorColor = RemoGreen
                    )
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (newPlaylistName.isNotBlank()) {
                        viewModel.createPlaylist(newPlaylistName)
                        newPlaylistName = ""
                        showCreateDialog = false
                    }
                }) { Text("Create", color = RemoGreen) }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) { Text("Cancel", color = RemoOnSurfaceVariant) }
            },
            containerColor = RemoSurface
        )
    }

    Scaffold(
        containerColor = RemoDark,
        floatingActionButton = {
            if (selectedTab == 0) {
                FloatingActionButton(
                    onClick = { showCreateDialog = true },
                    containerColor = RemoGreen,
                    contentColor = RemoBlack
                ) { Icon(Icons.Filled.Add, "Create playlist") }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(RemoDark)
                .padding(padding)
                .statusBarsPadding()
        ) {
            Text(
                text = "Your Library",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = RemoWhite,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )

            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = RemoDark,
                contentColor = RemoGreen,
                indicator = { tabPositions ->
                    Box(
                        Modifier
                            .tabIndicatorOffset(tabPositions[selectedTab])
                            .height(2.dp)
                            .background(RemoGreen)
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                title,
                                color = if (selectedTab == index) RemoGreen else RemoOnSurfaceVariant,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            when (selectedTab) {
                0 -> PlaylistsTab(uiState.playlists) { navController.navigate(Screen.Playlist.createRoute(it.id)) }
                1 -> AlbumsTab(uiState.albums) { navController.navigate(Screen.Album.createRoute(it.id)) }
                2 -> ArtistsTab(uiState.artists) { navController.navigate(Screen.Artist.createRoute(it.id)) }
            }
        }
    }
}

@Composable
fun PlaylistsTab(playlists: List<Playlist>, onClick: (Playlist) -> Unit) {
    LazyColumn(contentPadding = PaddingValues(vertical = 8.dp)) {
        items(playlists) { playlist ->
            Row(
                modifier = Modifier.fillMaxWidth().clickable { onClick(playlist) }.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = playlist.coverArtUrl,
                    contentDescription = playlist.name,
                    modifier = Modifier.size(52.dp).clip(RoundedCornerShape(4.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(playlist.name, style = MaterialTheme.typography.bodyLarge, color = RemoWhite, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text("${playlist.songs.size} songs", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
        if (playlists.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    Text("No playlists yet", color = RemoOnSurfaceVariant)
                }
            }
        }
    }
}

@Composable
fun AlbumsTab(albums: List<Album>, onClick: (Album) -> Unit) {
    LazyColumn(contentPadding = PaddingValues(vertical = 8.dp)) {
        items(albums) { album ->
            Row(
                modifier = Modifier.fillMaxWidth().clickable { onClick(album) }.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = album.coverArtUrl,
                    contentDescription = album.title,
                    modifier = Modifier.size(52.dp).clip(RoundedCornerShape(4.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(album.title, style = MaterialTheme.typography.bodyLarge, color = RemoWhite, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(album.artistName, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
        if (albums.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    Text("No saved albums", color = RemoOnSurfaceVariant)
                }
            }
        }
    }
}

@Composable
fun ArtistsTab(artists: List<Artist>, onClick: (Artist) -> Unit) {
    LazyColumn(contentPadding = PaddingValues(vertical = 8.dp)) {
        items(artists) { artist ->
            Row(
                modifier = Modifier.fillMaxWidth().clickable { onClick(artist) }.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = artist.imageUrl,
                    contentDescription = artist.name,
                    modifier = Modifier.size(52.dp).clip(RoundedCornerShape(26.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(artist.name, style = MaterialTheme.typography.bodyLarge, color = RemoWhite, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text("Artist", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
        if (artists.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    Text("No followed artists", color = RemoOnSurfaceVariant)
                }
            }
        }
    }
}
