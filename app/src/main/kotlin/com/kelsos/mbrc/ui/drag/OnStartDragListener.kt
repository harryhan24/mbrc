package com.kelsos.mbrc.ui.drag

import androidx.recyclerview.widget.RecyclerView

interface OnStartDragListener {
  fun onStartDrag(viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder)
}