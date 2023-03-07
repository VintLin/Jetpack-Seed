package com.example.main.widget.tabs

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class NavigationBarView @JvmOverloads constructor(
    context: Context, attributeSet: AttributeSet? = null, defStyleAttr: Int = 0
) : RecyclerView(context, attributeSet, defStyleAttr) {

    fun init(tabs: MutableList<TabButtonData>) {
        for (i in tabs.indices) {
            tabs[i].isFirst = false
            tabs[i].isEnd = false
            if (i == 0) tabs[i].isFirst = true
            if (i == tabs.size - 1) tabs[i].isEnd = true
        }
        layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        adapter = NavAdapter(context, tabs)
        setHasFixedSize(true)
    }

    private class NavAdapter(private val context: Context, private var tabs: MutableList<TabButtonData>) : Adapter<NavAdapter.NavViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NavViewHolder {
            return NavViewHolder(TabButtonView(context))
        }

        override fun onBindViewHolder(holder: NavViewHolder, position: Int) {
            holder.bind(tabs[position])
            holder.itemView.setOnClickListener {
                for (tab in tabs) {
                    tab.isChoice = false
                }
                tabs[position].isChoice = true
                notifyDataSetChanged()
            }
        }

        override fun getItemCount(): Int {
            return tabs.size
        }

        inner class NavViewHolder(itemView: TabButtonView) : ViewHolder(itemView) {
            fun bind(data: TabButtonData) {
                itemView.layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT)
                (itemView as TabButtonView).refresh(data)
            }
        }
    }

}