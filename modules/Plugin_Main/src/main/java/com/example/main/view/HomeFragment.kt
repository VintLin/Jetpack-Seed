package com.example.main.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.base.activity.CommonFragment
import com.example.base.jetpack.binding.DataBindingConfig
import com.example.base.util.once.Once
import com.example.main.BR
import com.example.main.R
import com.example.main.bean.DemoItem
import com.example.main.bean.DemoUtils
import com.example.main.grid.bean.GirdViewAction
import com.example.main.grid.bean.GirdViewConfig
import com.example.main.grid.bean.ItemSize
import com.example.main.grid.core.BaseAdapter
import com.example.main.grid.impl.GridRecyclerView
import com.example.main.grid.impl.GridRecyclerViewAdapter
import com.example.main.viewmodel.HomeViewModel


class HomeFragment : CommonFragment() {
    lateinit var mState: HomeViewModel
    private var gridView: GridRecyclerView? = null

    override fun initViewModel() {
        mState = getFragmentScopeViewModel(HomeViewModel::class.java)
    }

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.fragment_home, BR.vm, mState)
            .addBindingParam(BR.event, EventHandler())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        gridView = view.findViewById(R.id.recyclerView)
        gridView?.apply {
            val adapter = ViewAdapter(DemoUtils().standTwoItems())
            adapter.setGirdViewAction(Action())
            setAdapter(adapter)
        }
    }

    inner class EventHandler : View.OnClickListener {
        override fun onClick(v: View) {
            if (Once.beenDone(500L, "MainFragment.Click")) return
            Once.clearAndMarkDone("MainFragment.Click")
        }
    }

    class Action : GirdViewAction {
        override fun onItemClick(index: Int, view: View) {
            Log.e("HomeFragment", "onItemClick index: $index")
        }

        override fun onItemLongClick(index: Int, view: View): Boolean {
            Log.e("HomeFragment", "onItemLongClick index: $index")
            return false
        }

    }

    inner class ViewAdapter(private val items: List<DemoItem>) : GridRecyclerViewAdapter() {

        override fun getRealItemCount(): Int {
            return items.size
        }

        override fun getItem(position: Int): ItemSize {
            return items[position]
        }

        override fun onCreateItemViewHolder(parent: ViewGroup, viewType: Int, position: Int): ItemViewHolder {
            return ViewHolder(parent, viewType)
        }

        override fun onBindItemViewHolder(parent: ViewGroup, holder: ItemViewHolder, position: Int) {
            (holder as ViewHolder).bind(items[position])
        }

        override fun getItemViewType(position: Int): Int {
            return if (position % 2 == 0) 1 else 0
        }
    }

    inner class ViewHolder(parent: ViewGroup, viewType: Int) :
        BaseAdapter.ItemViewHolder(
            LayoutInflater.from(parent.context).inflate(
                if (viewType == 0) R.layout.adapter_item else R.layout.adapter_item_odd,
                parent,
                false
            )
        ) {

        private val textView: TextView = if (viewType == 0) {
            itemView.findViewById<View>(R.id.textview) as TextView
        } else {
            itemView.findViewById<View>(R.id.textview_odd) as TextView
        }

        fun bind(item: DemoItem) {
            textView.text = "${item.position}"
        }
    }
}