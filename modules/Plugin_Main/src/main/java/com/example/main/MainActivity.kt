package com.example.main

import android.os.Bundle
import com.example.main.BR
import com.example.main.R
import com.example.base.activity.CommonActivity
import com.example.base.jetpack.binding.DataBindingConfig
import com.example.main.viewmodel.MainViewModel

class MainActivity  : CommonActivity() {
    private lateinit var mState: MainViewModel

    override fun initViewModel() {
        mState = getActivityScopeViewModel(MainViewModel::class.java)
    }

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.activity_main, BR.vm, mState)
    }
}