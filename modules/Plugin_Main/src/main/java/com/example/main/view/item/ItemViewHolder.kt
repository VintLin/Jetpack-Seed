package com.example.main.view.item

import android.view.View
import android.widget.TextView
import com.example.main.R
import com.example.main.grid.core.BaseAdapter

class ViewHolder(itemView: View) : BaseAdapter.ItemViewHolder(itemView) {

    private val textView: TextView = itemView.findViewById<View>(R.id.textview) as TextView

    fun bind(item: ItemData) {
        textView.text = "${item.position}"
    }
}