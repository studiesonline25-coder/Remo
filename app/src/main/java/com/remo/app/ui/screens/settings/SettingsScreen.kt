package com.remo.app.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.remo.app.ui.theme.*

@Composable
fun SettingsScreen(
    navController: NavHostController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showQualityMenu by remember { mutableStateOf(false) }
    val qualityOptions = listOf("Low", "Normal", "High", "Very High")

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
            Text("Settings", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = RemoWhite)
        }

        HorizontalDivider(color = RemoSurfaceVariant, thickness = 0.5.dp)

        // Account
        SettingsSectionHeader("Account")
        SettingsRow(title = "Account", subtitle = "Manage your Remo account") {}

        HorizontalDivider(color = RemoSurfaceVariant, thickness = 0.5.dp)

        // Notifications
        SettingsSectionHeader("Notifications")
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Notifications", style = MaterialTheme.typography.bodyLarge, color = RemoWhite)
                Text("Receive push notifications", style = MaterialTheme.typography.bodySmall, color = RemoOnSurfaceVariant)
            }
            Switch(
                checked = uiState.notificationsEnabled,
                onCheckedChange = { viewModel.toggleNotifications() },
                colors = SwitchDefaults.colors(checkedThumbColor = RemoBlack, checkedTrackColor = RemoGreen)
            )
        }

        HorizontalDivider(color = RemoSurfaceVariant, thickness = 0.5.dp)

        // Audio Quality
        SettingsSectionHeader("Audio Quality")
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Streaming quality", style = MaterialTheme.typography.bodyLarge, color = RemoWhite)
                Text(uiState.audioQuality, style = MaterialTheme.typography.bodySmall, color = RemoOnSurfaceVariant)
            }
            Box {
                TextButton(onClick = { showQualityMenu = true }) {
                    Text(uiState.audioQuality, color = RemoGreen, fontWeight = FontWeight.Bold)
                }
                DropdownMenu(
                    expanded = showQualityMenu,
                    onDismissRequest = { showQualityMenu = false },
                    modifier = Modifier.background(RemoSurface)
                ) {
                    qualityOptions.forEach { quality ->
                        DropdownMenuItem(
                            text = { Text(quality, color = RemoWhite) },
                            onClick = {
                                viewModel.setAudioQuality(quality)
                                showQualityMenu = false
                            }
                        )
                    }
                }
            }
        }

        HorizontalDivider(color = RemoSurfaceVariant, thickness = 0.5.dp)

        // Crossfade
        SettingsSectionHeader("Crossfade")
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Crossfade", style = MaterialTheme.typography.bodyLarge, color = RemoWhite)
                Text("${uiState.crossfadeDuration}s", style = MaterialTheme.typography.bodyMedium, color = RemoGreen, fontWeight = FontWeight.Bold)
            }
            Slider(
                value = uiState.crossfadeDuration.toFloat(),
                onValueChange = { viewModel.setCrossfade(it.toInt()) },
                valueRange = 0f..12f,
                steps = 11,
                colors = SliderDefaults.colors(thumbColor = RemoGreen, activeTrackColor = RemoGreen, inactiveTrackColor = RemoGray)
            )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("0s", style = MaterialTheme.typography.bodySmall, color = RemoOnSurfaceVariant)
                Text("12s", style = MaterialTheme.typography.bodySmall, color = RemoOnSurfaceVariant)
            }
        }

        HorizontalDivider(color = RemoSurfaceVariant, thickness = 0.5.dp)

        // About
        SettingsSectionHeader("About")
        SettingsRow(title = "Version", subtitle = "Remo 1.0.0") {}
        SettingsRow(title = "Terms of Service", subtitle = null) {}
        SettingsRow(title = "Privacy Policy", subtitle = null) {}

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun SettingsSectionHeader(title: String) {
    Text(
        text = title.uppercase(),
        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
        color = RemoOnSurfaceVariant,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
    )
}

@Composable
fun SettingsRow(title: String, subtitle: String?, onClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Text(title, style = MaterialTheme.typography.bodyLarge, color = RemoWhite)
        if (subtitle != null) {
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = RemoOnSurfaceVariant)
        }
    }
}
