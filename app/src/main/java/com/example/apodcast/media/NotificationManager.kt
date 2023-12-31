package com.example.apodcast.media

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import com.example.apodcast.R
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import kotlinx.coroutines.*
const val NOW_PLAYING_CHANNEL_ID = "com.example.apodcast.media.NOW_PLAYING"
const val NOW_PLAYING_NOTIFICATION_ID = 0xb338 // Arbitrary number used to identify our notification

class NotificationManager (
    context: Context,
    sessionToken: MediaSessionCompat.Token,
    notificationListener: PlayerNotificationManager.NotificationListener
    ) {

        private var player: Player? = null
        private val serviceJob = SupervisorJob()
        private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)
        private val notificationManager: PlayerNotificationManager
        private val platformNotificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        init {
            val mediaController = MediaControllerCompat(context, sessionToken)

            val builder = PlayerNotificationManager.Builder(context, NOW_PLAYING_NOTIFICATION_ID, NOW_PLAYING_CHANNEL_ID)
            with (builder) {
                setMediaDescriptionAdapter(DescriptionAdapter(mediaController))
                setNotificationListener(notificationListener)
                setChannelNameResourceId(R.string.notification_channel)
                setChannelDescriptionResourceId(R.string.notification_channel_description)
            }
            notificationManager = builder.build()
            notificationManager.setMediaSessionToken(sessionToken)
            notificationManager.setUseRewindAction(false)
            notificationManager.setUseFastForwardAction(false)
        }

        fun hideNotification() {
            notificationManager.setPlayer(null)
        }

        fun showNotificationForPlayer(player: Player){
            notificationManager.setPlayer(player)
        }

        private inner class DescriptionAdapter(private val controller: MediaControllerCompat) :
            PlayerNotificationManager.MediaDescriptionAdapter {

            var currentIconUri: Uri? = null
            var currentBitmap: Bitmap? = null

            override fun createCurrentContentIntent(player: Player): PendingIntent? =
                controller.sessionActivity

            override fun getCurrentContentText(player: Player) = ""
//                controller.metadata.description.subtitle.toString()

            override fun getCurrentLargeIcon(
                player: Player,
                callback: PlayerNotificationManager.BitmapCallback
            ): Bitmap? {
                return null
            }

            override fun getCurrentContentTitle(player: Player) = ""
//                controller.metadata.description.title.toString()

//            override fun getCurrentLargeIcon(
//                player: Player,
//                callback: PlayerNotificationManager.BitmapCallback
//            ): Bitmap? {
//                val iconUri = controller.metadata.description.iconUri
//                return if (currentIconUri != iconUri || currentBitmap == null) {
//
//                    // Cache the bitmap for the current song so that successive calls to
//                    // `getCurrentLargeIcon` don't cause the bitmap to be recreated.
//                    currentIconUri = iconUri
//                    serviceScope.launch {
//                        currentBitmap = iconUri?.let {
//                            resolveUriAsBitmap(it)
//                        }
//                        currentBitmap?.let { callback.onBitmap(it) }
//                    }
//                    null
//                } else {
//                    currentBitmap
//                }
//            }
//
//            private suspend fun resolveUriAsBitmap(uri: Uri): Bitmap? {
//                return withContext(Dispatchers.IO) {
//                    // Block on downloading artwork.
//                    Glide.with(context).applyDefaultRequestOptions(glideOptions)
//                        .asBitmap()
//                        .load(uri)
//                        .submit(NOTIFICATION_LARGE_ICON_SIZE, NOTIFICATION_LARGE_ICON_SIZE)
//                        .get()
//                }
//            }
        }
    }

//    const val NOTIFICATION_LARGE_ICON_SIZE = 144 // px
//
//    private val glideOptions = RequestOptions()
//        .fallback(R.drawable.default_art)
//        .diskCacheStrategy(DiskCacheStrategy.DATA)
//
//    private const val MODE_READ_ONLY = "r"