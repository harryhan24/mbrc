package com.kelsos.mbrc.ui.navigation.lyrics

import com.kelsos.mbrc.mvp.BaseView
import com.kelsos.mbrc.mvp.Presenter

interface LyricsView : BaseView {
  fun updateLyrics(lyrics: List<String>)
}

interface LyricsPresenter : Presenter<LyricsView>