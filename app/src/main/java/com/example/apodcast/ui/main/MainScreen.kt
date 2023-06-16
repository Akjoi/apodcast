package com.example.apodcast.ui.main


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.example.apodcast.entities.MainScreenState
import com.example.apodcast.entities.TrackPreview
import kotlinx.coroutines.flow.StateFlow

@Composable
fun MainScreen(state: StateFlow<MainScreenState>) {
    val screenState by state.collectAsState()


    if (screenState.loading) {
        CircularProgressIndicator()
        return
    }
    if (screenState.musicList != null) {
        TopicRow(
            screenState.musicList!!, color = Color.Yellow
        )
        return
    }

    Text(text = "Нет треков")

}

@Composable
fun TopicRow(trackList: List<TrackPreview>, color: Color) {
    LazyRow {
        items(trackList) { item: TrackPreview ->
            TopicItem(item = item, color = color)
        }
    }
}

@Composable
fun TopicItem(item: TrackPreview, color: Color) {
    Card(
        modifier = Modifier
            .width(256.dp)
            .padding(top = 8.dp, start = 4.dp, bottom = 16.dp)
    ) {

        Column(
            modifier = Modifier
                .background(color = color)
                .padding(6.dp)
        ) {
            Image(painter = rememberAsyncImagePainter(item.image), contentDescription = "", modifier = Modifier.fillMaxWidth())
            Text(text = item.title)
            Text(text = item.artist)
        }
    }
}
