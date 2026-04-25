package com.remo.app.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LibraryMusic
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import com.remo.app.ui.navigation.Screen
import com.remo.app.ui.theme.RemoDark
import com.remo.app.ui.theme.RemoGreen
import com.remo.app.ui.theme.RemoOnSurfaceVariant

data class BottomNavItem(
    val label: String,
    val route: String,
    val selectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val unselectedIcon: androidx.compose.ui.graphics.vector.ImageVector
)

@Composable
fun RemoBottomBar(navController: NavHostController, currentRoute: String?) {
    val items = listOf(
        BottomNavItem("Home", Screen.Home.route, Icons.Filled.Home, Icons.Outlined.Home),
        BottomNavItem("Search", Screen.Search.route, Icons.Filled.Search, Icons.Outlined.Search),
        BottomNavItem("Library", Screen.Library.route, Icons.Filled.LibraryMusic, Icons.Outlined.LibraryMusic)
    )

    NavigationBar(
        containerColor = RemoDark,
        contentColor = RemoGreen
    ) {
        items.forEach { item ->
            val selected = currentRoute == item.route
            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(Screen.Home.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Icon(
                        imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.label
                    )
                },
                label = { Text(item.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = RemoGreen,
                    selectedTextColor = RemoGreen,
                    unselectedIconColor = RemoOnSurfaceVariant,
                    unselectedTextColor = RemoOnSurfaceVariant,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}
