package com.kelsos.mbrc.content.library.tracks

import android.arch.paging.DataSource
import com.kelsos.mbrc.content.library.DataModel
import com.kelsos.mbrc.networking.ApiBase
import com.kelsos.mbrc.networking.protocol.Protocol
import com.kelsos.mbrc.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.utilities.epoch
import io.reactivex.Completable
import io.reactivex.Single
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.withContext
import javax.inject.Inject

class TrackRepositoryImpl
@Inject
constructor(
  private val dao: TrackDao,
  private val remoteDataSource: ApiBase,
  private val coroutineDispatchers: AppCoroutineDispatchers
) : TrackRepository {

  private val mapper = TrackDtoMapper()

  override suspend fun count(): Long {
    return withContext(coroutineDispatchers.database) { dao.count() }
  }

  override fun getAll(): Single<DataSource.Factory<Int, TrackEntity>> {
    return Single.fromCallable { dao.getAll() }
  }

  override fun getAlbumTracks(
    album: String,
    artist: String
  ): Single<DataSource.Factory<Int, TrackEntity>> {
    return Single.fromCallable { dao.getAlbumTracks(album, artist) }
  }

  override fun allTracks(): Single<DataModel<TrackEntity>> {
    return Single.fromCallable { DataModel(dao.getAll(), dao.getAllIndexes()) }
  }

  override fun getNonAlbumTracks(artist: String): Single<DataSource.Factory<Int, TrackEntity>> {
    return Single.fromCallable { dao.getNonAlbumTracks(artist) }
  }

  override fun getRemote(): Completable {
    val added = epoch()
    return remoteDataSource.getAllPages(Protocol.LibraryBrowseTracks, TrackDto::class).doOnNext {
      async(CommonPool) {
        val tracks = it.map { mapper.map(it).apply { dateAdded = added } }
        val sources = tracks.map { it.src }

        withContext(coroutineDispatchers.database) {

          val matches = sources.chunked(50)
            .flatMap { dao.findMatchingIds(it) }
            .map { it.src to it.id }
            .toMap()

          val toUpdate = tracks.filter { matches.containsKey(it.src) }
          val toInsert = tracks.minus(toUpdate)
          
          dao.update(toUpdate.map { it.id = matches.getValue(it.src); it })
          dao.insertAll(toInsert)
        }
      }
    }.doOnComplete {
      async(coroutineDispatchers.database) {
        dao.removePreviousEntries(added)
      }
    }.ignoreElements()
  }

  override fun search(term: String): Single<DataSource.Factory<Int, TrackEntity>> {
    return Single.fromCallable { dao.search(term) }
  }

  override fun getGenreTrackPaths(genre: String): Single<List<String>> {
    return Single.fromCallable { dao.getGenreTrackPaths(genre) }
  }

  override fun getArtistTrackPaths(artist: String): Single<List<String>> {
    return Single.fromCallable { dao.getArtistTrackPaths(artist) }
  }

  override fun getAlbumTrackPaths(album: String, artist: String): Single<List<String>> {
    return Single.fromCallable { dao.getAlbumTrackPaths(album, artist) }
  }

  override fun getAllTrackPaths(): Single<List<String>> {
    return Single.fromCallable { dao.getAllTrackPaths() }
  }

  override fun cacheIsEmpty(): Single<Boolean> {
    return Single.fromCallable { dao.count() == 0L }
  }
}