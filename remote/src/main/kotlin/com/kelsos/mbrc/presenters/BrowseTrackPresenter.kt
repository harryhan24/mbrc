package com.kelsos.mbrc.presenters

import com.kelsos.mbrc.annotations.Queue.Action
import com.kelsos.mbrc.domain.Track
import com.kelsos.mbrc.ui.views.BrowseTrackView

interface BrowseTrackPresenter {
  fun bind(view: BrowseTrackView)

  fun load()

  fun queue(track: Track, @Action action: String)

  fun load(page: Int, totalItemsCount: Int)
}
