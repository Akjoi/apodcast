package com.example.apodcast

sealed interface Destination {
    val route: String

    object Main: Destination {
        override val route: String
            get() = "main"
    }

    object TrackPage: Destination {
        override val route: String
            get() = "track"
    }

    object NowPlaying: Destination {
        override val route: String
            get() = "now_playing"
    }
}