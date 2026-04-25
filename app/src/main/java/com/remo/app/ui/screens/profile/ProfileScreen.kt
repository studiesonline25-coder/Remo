package com.remo.app.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
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
import com.remo.app.ui.navigation.Screen
import com.remo.app.ui.theme.*

@Composable
fun ProfileScreen(
    navController: NavHostController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showEditDialog by remember { mutableStateOf(false) }
    var editName by remember { mutableStateOf("") }

    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Edit Name", color = RemoWhite) },
            text = {
                OutlinedTextField(
                    value = editName,
                    onValueChange = { editName = it },
                    label = { Text("Display name") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = RemoGreen, unfocusedBorderColor = RemoGray,
                        focusedTextColor = RemoWhite, unfocusedTextColor = RemoWhite, cursorColor = RemoGreen
                    )
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.updateDisplayName(editName)
                    showEditDialog = false
                }) { Text("Save", color = RemoGreen) }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) { Text("Cancel", color = RemoOnSurfaceVariant) }
            },
            containerColor = RemoSurface
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RemoDark)
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Filled.ArrowBack, "Back", tint = RemoWhite)
            }
            Text("Profile", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = RemoWhite)
        }

        // Avatar
        Box(modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp), contentAlignment = Alignment.Center) {
            if (!uiState.user?.profileImageUrl.isNullOrBlank()) {
                AsyncImage(
                    model = uiState.user!!.profileImageUrl,
                    contentDescription = "Avatar",
                    modifier = Modifier.size(100.dp).clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier.size(100.dp).clip(CircleShape).background(RemoSurfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Person, "Avatar", tint = RemoOnSurfaceVariant, modifier = Modifier.size(60.dp))
                }
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = uiState.user?.displayName ?: "User",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = RemoWhite
            )
            Text(
                text = uiState.user?.email ?: "",
                style = MaterialTheme.typography.bodyMedium,
                color = RemoOnSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Stats
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(label = "Playlists", value = uiState.user?.playlists?.size?.toString() ?: "0")
            StatItem(label = "Liked Songs", value = uiState.user?.likedSongs?.size?.toString() ?: "0")
            StatItem(label = "Following", value = uiState.user?.followingArtists?.size?.toString() ?: "0")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Buttons
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
            OutlinedButton(
                onClick = {
                    editName = uiState.user?.displayName ?: ""
                    showEditDialog = true
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = RemoWhite)
            ) {
                Text("Edit Profile", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    viewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = RemoError)
            ) {
                Text("Log Out", fontWeight = FontWeight.Bold, color = RemoWhite)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = RemoWhite)
        Text(label, style = MaterialTheme.typography.bodySmall, color = RemoOnSurfaceVariant)
    }
}
