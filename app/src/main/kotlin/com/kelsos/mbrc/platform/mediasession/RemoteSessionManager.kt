@file:Suppress("DEPRECATION")

package com.kelsos.mbrc.platform.mediasession

import android.annotation.TargetApi
import android.app.Application
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Intent
import android.media.AudioManager
import android.media.session.MediaSession.FLAG_HANDLES_MEDIA_BUTTONS
import android.media.session.MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS
import android.media.session.PlaybackState
import android.os.Build
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.kelsos.mbrc.content.activestatus.PlayerState
import com.kelsos.mbrc.content.activestatus.PlayerState.State
import com.kelsos.mbrc.content.library.tracks.PlayingTrack
import com.kelsos.mbrc.events.UserAction
import com.kelsos.mbrc.networking.client.UserActionUseCase
import com.kelsos.mbrc.networking.connections.Connection
import com.kelsos.mbrc.networking.protocol.Protocol
import com.kelsos.mbrc.utilities.RemoteUtils
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteSessionManager
@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
@Inject
constructor(
  context: Application,
  volumeProvider: RemoteVolumeProvider,
  private val userActionUseCase: UserActionUseCase,
  private val manager: AudioManager
) : AudioManager.OnAudioFocusChangeListener {
  private val mediaSession: MediaSessionCompat

  @Inject
  lateinit var handler: MediaIntentHandler

  init {
    val myEventReceiver = ComponentName(context.packageName, MediaButtonReceiver::class.java.name)
    val mediaButtonIntent = Intent(Intent.ACTION_MEDIA_BUTTON)
    mediaButtonIntent.component = myEventReceiver
    val mediaPendingIntent = PendingIntent.getBroadcast(
      context.applicationContext, 0, mediaButtonIntent,
      PendingIntent.FLAG_UPDATE_CURRENT
    )

    mediaSession = MediaSessionCompat(context, "Session").apply {
      setMediaButtonReceiver(mediaPendingIntent)
      setPlaybackToRemote(volumeProvider)
      setFlags(FLAG_HANDLES_MEDIA_BUTTONS or FLAG_HANDLES_TRANSPORT_CONTROLS)
    }

    mediaSession.setCallback(object : MediaSessionCompat.Callback() {
      override fun onMediaButtonEvent(mediaButtonEvent: Intent?): Boolean {
        val success = handler.handleMediaIntent(mediaButtonEvent)
        return success || super.onMediaButtonEvent(mediaButtonEvent)
      }

      override fun onPlay() {
        postAction(UserAction(Protocol.PlayerPlay, true))
      }

      override fun onPause() {
        postAction(UserAction(Protocol.PlayerPause, true))
      }

      override fun onSkipToNext() {
        postAction(UserAction(Protocol.PlayerNext, true))
      }

      override fun onSkipToPrevious() {
        postAction(UserAction(Protocol.PlayerPrevious, true))
      }

      override fun onStop() {
        postAction(UserAction(Protocol.PlayerStop, true))
      }
    })
  }

  private fun onConnectionStatusChanged(@Connection.Status status: Int) {
    if (status != Connection.OFF) {
      return
    }

    val playbackState = PlaybackStateCompat.Builder()
      .setState(PlaybackState.STATE_STOPPED, -1, 0f)
      .build()

    with(mediaSession) {
      isActive = false
      setPlaybackState(playbackState)
    }

    abandonFocus()
  }

  private fun postAction(action: UserAction) {
    userActionUseCase.perform(action)
  }

  val mediaSessionToken: MediaSessionCompat.Token
    get() = mediaSession.sessionToken

  private fun metadataUpdate(track: PlayingTrack) {
    val bitmap = RemoteUtils.coverBitmapSync(track.coverUrl)

    val meta = MediaMetadataCompat.Builder().apply {
      putString(MediaMetadataCompat.METADATA_KEY_ALBUM, track.album)
      putString(MediaMetadataCompat.METADATA_KEY_ARTIST, track.artist)
      putString(MediaMetadataCompat.METADATA_KEY_TITLE, track.title)
      putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, bitmap)
    }

    mediaSession.setMetadata(meta.build())
  }

  private fun updateState(@State state: String) {

    val playbackState = PlaybackStateCompat.Builder()
      .setActions(PLAYBACK_ACTIONS)
      .apply {
        when (state) {
          PlayerState.PLAYING -> {
            setState(PlaybackStateCompat.STATE_PLAYING, -1, 1f)
            mediaSession.isActive = true
          }
          PlayerState.PAUSED -> {
            setState(PlaybackStateCompat.STATE_PAUSED, -1, 0f)
            mediaSession.isActive = true
          }
          else -> {
            setState(PlaybackStateCompat.STATE_STOPPED, -1, 0f)
            mediaSession.isActive = false
          }
        }
      }.build()

    mediaSession.setPlaybackState(playbackState)
  }

  private fun onPlayStateChange(@State state: String) {
    when (state) {
      PlayerState.PLAYING -> requestFocus()
      PlayerState.PAUSED -> Unit
      else -> abandonFocus()
    }
  }

  private fun requestFocus(): Boolean {
    return AudioManager.AUDIOFOCUS_REQUEST_GRANTED == manager.requestAudioFocus(
      this,
      AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN
    )
  }

  private fun abandonFocus(): Boolean {
    return AudioManager.AUDIOFOCUS_REQUEST_GRANTED == manager.abandonAudioFocus(this)
  }

  override fun onAudioFocusChange(focusChange: Int) {
    when (focusChange) {
      AudioManager.AUDIOFOCUS_GAIN -> Timber.d("gained")
      AudioManager.AUDIOFOCUS_LOSS,
      AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> Timber.d("transient loss")
      AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> Timber.d("Loss can duck")
    }
  }

  companion object {
    private const val PLAYBACK_ACTIONS = PlaybackStateCompat.ACTION_PAUSE or
      PlaybackStateCompat.ACTION_PLAY_PAUSE or
      PlaybackStateCompat.ACTION_PLAY or
      PlaybackStateCompat.ACTION_SKIP_TO_NEXT or
      PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS or
      PlaybackStateCompat.ACTION_STOP
  }
}