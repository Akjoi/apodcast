package com.example.apodcast

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.apodcast.ui.main.MainScreen
import com.example.apodcast.ui.main.MainScreenViewModel

@Composable
fun ApodcastNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    innerPadding: PaddingValues
) {
    NavHost(
        navController = navController, startDestination = Destination.Main.route,
        modifier = Modifier
            .padding(innerPadding)
            .then(modifier)
    ) {
        composable(route = Destination.Main.route) {
            val viewModel: MainScreenViewModel = viewModel()
            MainScreen(viewModel.uiState)
        }
        composable(route = Destination.TrackPage.route) {}
        composable(route = Destination.NowPlaying.route) {}
    }
}