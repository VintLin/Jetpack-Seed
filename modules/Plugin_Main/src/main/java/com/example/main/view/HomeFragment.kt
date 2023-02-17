package com.example.main.view

import android.os.Bundle
import android.view.View
import com.example.base.activity.CommonFragment
import com.example.base.jetpack.binding.DataBindingConfig
import com.example.base.util.once.Once
import com.example.main.BR
import com.example.main.R
import com.example.main.viewmodel.HomeViewModel
import com.opensource.svgaplayer.SVGACache
import com.opensource.svgaplayer.SVGAParser
import com.opensource.svgaplayer.SVGAVideoEntity
import kotlinx.android.synthetic.main.fragment_home.*
import java.net.URL


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
        mState.localStatus.postValue(HomeViewModel.LoadState.LOADING)
    }

    private fun loadLocal() {
        SVGACache.clearCache()
        val currentTime = System.currentTimeMillis()
        // 加载本地动画文件
        SVGAParser(requireContext()).decodeFromAssets("local.svga", object : SVGAParser.ParseCompletion {
            override fun onComplete(videoItem: SVGAVideoEntity) {
                // 解码完成，设置动画数据并播放
                svga_local.setVideoItem(videoItem)
                svga_local.startAnimation()
                mState.localStatus.postValue(HomeViewModel.LoadState.COMPLETE)
                mState.localTimer.postValue((System.currentTimeMillis() - currentTime) / 1000f)
            }

            override fun onError() {
                // 解码失败，进行错误处理
                mState.localStatus.postValue(HomeViewModel.LoadState.FAIL)
                mState.localTimer.postValue((System.currentTimeMillis() - currentTime) / 1000f)
            }
        })
    }

    private fun loadNetwork() {
        SVGACache.clearCache()
        val currentTime = System.currentTimeMillis()
        mState.networkStatus.postValue(HomeViewModel.LoadState.LOADING)
        // 加载网络动画文件
        SVGAParser(requireContext()).decodeFromURL(URL("https://picfile-baidu.babybus.com/AppConfig/BabyBusFile/20230217/bc83f1dcae8943eaacaed6714d2612e5.svga"), object : SVGAParser.ParseCompletion {
            override fun onComplete(videoItem: SVGAVideoEntity) {
                // 解码完成，设置动画数据并播放
                svga_network.setVideoItem(videoItem)
                svga_network.startAnimation()
                mState.networkStatus.postValue(HomeViewModel.LoadState.COMPLETE)
                mState.networkTimer.postValue((System.currentTimeMillis() - currentTime) / 1000f)
            }

            override fun onError() {
                // 解码失败，进行错误处理
                mState.networkStatus.postValue(HomeViewModel.LoadState.FAIL)
                mState.networkTimer.postValue((System.currentTimeMillis() - currentTime) / 1000f)
            }
        })
    }

    inner class EventHandler : View.OnClickListener {
        override fun onClick(v: View) {
            if (Once.beenDone(500L, "MainFragment.Click")) return
            Once.clearAndMarkDone("MainFragment.Click")
            when (v.id) {
                R.id.btn_network -> {
                    btn_network.visibility = View.GONE
                    loadNetwork()
                }
                R.id.btn_local -> {
                    btn_local.visibility = View.GONE
                    loadLocal()
                }
            }
        }
    }
}