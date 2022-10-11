package com.example.main.grid.pool

interface ObjectPoolFactory<T> {
    fun createObject(): T
}