package com.example.apodcast.media

import android.app.Activity
import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.ResultReceiver
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.media.MediaBrowserServiceCompat
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.android.exoplayer2.util.Util

const val LOG_TAG = "MusicService"
const val GET_BUFFER = "com.example.apodcast.COMMAND.GET.BUFFER"
const val BUFFER_KEY = "com.example.apodcast.ARGS.BUFFER"

class MusicService: MediaBrowserServiceCompat() {

    private var mediaSession: MediaSessionCompat? = null
    private val mediaSessionCallback = MediaSessionCallback()
    private var isForegroundService = false
    private val playerListener = PlayerEventListener()
    private var notificationManager: NotificationManager? = null
    private val uAmpAudioAttributes = AudioAttributes.Builder()
        .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
        .setUsage(C.USAGE_MEDIA)
        .build()

    private val stateBuilder: PlaybackStateCompat.Builder = PlaybackStateCompat.Builder()
        .setActions(
            PlaybackStateCompat.ACTION_PLAY
                    or PlaybackStateCompat.ACTION_STOP
                    or PlaybackStateCompat.ACTION_PAUSE
                    or PlaybackStateCompat.ACTION_PLAY_PAUSE
                    or PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                    or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
        )

    private val exoPlayer: ExoPlayer by lazy {
        ExoPlayer.Builder(this).build().apply {
            setAudioAttributes(uAmpAudioAttributes, true)
            setHandleAudioBecomingNoisy(true)
            addListener(playerListener)
        }
    }



    override fun onCreate() {
        Log.i(LOG_TAG, "Create Service")
        super.onCreate()
        val sessionActivityPendingIntent =
            packageManager?.getLaunchIntentForPackage(packageName)?.let { sessionIntent ->
                PendingIntent.getActivity(this, 0, sessionIntent, 0)
            }

        mediaSession = MediaSessionCompat(baseContext, LOG_TAG).apply {
            setSessionActivity(sessionActivityPendingIntent)
            setPlaybackState(stateBuilder.build())
            setCallback(mediaSessionCallback)
            setSessionToken(sessionToken)
            isActive = true
        }

//        notificationManager = NotificationManager(
//            this,
//            mediaSession!!.sessionToken,
//            PlayerNotificationListener()
//        )
    }

    override fun onDestroy() {
        mediaSession?.run {
            isActive = false
            release()
        }

        exoPlayer.removeListener(playerListener)
        exoPlayer.release()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        exoPlayer.stop()
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot? {
       return BrowserRoot("123", null)
    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
        result.sendResult(mutableListOf<MediaBrowserCompat.MediaItem>())
    }

    private fun setNewState(newState: Int) {
        mediaSession?.setPlaybackState(
            stateBuilder.setState(newState, exoPlayer.currentPosition, 1f).build()
        )
    }


    private inner class MediaSessionCallback: MediaSessionCompat.Callback() {

        override fun onSeekTo(pos: Long) {
            exoPlayer.seekTo(pos)
            setNewState(PlaybackStateCompat.STATE_PLAYING)
            exoPlayer.playWhenReady = true

        }

        override fun onPlayFromUri(uri: Uri?, extras: Bundle?) {
            mediaSession?.isActive = true
            setNewState(PlaybackStateCompat.STATE_PLAYING)
            exoPlayer.setMediaItem(MediaItem.fromUri(uri!!))
            exoPlayer.prepare()
            exoPlayer.play()

        }

        override fun onCommand(command: String?, extras: Bundle?, cb: ResultReceiver?) {
            if (command == GET_BUFFER) {
                val bundle = Bundle().apply {
                    putLong(BUFFER_KEY, exoPlayer.bufferedPosition)
                }
                cb?.send(Activity.RESULT_OK,bundle)
            }
        }

        override fun onPlay() {

//            MusicRepository.Track track = musicRepository.getCurrent();
//
//            // Заполняем данные о треке
//            MediaMetadataCompat metadata = metadataBuilder
//                    .putBitmap(MediaMetadataCompat.METADATA_KEY_ART,
//                BitmapFactory.decodeResource(getResources(), track.getBitmapResId()));
//            .putString(MediaMetadataCompat.METADATA_KEY_TITLE, track.getTitle());
//            .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, track.getArtist());
//            .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, track.getArtist());
//            .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, track.getDuration())
//                .build();
//            mediaSession.setMetadata(metadata);
            mediaSession?.isActive = true
            mediaSession?.setPlaybackState(
                stateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 1f).build()
            )
//            // Загружаем URL аудио-файла в ExoPlayer
//            prepareToPlay(track.getUri());
//
//            // Запускаем воспроизведение
//            exoPlayer.setPlayWhenReady(true);
        }

        override fun onPause() {
            exoPlayer.playWhenReady = false
            setNewState(PlaybackStateCompat.STATE_PAUSED)
        }

        override fun onStop() {
            exoPlayer.playWhenReady = false
            // since there we are not main player
            mediaSession!!.isActive = false
            setNewState(PlaybackStateCompat.STATE_STOPPED)
        }
    }

    private inner class PlayerEventListener : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            when (playbackState) {
                Player.STATE_BUFFERING,
                Player.STATE_READY -> {
//                    notificationManager.showNotificationForPlayer(exoPlayer)
//                    if (playbackState == Player.STATE_READY) {
//
////                        if (!playWhenReady) {
////                            // If playback is paused we remove the foreground state which allows the
////                            // notification to be dismissed. An alternative would be to provide a
////                            // "close" button in the notification which stops playback and clears
////                            // the notification.
////                            stopForeground(false)
////                            isForegroundService = false
////                        }
//                    }
                }
                else -> {
//                    notificationManager.hideNotification()
                }
            }
        }
    }

    /**
     * Listen for notification events.
     */
    private inner class PlayerNotificationListener :
        PlayerNotificationManager.NotificationListener {
        override fun onNotificationPosted(
            notificationId: Int,
            notification: Notification,
            ongoing: Boolean
        ) {
            if (ongoing && !isForegroundService) {
                ContextCompat.startForegroundService(
                    applicationContext,
                    Intent(applicationContext, this@MusicService.javaClass)
                )
                startForeground(notificationId, notification)
                isForegroundService = true
            }
        }

        override fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean) {
            // This API is deprecated in 33 sdk. So there`s no necessary to change it yet
            stopForeground(true)
            isForegroundService = false
            stopSelf()
        }
    }
}