package com.remo.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val RemoDarkColorScheme = darkColorScheme(
    primary = RemoGreen,
    onPrimary = RemoBlack,
    secondary = RemoGreenDark,
    onSecondary = RemoWhite,
    background = RemoDark,
    onBackground = RemoWhite,
    surface = RemoSurface,
    onSurface = RemoWhite,
    surfaceVariant = RemoSurfaceVariant,
    onSurfaceVariant = RemoOnSurfaceVariant,
    error = RemoError,
    onError = RemoWhite
)

@Composable
fun RemoTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = RemoDarkColorScheme,
        typography = RemoTypography,
        content = content
    )
}
