package com.example.main.view

import android.graphics.Color
import android.os.Bundle
import android.view.View
import com.example.base.activity.CommonFragment
import com.example.base.jetpack.binding.DataBindingConfig
import com.example.base.util.once.Once
import com.example.main.BR
import com.example.main.R
import com.example.main.viewmodel.HomeViewModel
import com.example.main.widget.ShadowUiData
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : CommonFragment() {
    lateinit var mState: HomeViewModel

    override fun initViewModel() {
        mState = getFragmentScopeViewModel(HomeViewModel::class.java)
    }

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.fragment_home, BR.vm, mState).addBindingParam(BR.event, EventHandler())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val ui = ShadowUiData.Config()
            .setColor(Color.WHITE)
            .setShadow(
                outer = ShadowUiData.Shadow(
                    0f, 0f, 20f, 10f, Color.parseColor("#999999")
                ),
                inner = ShadowUiData.Shadow(
                    0f, 0f, 50f, -10f, Color.parseColor("#CCCCCC")
                ),
            ).setRound(
                ShadowUiData.Round(
                    leftTop = 30f,
                    rightTop = 40f,
                    rightBottom = 50f,
                    leftBottom = 60f,
                )
            ).getUiData()
        shadow.setData(ui)
    }

    inner class EventHandler : View.OnClickListener {
        override fun onClick(v: View) {
            if (Once.beenDone(500L, "MainFragment.Click")) return
            Once.clearAndMarkDone("MainFragment.Click")
            when (v.id) {

            }
        }
    }
}