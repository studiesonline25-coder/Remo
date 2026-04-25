package com.remo.app.ui.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.remo.app.ui.components.SongItem
import com.remo.app.ui.navigation.Screen
import com.remo.app.ui.theme.*

val genreColors = listOf(
    Color(0xFF1E3264), Color(0xFF8D67AB), Color(0xFFE61E32),
    Color(0xFFBA5D07), Color(0xFF148A08), Color(0xFFE8115B),
    Color(0xFF509BF5), Color(0xFF27856A), Color(0xFF1E3264),
    Color(0xFF8D67AB), Color(0xFFE61E32), Color(0xFFBA5D07)
)

@Composable
fun SearchScreen(
    navController: NavHostController,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RemoDark)
            .statusBarsPadding()
    ) {
        Text(
            text = "Search",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = RemoWhite,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )

        // Search bar
        OutlinedTextField(
            value = uiState.query,
            onValueChange = { viewModel.onQueryChange(it) },
            placeholder = { Text("Songs, artists, albums", color = RemoOnSurfaceVariant) },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = RemoOnSurfaceVariant) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = RemoGreen,
                unfocusedBorderColor = RemoGray,
                focusedTextColor = RemoWhite,
                unfocusedTextColor = RemoWhite,
                cursorColor = RemoGreen,
                unfocusedContainerColor = RemoSurfaceVariant,
                focusedContainerColor = RemoSurfaceVariant
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.query.isEmpty()) {
            // Genre grid
            Text(
                text = "Browse all",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = RemoWhite,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            val chunkedGenres = uiState.genres.chunked(2)
            LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)) {
                items(chunkedGenres) { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        row.forEachIndexed { idx, genre ->
                            val colorIndex = (uiState.genres.indexOf(genre)) % genreColors.size
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(56.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(genreColors[colorIndex])
                                    .clickable { viewModel.onQueryChange(genre) },
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Text(
                                    text = genre,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = RemoWhite,
                                    modifier = Modifier.padding(start = 12.dp)
                                )
                            }
                        }
                        if (row.size == 1) Spacer(modifier = Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        } else {
            // Search results
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = RemoGreen)
                }
            } else if (uiState.results.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No results for \"${uiState.query}\"", color = RemoOnSurfaceVariant)
                }
            } else {
                LazyColumn {
                    items(uiState.results) { song ->
                        SongItem(
                            song = song,
                            onSongClick = { navController.navigate(Screen.Player.route) }
                        )
                    }
                }
            }
        }
    }
}
