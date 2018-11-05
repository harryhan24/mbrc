package com.kelsos.mbrc.ui.navigation.player

import androidx.appcompat.app.AlertDialog
import com.kelsos.mbrc.R
import com.kelsos.mbrc.extensions.coloredSpan

fun PlayerFragment.showChangeLogDialog(): AlertDialog {
  return requireContext().run {
    AlertDialog.Builder(this)
      .setTitle(coloredSpan(R.string.main__dialog_change_log, R.color.accent))
      .setView(R.layout.change_log_dialog)
      .setPositiveButton(coloredSpan(android.R.string.ok, R.color.accent)) { dialogInterface, _ ->
        dialogInterface.dismiss()
      }
      .show()
  }
}

fun PlayerFragment.showPluginOutOfDateDialog(): AlertDialog {
  return requireContext().run {
    AlertDialog.Builder(this)
      .setTitle(coloredSpan(R.string.main__dialog_plugin_outdated_title, R.color.accent))
      .setMessage(R.string.main__dialog_plugin_outdated_message)
      .setPositiveButton(coloredSpan(android.R.string.ok, R.color.accent)) { dialogInterface, _ ->
        dialogInterface.dismiss()
      }
      .show()
  }
}