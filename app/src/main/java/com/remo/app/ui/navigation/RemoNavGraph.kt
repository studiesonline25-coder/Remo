package com.remo.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.remo.app.ui.components.RemoBottomBar
import com.remo.app.ui.components.MiniPlayer
import com.remo.app.ui.screens.album.AlbumScreen
import com.remo.app.ui.screens.artist.ArtistScreen
import com.remo.app.ui.screens.auth.LoginScreen
import com.remo.app.ui.screens.auth.RegisterScreen
import com.remo.app.ui.screens.auth.SplashScreen
import com.remo.app.ui.screens.home.HomeScreen
import com.remo.app.ui.screens.library.LibraryScreen
import com.remo.app.ui.screens.player.PlayerScreen
import com.remo.app.ui.screens.playlist.PlaylistScreen
import com.remo.app.ui.screens.profile.ProfileScreen
import com.remo.app.ui.screens.search.SearchScreen
import com.remo.app.ui.screens.settings.SettingsScreen

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object Search : Screen("search")
    object Library : Screen("library")
    object Player : Screen("player")
    object Profile : Screen("profile")
    object Settings : Screen("settings")
    object Playlist : Screen("playlist/{playlistId}") {
        fun createRoute(playlistId: String) = "playlist/$playlistId"
    }
    object Artist : Screen("artist/{artistId}") {
        fun createRoute(artistId: String) = "artist/$artistId"
    }
    object Album : Screen("album/{albumId}") {
        fun createRoute(albumId: String) = "album/$albumId"
    }
}

val bottomNavScreens = listOf(Screen.Home, Screen.Search, Screen.Library)

@Composable
fun RemoNavGraph() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in bottomNavScreens.map { it.route }
    val showMiniPlayer = currentRoute in bottomNavScreens.map { it.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                RemoBottomBar(navController = navController, currentRoute = currentRoute)
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Splash.route) {
                SplashScreen(navController = navController)
            }
            composable(Screen.Login.route) {
                LoginScreen(navController = navController)
            }
            composable(Screen.Register.route) {
                RegisterScreen(navController = navController)
            }
            composable(Screen.Home.route) {
                HomeScreen(navController = navController)
            }
            composable(Screen.Search.route) {
                SearchScreen(navController = navController)
            }
            composable(Screen.Library.route) {
                LibraryScreen(navController = navController)
            }
            composable(Screen.Player.route) {
                PlayerScreen(navController = navController)
            }
            composable(Screen.Profile.route) {
                ProfileScreen(navController = navController)
            }
            composable(Screen.Settings.route) {
                SettingsScreen(navController = navController)
            }
            composable(
                route = Screen.Playlist.route,
                arguments = listOf(navArgument("playlistId") { type = NavType.StringType })
            ) { backStackEntry ->
                PlaylistScreen(
                    navController = navController,
                    playlistId = backStackEntry.arguments?.getString("playlistId") ?: ""
                )
            }
            composable(
                route = Screen.Artist.route,
                arguments = listOf(navArgument("artistId") { type = NavType.StringType })
            ) { backStackEntry ->
                ArtistScreen(
                    navController = navController,
                    artistId = backStackEntry.arguments?.getString("artistId") ?: ""
                )
            }
            composable(
                route = Screen.Album.route,
                arguments = listOf(navArgument("albumId") { type = NavType.StringType })
            ) { backStackEntry ->
                AlbumScreen(
                    navController = navController,
                    albumId = backStackEntry.arguments?.getString("albumId") ?: ""
                )
            }
        }
    }
}
