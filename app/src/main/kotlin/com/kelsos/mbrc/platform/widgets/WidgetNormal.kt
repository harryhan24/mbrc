package com.kelsos.mbrc.platform.widgets

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.kelsos.mbrc.R
import com.kelsos.mbrc.content.activestatus.PlayerState
import com.kelsos.mbrc.content.activestatus.PlayerState.State
import com.kelsos.mbrc.content.library.tracks.PlayingTrackModel
import com.kelsos.mbrc.platform.mediasession.RemoteViewIntentBuilder.NEXT
import com.kelsos.mbrc.platform.mediasession.RemoteViewIntentBuilder.PLAY
import com.kelsos.mbrc.platform.mediasession.RemoteViewIntentBuilder.PREVIOUS
import com.kelsos.mbrc.platform.mediasession.RemoteViewIntentBuilder.getPendingIntent
import com.kelsos.mbrc.ui.navigation.main.MainActivity
import com.squareup.picasso.Picasso
import timber.log.Timber
import java.io.File

class WidgetNormal : AppWidgetProvider() {

  override fun onReceive(context: Context?, intent: Intent?) {
    super.onReceive(context, intent)
    if (intent == null || intent.action != AppWidgetManager.ACTION_APPWIDGET_UPDATE) {
      return
    }

    val extras = intent.extras
    val widgetManager = AppWidgetManager.getInstance(context)
    val widgets = ComponentName(context?.packageName, WidgetNormal::class.java.name)
    val widgetsIds = widgetManager.getAppWidgetIds(widgets)

    if (extras == null) {
      return
    }

    if (extras.getBoolean(UpdateWidgets.COVER, false)) {
      val path = extras.getString(UpdateWidgets.COVER_PATH, "")
      updateCover(context, widgetManager, widgetsIds, path)
    } else if (extras.getBoolean(UpdateWidgets.INFO, false)) {
      updateInfo(
        context,
        widgetManager,
        widgetsIds,
        extras.getParcelable<PlayingTrackModel>(UpdateWidgets.TRACK_INFO)
      )
    } else if (extras.getBoolean(UpdateWidgets.STATE, false)) {
      updatePlayState(
        context, widgetManager, widgetsIds,
        extras.getString(UpdateWidgets.PLAYER_STATE, PlayerState.UNDEFINED)
      )
    }
  }

  override fun onUpdate(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetIds: IntArray
  ) {
    super.onUpdate(context, appWidgetManager, appWidgetIds)
    Timber.v("Update widget received")

    for (appWidgetId in appWidgetIds) {
      // Create an Intent to launch ExampleActivity
      val intent = Intent(context, MainActivity::class.java)
      val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

      // Get the layout for the App Widget and attach an on-click listener
      // to the button
      val views = RemoteViews(context.packageName, R.layout.widget_normal)

      views.setOnClickPendingIntent(R.id.widget_normal_image, pendingIntent)
      views.setOnClickPendingIntent(R.id.widget_normal_play, getPendingIntent(PLAY, context))
      views.setOnClickPendingIntent(R.id.widget_normal_next, getPendingIntent(NEXT, context))
      views.setOnClickPendingIntent(
        R.id.widget_normal_previous,
        getPendingIntent(PREVIOUS, context)
      )

      // Tell the AppWidgetManager to perform an update on the current app widget
      appWidgetManager.updateAppWidget(appWidgetId, views)
    }
  }

  private fun updateInfo(
    context: Context?,
    widgetManager: AppWidgetManager,
    widgetsIds: IntArray,
    info: PlayingTrackModel
  ) {
    if (context == null) {
      return
    }

    val widget = RemoteViews(context.packageName, R.layout.widget_normal)
    widget.setTextViewText(R.id.widget_normal_line_one, info.title)
    widget.setTextViewText(R.id.widget_normal_line_two, info.artist)
    widget.setTextViewText(R.id.widget_normal_line_three, info.album)
    widgetManager.updateAppWidget(widgetsIds, widget)
  }

  private fun updateCover(
    context: Context?,
    widgetManager: AppWidgetManager,
    widgetsIds: IntArray,
    path: String
  ) {
    if (context == null) {
      return
    }

    val widget = RemoteViews(context.packageName, R.layout.widget_normal)

    val coverFile = File(path)
    if (coverFile.exists()) {
      Picasso.get().invalidate(coverFile)
      Picasso.get().load(coverFile)
        .centerCrop()
        .resizeDimen(R.dimen.widget_normal_height, R.dimen.widget_normal_height)
        .into(widget, R.id.widget_normal_image, widgetsIds)
    } else {
      widget.setImageViewResource(R.id.widget_normal_image, R.drawable.ic_image_no_cover)
    }
    widgetManager.updateAppWidget(widgetsIds, widget)
  }

  private fun updatePlayState(
    context: Context?,
    manager: AppWidgetManager,
    widgetsIds: IntArray,
    @State state: String
  ) {
    if (context == null) {
      return
    }

    val widget = RemoteViews(context.packageName, R.layout.widget_normal)

    widget.setImageViewResource(
      R.id.widget_normal_play,
      if (PlayerState.PLAYING == state) {
        R.drawable.ic_action_pause
      } else {
        R.drawable.ic_action_play
      }
    )
    manager.updateAppWidget(widgetsIds, widget)
  }
}