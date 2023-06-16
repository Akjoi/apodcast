package com.example.apodcast.entities

data class MainScreenState(
    val loading: Boolean,
    val musicList: List<TrackPreview>?
)
