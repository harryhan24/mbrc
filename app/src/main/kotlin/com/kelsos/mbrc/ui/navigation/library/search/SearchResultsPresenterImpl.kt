package com.kelsos.mbrc.ui.navigation.library.search

import android.arch.lifecycle.LiveData
import com.kelsos.mbrc.content.library.albums.AlbumEntity
import com.kelsos.mbrc.content.library.albums.AlbumRepository
import com.kelsos.mbrc.content.library.artists.ArtistEntity
import com.kelsos.mbrc.content.library.artists.ArtistRepository
import com.kelsos.mbrc.content.library.genres.GenreEntity
import com.kelsos.mbrc.content.library.genres.GenreRepository
import com.kelsos.mbrc.content.library.tracks.TrackEntity
import com.kelsos.mbrc.content.library.tracks.TrackRepository
import com.kelsos.mbrc.mvp.BasePresenter
import com.kelsos.mbrc.utilities.SchedulerProvider
import io.reactivex.Single
import io.reactivex.functions.Function4
import timber.log.Timber
import javax.inject.Inject

class SearchResultsPresenterImpl
@Inject
constructor(
    private val genreRepository: GenreRepository,
    private val artistRepository: ArtistRepository,
    private val albumRepository: AlbumRepository,
    private val trackRepository: TrackRepository,
    private val schedulerProvider: SchedulerProvider
) : BasePresenter<SearchResultsView>(),
    SearchResultsPresenter {
  override fun search(term: String) {

    addDisposable(Single.zip(genreRepository.search(term),
        artistRepository.search(term),
        albumRepository.search(term),
        trackRepository.search(term),
        Function4 { genreList: LiveData<List<GenreEntity>>,
                    artistList: LiveData<List<ArtistEntity>>,
                    albumList: LiveData<List<AlbumEntity>>,
                    trackList: LiveData<List<TrackEntity>> ->

          SearchResults(genreList, artistList, albumList, trackList)
        })
        .subscribeOn(schedulerProvider.io())
        .observeOn(schedulerProvider.main())
        .subscribe({
          view().update(it)
        }) {
          Timber.v(it)
        })
  }
}
