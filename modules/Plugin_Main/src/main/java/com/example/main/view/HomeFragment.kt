package com.example.main.view

import android.view.View
import com.example.main.R
import com.example.main.BR
import com.example.base.activity.CommonFragment
import com.example.base.jetpack.binding.DataBindingConfig
import com.example.base.util.once.Once
import com.example.main.viewmodel.HomeViewModel

class HomeFragment : CommonFragment() {
    lateinit var mState: HomeViewModel

    override fun initViewModel() {
        mState = getFragmentScopeViewModel(HomeViewModel::class.java)
    }

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.fragment_home, BR.vm, mState)
            .addBindingParam(BR.event, EventHandler())
    }

    inner class EventHandler : View.OnClickListener {
        override fun onClick(v: View) {
            if (Once.beenDone(500L, "MainFragment.Click")) return
            Once.clearAndMarkDone("MainFragment.Click")
            when (v.id) {
                R.id.btn_test -> {

                }
            }
        }
    }
}