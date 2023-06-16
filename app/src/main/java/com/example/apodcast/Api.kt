package com.example.apodcast

import com.example.apodcast.entities.TrackResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface Api {

    @GET("uamp/catalog.json")
    suspend fun musicList(): Response<TrackResponse>
}