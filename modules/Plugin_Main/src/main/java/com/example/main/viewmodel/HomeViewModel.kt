package com.example.main.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {
    val localTimer: MutableLiveData<Float> = MutableLiveData(0f)
    val localStatus: MutableLiveData<LoadState> = MutableLiveData(LoadState.DEFAULT)

    val networkTimer: MutableLiveData<Float> = MutableLiveData(0f)
    val networkStatus: MutableLiveData<LoadState> = MutableLiveData(LoadState.DEFAULT)


    enum class LoadState(val event: String) {
        DEFAULT("未加载"), LOADING("加载中"), COMPLETE("加载成功"), FAIL("加载失败"),
    }
}