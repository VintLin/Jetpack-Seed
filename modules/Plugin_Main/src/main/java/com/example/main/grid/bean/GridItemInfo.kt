package com.example.main.grid.bean

import com.example.main.grid.bean.GridItemSize

class GridItemInfo(private val index: Int, private val item: GridItemSize) {

    fun getSize(): GridItemSize {
        return item
    }

    fun getIndex(): Int {
        return index
    }
}