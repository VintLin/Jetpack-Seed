package com.example.main.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel: ViewModel() {
    val defaultValue: MutableLiveData<Int> = MutableLiveData(-1)
}