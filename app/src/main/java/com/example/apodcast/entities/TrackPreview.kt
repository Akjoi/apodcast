package com.example.apodcast.entities

import com.google.gson.annotations.SerializedName

data class TrackPreview(
    @SerializedName("id")
    val id: String,

    @SerializedName("title")
    val title: String,

    @SerializedName("album")
    val album: String,

    @SerializedName("artist")
    val artist: String,

    @SerializedName("source")
    val source: String,

    @SerializedName("image")
    val image: String,

    @SerializedName("duration")
    val duration: Int,
)


data class TrackResponse(

    @SerializedName("music")
    val music: List<TrackPreview>

)

