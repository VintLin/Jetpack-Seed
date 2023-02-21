package com.example.main.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {
    enum class LoadState(val event: String) {
        DEFAULT("未加载"), LOADING("加载中"), COMPLETE("加载成功"), FAIL("加载失败"),
    }
}