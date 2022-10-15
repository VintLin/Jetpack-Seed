package com.example.main.view

import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.base.activity.CommonFragment
import com.example.base.jetpack.binding.DataBindingConfig
import com.example.base.util.once.Once
import com.example.main.BR
import com.example.main.R
import com.example.main.view.bean.DemoUtils
import com.example.main.grid.bean.GirdViewAction
import com.example.main.grid.impl.GridRecyclerView
import com.example.main.view.item.ItemViewAdapter
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
            val adapter = ItemViewAdapter(DemoUtils().randomItems(50))
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
}