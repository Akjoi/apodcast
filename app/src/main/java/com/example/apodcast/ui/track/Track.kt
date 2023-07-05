package com.example.apodcast.ui.track

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.apodcast.entities.TrackPreview
import com.example.apodcast.ui.main.TopicItem

@Composable
fun Track(item: String) {
    Card(
        modifier = Modifier
            .width(256.dp)
            .padding(top = 8.dp, start = 4.dp, bottom = 16.dp)
    ) {

        Column(
            modifier = Modifier
                .padding(6.dp)
        ) {
//            Image(painter = rememberAsyncImagePainter(item.image), contentDescription = "", modifier = Modifier.fillMaxWidth())
            Text(text = item)
//            Text(text = item.artist)
        }
    }
}