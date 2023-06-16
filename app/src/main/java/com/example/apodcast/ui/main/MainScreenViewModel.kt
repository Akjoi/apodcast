package com.example.apodcast.ui.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apodcast.appComponent
import com.example.apodcast.data.player.PlayerRepository
import com.example.apodcast.entities.MainScreenState
import com.example.apodcast.media.LOG_TAG
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainScreenViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(
        MainScreenState(
            loading = false,
            musicList = null
        )
    )
    val uiState: StateFlow<MainScreenState> = _uiState.asStateFlow()

    @Inject
    lateinit var repo: PlayerRepository

    init {
        application.appComponent.inject(this)
        _uiState.value = _uiState.value.copy(
            loading = true
        )
        viewModelScope.launch {
            _uiState.value = MainScreenState(
                loading = false,
                musicList = repo.listAll()
            )
        }
    }

    fun trackPicked(id: String) {
        Log.i(LOG_TAG, id)
    }
}