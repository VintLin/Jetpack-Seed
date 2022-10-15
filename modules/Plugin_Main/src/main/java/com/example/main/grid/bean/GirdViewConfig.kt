package com.example.main.grid.bean

data class GirdViewConfig(
    val splitCount: Int = 2,
    val spaceWidth: Int = 20,
    val boxWidth: Int = 250,
    val isHorizontal: Boolean = true,
    val isReordering: Boolean = false,
)