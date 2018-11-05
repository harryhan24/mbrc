package com.kelsos.mbrc.ui.navigation.player

import androidx.lifecycle.ViewModel
import com.jakewharton.rxrelay2.PublishRelay
import com.kelsos.mbrc.content.activestatus.livedata.PlayerStatusLiveDataProvider
import com.kelsos.mbrc.events.UserAction
import com.kelsos.mbrc.networking.client.UserActionUseCase
import com.kelsos.mbrc.networking.protocol.Protocol
import com.kelsos.mbrc.utilities.AppRxSchedulers
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit


class VolumeDialogViewModel(
  private val userActionUseCase: UserActionUseCase,
  private val appRxSchedulers: AppRxSchedulers,
  val playerStatus: PlayerStatusLiveDataProvider
) : ViewModel() {

  private val volumeRelay: PublishRelay<Int> = PublishRelay.create()
  private val disposable: Disposable

  init {
    disposable = volumeRelay.throttleLast(
      800,
      TimeUnit.MILLISECONDS,
      appRxSchedulers.network
    )
      .subscribeOn(appRxSchedulers.network)
      .subscribe { volume ->
        userActionUseCase.perform(UserAction.create(Protocol.PlayerVolume, volume))
      }
  }

  override fun onCleared() {
    disposable.dispose()
    super.onCleared()
  }

  fun mute() {
    userActionUseCase.perform(UserAction.toggle(Protocol.PlayerMute))
  }

  fun changeVolume(volume: Int) {
    volumeRelay.accept(volume)
  }
}
