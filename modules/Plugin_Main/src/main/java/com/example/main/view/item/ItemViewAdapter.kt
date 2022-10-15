package com.example.main.view.item

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.main.R
import com.example.main.grid.bean.GridItemSize
import com.example.main.grid.impl.GridRecyclerViewAdapter

class ItemViewAdapter(private val items: List<ItemData>) : GridRecyclerViewAdapter() {

    override fun getRealItemCount(): Int {
        return items.size
    }

    override fun getItem(position: Int): GridItemSize {
        return items[position]
    }

    override fun onCreateItemViewHolder(parent: ViewGroup, viewType: Int, position: Int): ItemViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(
            R.layout.adapter_item,
            parent,
            false
        )
        view.setBackgroundColor(if (viewType == 1) Color.BLUE else Color.RED)
        return ViewHolder(view)
    }

    override fun onBindItemViewHolder(parent: ViewGroup, holder: ItemViewHolder, position: Int) {
        (holder as ViewHolder).bind(items[position])
    }

    override fun getItemViewType(position: Int): Int {
        return if (position % 2 == 0) 1 else 0
    }
}