package com.example.apodcast.ui.player

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.media.session.PlaybackState
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.ResultReceiver
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import com.example.apodcast.R
import com.example.apodcast.extensions.currentPlayBackPosition
import com.example.apodcast.extensions.isPlaying
import com.example.apodcast.media.BUFFER_KEY
import com.example.apodcast.media.GET_BUFFER
import com.example.apodcast.media.LOG_TAG
import com.example.apodcast.media.MusicService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.floor


typealias CommandCallback =  ((Int, Bundle?) -> Unit)
private const val POSITION_UPDATE_INTERVAL_MILLIS = 100L

val EMPTY_PLAYBACK_STATE: PlaybackStateCompat = PlaybackStateCompat.Builder()
    .setState(PlaybackStateCompat.STATE_NONE, 0, 0f)
    .build()

class PlayerControllerViewModel(application: Application) : AndroidViewModel(application) {


    companion object {
        /**
         * Utility method to convert milliseconds to a display of minutes and seconds
         */
        fun timestampToMSS(position: Long): String {
            val totalSeconds = floor(position / 1E3).toInt()
            val minutes = totalSeconds / 60
            val remainingSeconds = totalSeconds - (minutes * 60)
            return if (position < 0) "--:--"
            else "%d:%02d".format(minutes, remainingSeconds)
        }
    }

    private var mediaBrowser: MediaBrowserCompat
    private lateinit var mediaController: MediaControllerCompat
    private val mediaBrowserConnectionCallback = MediaBrowserConnectionCallback()
    private val app = getApplication<Application>()

    private var playBackState: PlaybackStateCompat = EMPTY_PLAYBACK_STATE

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _curPos = MutableStateFlow(0L)
    val curPos: StateFlow<Long> = _curPos.asStateFlow()

    private val _bufPos = MutableStateFlow(0L)
    val bufPos: StateFlow<Long> = _bufPos.asStateFlow()

    private val handler = Handler(Looper.getMainLooper())
    private var needBufferUpdate = false
    private var needUpdate = false


    init {
        mediaBrowser = MediaBrowserCompat(
            app,
            ComponentName(app, MusicService::class.java),
            mediaBrowserConnectionCallback,
            null // optional Bundle
        ).apply { connect() }

    }

    override fun onCleared() {
        super.onCleared()
        mediaController.unregisterCallback(controllerCallback)
        mediaBrowser.disconnect()
    }

    fun changePlayerState() {
        updateTrackInfo()
        if (_isPlaying.value) {
            mediaController.transportControls.pause()
            needBufferUpdate = false
        }
        else {
            mediaController.transportControls.playFromUri(
                app.resources.getString(R.string.url).toUri(), null
            )
            needBufferUpdate = true
        }
    }

    private val resultCallback: CommandCallback =  { code: Int, bundle: Bundle? ->
        _bufPos.value = bundle?.getLong(BUFFER_KEY)!!

    }

    fun seekTo(position: Long) {
        Log.i(LOG_TAG, position.toString())
        mediaController.transportControls.seekTo(position)
    }

    private fun updateTrackInfo(): Boolean = handler.postDelayed( {
        val position = playBackState.currentPlayBackPosition
        if (_curPos.value != position)
            _curPos.value = position
        if (needBufferUpdate)
            sendCommand(
                GET_BUFFER,
                Bundle.EMPTY,
                resultCallback
            )
        updateTrackInfo()
    }, POSITION_UPDATE_INTERVAL_MILLIS)

    private fun sendCommand(
        command: String,
        parameters: Bundle?,
        resultCallback: ((Int, Bundle?) -> Unit)
    ) = if (mediaBrowser.isConnected) {
        mediaController.sendCommand(command, parameters, object : ResultReceiver(Handler(Looper.getMainLooper())) {
            override fun onReceiveResult(resultCode: Int, resultData: Bundle?) {
                resultCallback(resultCode, resultData)
            }
        })
        true
    } else {
        false
    }


    private inner class MediaBrowserConnectionCallback() :
        MediaBrowserCompat.ConnectionCallback() {
        /**
         * Invoked after [MediaBrowserCompat.connect] when the request has successfully
         * completed.
         */
        override fun onConnected() {
            // Get a MediaController for the MediaSession.
            mediaController = MediaControllerCompat(getApplication(), mediaBrowser.sessionToken).apply {
                registerCallback(controllerCallback)
            }

//            // Get the token for the MediaSession
//            mediaBrowser.sessionToken.also { token ->
//
//                // Create a MediaControllerCompat
//                val mediaController = MediaControllerCompat(
//                    this@MainActivity, // Context
//                    token
//                )
//
//                // Save the controller
//                MediaControllerCompat.setMediaController(this@MainActivity, mediaController)
//            }

        }

        /**
         * Invoked when the client is disconnected from the media browser.
         */
        override fun onConnectionSuspended() {
            Log.i(LOG_TAG, "Connection Suspended")
        }

        /**
         * Invoked when the connection to the media browser failed.
         */
        override fun onConnectionFailed() {
            Log.i(LOG_TAG, "Connection Failed")
        }
    }

    private var controllerCallback = object : MediaControllerCompat.Callback() {

        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            Log.i(LOG_TAG, metadata.toString())
        }

        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            playBackState = state ?: EMPTY_PLAYBACK_STATE
            _isPlaying.value = state!!.isPlaying
        }
    }
}
