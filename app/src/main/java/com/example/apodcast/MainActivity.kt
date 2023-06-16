package com.example.apodcast

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.example.apodcast.media.LOG_TAG
import com.example.apodcast.ui.player.PlayerControllerViewModel
import com.example.apodcast.ui.theme.ApodcastTheme

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: PlayerControllerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(PlayerControllerViewModel::class.java)
        setContent {
            ApodcastApp()
        }
    }

}

@Composable
fun ApodcastApp() {
    ApodcastTheme {
        val navController = rememberNavController()
        Scaffold(
            bottomBar = {
                Box(modifier = Modifier.height(48.dp)) {
                    Text(text = "Тут навигация")
                }
            }
        ) { paddingValues ->
            ApodcastNavHost(navController = navController, innerPadding = paddingValues)
        }
    }
}