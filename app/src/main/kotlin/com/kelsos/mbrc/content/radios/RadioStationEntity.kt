package com.kelsos.mbrc.content.radios

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
  tableName = "radio_station",
  indices = [(Index("url", name = "radio_url_idx", unique = true))]
)
data class RadioStationEntity(
  @ColumnInfo(name = "name")
  override var name: String = "",
  @ColumnInfo(name = "url")
  override var url: String = "",
  @ColumnInfo(name = "date_added")
  var dateAdded: Long = 0,
  @PrimaryKey(autoGenerate = true)
  override var id: Long = 0
) : RadioStation