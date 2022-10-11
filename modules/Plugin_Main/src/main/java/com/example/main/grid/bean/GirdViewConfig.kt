package com.example.main.grid.bean

import android.view.View

data class GirdViewConfig(
    val splitCount: Int = 2,
    val spaceWidth: Float = 20f,
    val boxWidth: Int = 200,
    val isHorizontal: Boolean = true,
    val isReordering: Boolean = false,
)