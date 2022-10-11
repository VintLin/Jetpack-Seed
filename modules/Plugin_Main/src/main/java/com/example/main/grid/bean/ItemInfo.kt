package com.example.main.grid.bean

class ItemInfo(private val index: Int, private val item: ItemSize) {

    fun getSize(): ItemSize {
        return item
    }

    fun getIndex(): Int {
        return index
    }
}