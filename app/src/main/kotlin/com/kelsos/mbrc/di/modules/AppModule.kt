package com.kelsos.mbrc.di.modules

import com.kelsos.mbrc.DatabaseTransactionRunner
import com.kelsos.mbrc.DatabaseTransactionRunnerImpl
import com.kelsos.mbrc.DeserializationAdapter
import com.kelsos.mbrc.DeserializationAdapterImpl
import com.kelsos.mbrc.IRemoteServiceCore
import com.kelsos.mbrc.RemoteServiceCore
import com.kelsos.mbrc.SerializationAdapter
import com.kelsos.mbrc.SerializationAdapterImpl
import com.kelsos.mbrc.content.activestatus.PlayingTrackCache
import com.kelsos.mbrc.content.activestatus.PlayingTrackCacheImpl
import com.kelsos.mbrc.content.activestatus.livedata.ConnectionStatusLiveDataProvider
import com.kelsos.mbrc.content.activestatus.livedata.ConnectionStatusLiveDataProviderImpl
import com.kelsos.mbrc.content.activestatus.livedata.DefaultSettingsLiveDataProvider
import com.kelsos.mbrc.content.activestatus.livedata.DefaultSettingsLiveDataProviderImpl
import com.kelsos.mbrc.content.activestatus.livedata.LyricsLiveDataProvider
import com.kelsos.mbrc.content.activestatus.livedata.LyricsLiveDataProviderImpl
import com.kelsos.mbrc.content.activestatus.livedata.PlayerStatusLiveDataProvider
import com.kelsos.mbrc.content.activestatus.livedata.PlayerStatusLiveDataProviderImpl
import com.kelsos.mbrc.content.activestatus.livedata.PlayingTrackLiveDataProvider
import com.kelsos.mbrc.content.activestatus.livedata.PlayingTrackLiveDataProviderImpl
import com.kelsos.mbrc.content.activestatus.livedata.TrackPositionLiveDataProvider
import com.kelsos.mbrc.content.activestatus.livedata.TrackPositionLiveDataProviderImpl
import com.kelsos.mbrc.content.activestatus.livedata.TrackRatingLiveDataProvider
import com.kelsos.mbrc.content.activestatus.livedata.TrackRatingLiveDataProviderImpl
import com.kelsos.mbrc.content.library.albums.AlbumRepository
import com.kelsos.mbrc.content.library.albums.AlbumRepositoryImpl
import com.kelsos.mbrc.content.library.artists.ArtistRepository
import com.kelsos.mbrc.content.library.artists.ArtistRepositoryImpl
import com.kelsos.mbrc.content.library.genres.GenreRepository
import com.kelsos.mbrc.content.library.genres.GenreRepositoryImpl
import com.kelsos.mbrc.content.library.tracks.TrackRepository
import com.kelsos.mbrc.content.library.tracks.TrackRepositoryImpl
import com.kelsos.mbrc.content.nowplaying.NowPlayingRepository
import com.kelsos.mbrc.content.nowplaying.NowPlayingRepositoryImpl
import com.kelsos.mbrc.content.nowplaying.cover.CoverApi
import com.kelsos.mbrc.content.nowplaying.cover.CoverApiImpl
import com.kelsos.mbrc.content.nowplaying.cover.CoverModel
import com.kelsos.mbrc.content.nowplaying.cover.StoredCoverModel
import com.kelsos.mbrc.content.nowplaying.queue.QueueApi
import com.kelsos.mbrc.content.nowplaying.queue.QueueApiImpl
import com.kelsos.mbrc.content.output.OutputApi
import com.kelsos.mbrc.content.output.OutputApiImpl
import com.kelsos.mbrc.content.playlists.PlaylistRepository
import com.kelsos.mbrc.content.playlists.PlaylistRepositoryImpl
import com.kelsos.mbrc.content.radios.RadioRepository
import com.kelsos.mbrc.content.radios.RadioRepositoryImpl
import com.kelsos.mbrc.content.sync.LibrarySyncInteractor
import com.kelsos.mbrc.content.sync.LibrarySyncInteractorImpl
import com.kelsos.mbrc.di.bindClass
import com.kelsos.mbrc.di.bindInstance
import com.kelsos.mbrc.di.bindSingletonClass
import com.kelsos.mbrc.di.bindSingletonProvider
import com.kelsos.mbrc.di.bindSingletonProviderInstance
import com.kelsos.mbrc.di.providers.AlbumDaoProvider
import com.kelsos.mbrc.di.providers.ArtistDaoProvider
import com.kelsos.mbrc.di.providers.ConnectionDaoProvider
import com.kelsos.mbrc.di.providers.DatabaseProvider
import com.kelsos.mbrc.di.providers.GenreDaoProvider
import com.kelsos.mbrc.di.providers.NowPlayingDaoProvider
import com.kelsos.mbrc.di.providers.PlaylistDaoProvider
import com.kelsos.mbrc.di.providers.RadioStationDaoProvider
import com.kelsos.mbrc.di.providers.TrackDaoProvider
import com.kelsos.mbrc.networking.ClientConnectionUseCase
import com.kelsos.mbrc.networking.ClientConnectionUseCaseImpl
import com.kelsos.mbrc.networking.RequestManager
import com.kelsos.mbrc.networking.RequestManagerImpl
import com.kelsos.mbrc.networking.client.ClientConnectionManager
import com.kelsos.mbrc.networking.client.IClientConnectionManager
import com.kelsos.mbrc.networking.client.MessageDeserializer
import com.kelsos.mbrc.networking.client.MessageDeserializerImpl
import com.kelsos.mbrc.networking.client.MessageHandler
import com.kelsos.mbrc.networking.client.MessageHandlerImpl
import com.kelsos.mbrc.networking.client.MessageQueue
import com.kelsos.mbrc.networking.client.MessageQueueImpl
import com.kelsos.mbrc.networking.client.MessageSerializer
import com.kelsos.mbrc.networking.client.MessageSerializerImpl
import com.kelsos.mbrc.networking.client.UiMessageQueue
import com.kelsos.mbrc.networking.client.UiMessageQueueImpl
import com.kelsos.mbrc.networking.client.UserActionUseCase
import com.kelsos.mbrc.networking.client.UserActionUseCaseImpl
import com.kelsos.mbrc.networking.connections.ConnectionRepository
import com.kelsos.mbrc.networking.connections.ConnectionRepositoryImpl
import com.kelsos.mbrc.networking.discovery.RemoteServiceDiscovery
import com.kelsos.mbrc.networking.discovery.RemoteServiceDiscoveryImpl
import com.kelsos.mbrc.networking.discovery.ServiceDiscoveryUseCase
import com.kelsos.mbrc.networking.discovery.ServiceDiscoveryUseCaseImpl
import com.kelsos.mbrc.networking.protocol.CommandExecutor
import com.kelsos.mbrc.networking.protocol.CommandExecutorImpl
import com.kelsos.mbrc.networking.protocol.CommandFactory
import com.kelsos.mbrc.networking.protocol.CommandFactoryImpl
import com.kelsos.mbrc.networking.protocol.VolumeInteractor
import com.kelsos.mbrc.networking.protocol.VolumeInteractorImpl
import com.kelsos.mbrc.platform.ServiceChecker
import com.kelsos.mbrc.platform.ServiceCheckerImpl
import com.kelsos.mbrc.platform.mediasession.INotificationManager
import com.kelsos.mbrc.platform.mediasession.SessionNotificationManager
import com.kelsos.mbrc.preferences.AlbumSortingStore
import com.kelsos.mbrc.preferences.AlbumSortingStoreImpl
import com.kelsos.mbrc.preferences.ClientInformationStore
import com.kelsos.mbrc.preferences.ClientInformationStoreImpl
import com.kelsos.mbrc.preferences.SettingsManager
import com.kelsos.mbrc.preferences.SettingsManagerImpl
import com.kelsos.mbrc.utilities.AppRxSchedulers
import com.squareup.moshi.Moshi
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import toothpick.config.Module
import java.util.concurrent.Executors


class AppModule : Module() {
  init {

    bindInstance {
      val moshi = Moshi.Builder().build()
      return@bindInstance moshi
    }

    bindSingletonClass<CoverApi> { CoverApiImpl::class }
    bindSingletonClass<QueueApi> { QueueApiImpl::class }

    bindClass<ConnectionRepository> { ConnectionRepositoryImpl::class }

    bindClass<TrackRepository> { TrackRepositoryImpl::class }
    bindClass<AlbumRepository> { AlbumRepositoryImpl::class }
    bindClass<ArtistRepository> { ArtistRepositoryImpl::class }
    bindClass<GenreRepository> { GenreRepositoryImpl::class }

    bindClass<NowPlayingRepository> { NowPlayingRepositoryImpl::class }
    bindClass<PlaylistRepository> { PlaylistRepositoryImpl::class }

    bindSingletonProvider(DatabaseProvider::class)
    bindSingletonProvider(GenreDaoProvider::class)

    bindSingletonProvider(ArtistDaoProvider::class)
    bindSingletonProvider(AlbumDaoProvider::class)
    bindSingletonProvider(TrackDaoProvider::class)
    bindSingletonProvider(NowPlayingDaoProvider::class)

    bindSingletonProvider(PlaylistDaoProvider::class)
    bindSingletonProvider(RadioStationDaoProvider::class)
    bindSingletonProvider(ConnectionDaoProvider::class)

    bindSingletonClass<SettingsManager> { SettingsManagerImpl::class }
    bindSingletonClass<PlayingTrackCache> { PlayingTrackCacheImpl::class }
    bindSingletonClass<ServiceChecker> { ServiceCheckerImpl::class }

    bindSingletonClass<LibrarySyncInteractor> { LibrarySyncInteractorImpl::class }

    bindSingletonClass<RadioRepository> { RadioRepositoryImpl::class }
    bindSingletonClass<ClientInformationStore> { ClientInformationStoreImpl::class }
    bindSingletonClass<VolumeInteractor> { VolumeInteractorImpl::class }
    bindSingletonClass<OutputApi> { OutputApiImpl::class }
    bindClass<AlbumSortingStore> { AlbumSortingStoreImpl::class }

    //bindInstance { SyncProgressProvider() }

    bindSingletonClass<PlayingTrackLiveDataProvider> { PlayingTrackLiveDataProviderImpl::class }
    bindSingletonClass<PlayerStatusLiveDataProvider> { PlayerStatusLiveDataProviderImpl::class }
    bindSingletonClass<TrackRatingLiveDataProvider> { TrackRatingLiveDataProviderImpl::class }
    bindSingletonClass<ConnectionStatusLiveDataProvider> {
      ConnectionStatusLiveDataProviderImpl::class
    }

    bindSingletonClass<DefaultSettingsLiveDataProvider> {
      DefaultSettingsLiveDataProviderImpl::class
    }

    bindSingletonClass<LyricsLiveDataProvider> { LyricsLiveDataProviderImpl::class }

    bindClass<MessageSerializer> { MessageSerializerImpl::class }

    bindSingletonClass<MessageQueue> { MessageQueueImpl::class }
    bindSingletonClass<MessageHandler> { MessageHandlerImpl::class }
    bindSingletonClass<CommandExecutor> { CommandExecutorImpl::class }
    bindClass<UserActionUseCase> { UserActionUseCaseImpl::class }
    bindSingletonClass<IClientConnectionManager> { ClientConnectionManager::class }
    bindClass<ClientConnectionUseCase> { ClientConnectionUseCaseImpl::class }
    bindSingletonClass<CommandFactory> { CommandFactoryImpl::class }
    bindSingletonClass<MessageDeserializer> { MessageDeserializerImpl::class }
    bindSingletonClass<UiMessageQueue> { UiMessageQueueImpl::class }
    bindSingletonClass<RemoteServiceDiscovery> { RemoteServiceDiscoveryImpl::class }
    bindSingletonClass<ServiceDiscoveryUseCase> { ServiceDiscoveryUseCaseImpl::class }
    bindSingletonClass<TrackPositionLiveDataProvider> { TrackPositionLiveDataProviderImpl::class }

    bindSingletonClass<INotificationManager> { SessionNotificationManager::class }
    bindSingletonClass<IRemoteServiceCore> { RemoteServiceCore::class }

    bindClass<SerializationAdapter> { SerializationAdapterImpl::class }
    bindClass<DeserializationAdapter> { DeserializationAdapterImpl::class }

    bindSingletonProviderInstance {
      AppRxSchedulers(
        AndroidSchedulers.mainThread(),
        Schedulers.io(),
        Schedulers.from(Executors.newSingleThreadExecutor {
          Thread(it, "database")
        }),
        Schedulers.io()
      )
    }

    bindClass<DatabaseTransactionRunner> { DatabaseTransactionRunnerImpl::class }

    bindSingletonProvider(AppCoroutineDispatcherProvider::class)

    bindClass<RequestManager> { RequestManagerImpl::class }

    bindInstance<CoverModel> { StoredCoverModel }
  }
}