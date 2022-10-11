package com.example.main.grid.core

interface BaseGridView<T : BaseAdapter> {
    fun setAdapter(adapter: T)
}