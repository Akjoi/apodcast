package com.example.apodcast.data.player

import com.example.apodcast.Api
import com.example.apodcast.entities.TrackPreview
import javax.inject.Inject

interface IPlayerRepository {

    suspend fun listAll(): List<TrackPreview>?

//    fun skipNext()
//
//    fun skipPrevious()
//
//    fun shuffle()
//
//    fun getCurrentTrack()
//
//    fun setCurrentTrack()

}

class PlayerRepository @Inject constructor(private val api: Api) : IPlayerRepository {

    private var musicList: List<TrackPreview>? = null
    override suspend fun listAll(): List<TrackPreview>? {
        if (musicList == null)
            musicList = api.musicList().body()?.music
        return musicList
    }

}