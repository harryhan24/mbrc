package com.kelsos.mbrc.content.nowplaying

import com.kelsos.mbrc.interfaces.data.Repository

interface NowPlayingRepository : Repository<NowPlayingEntity> {

  fun move(from: Int, to: Int)
}