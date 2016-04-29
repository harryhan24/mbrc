package com.kelsos.mbrc.presenters

import com.google.inject.Inject
import com.kelsos.mbrc.annotations.MetaDataType
import com.kelsos.mbrc.annotations.Queue
import com.kelsos.mbrc.domain.Track
import com.kelsos.mbrc.interactors.AlbumTrackInteractor
import com.kelsos.mbrc.interactors.QueueInteractor
import com.kelsos.mbrc.ui.views.AlbumTrackView
import roboguice.util.Ln
import rx.android.schedulers.AndroidSchedulers
import timber.log.Timber

class AlbumTracksPresenterImpl : AlbumTracksPresenter {

  private var view: AlbumTrackView? = null
  @Inject private lateinit var albumTrackInteractor: AlbumTrackInteractor
  @Inject private lateinit var interactor: QueueInteractor

  override fun bind(view: AlbumTrackView) {
    this.view = view
  }

  override fun load(albumId: Long) {
    albumTrackInteractor.execute(albumId).observeOn(AndroidSchedulers.mainThread()).subscribe({ albumTrackModel ->
      view!!.updateTracks(albumTrackModel.tracks)
      view!!.updateAlbum(albumTrackModel.album)
    }, { Ln.v(it) })
  }

  override fun play(albumId: Long) {
    interactor.execute(MetaDataType.ALBUM, Queue.NOW, albumId.toInt()).subscribe({ aBoolean ->
      if (aBoolean!!) {
        view?.showPlaySuccess()
      } else {
        view?.showPlayFailed()
      }
    }) { t ->
      view?.showPlayFailed()
      Timber.e(t, "Failed to play the album %d", albumId)
    }
  }

  override fun queue(entry: Track, @Queue.Action action: String) {
    val id = entry.id
    interactor.execute(MetaDataType.TRACK, action, id.toInt()).subscribe({ aBoolean ->
      if (aBoolean!!) {
        view?.showTrackSuccess()
      } else {
        view?.showTrackFailed()
      }
    }) { t ->
      view?.showTrackFailed()
      Timber.e(t, "Failed to play the track %d", id)
    }
  }
}
