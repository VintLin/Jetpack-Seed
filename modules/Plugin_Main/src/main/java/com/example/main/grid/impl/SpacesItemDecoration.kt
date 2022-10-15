package com.example.main.grid.impl

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import androidx.recyclerview.widget.RecyclerView

class SpacesItemDecoration(private val padding: Int, private val isHorizontal: Boolean) : ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        if (isHorizontal) outRect.right = padding
        else outRect.bottom = padding
    }
}