package com.kelsos.mbrc.content.library.artists

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
  tableName = "artist",
  indices = [(Index("artist", name = "artist_artist_idx", unique = true))]
)
data class ArtistEntity(
  @ColumnInfo
  override var artist: String,
  @ColumnInfo(name = "date_added")
  var dateAdded: Long = 0,
  @PrimaryKey(autoGenerate = true)
  override var id: Long = 0
) : Artist