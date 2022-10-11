package com.example.main.grid.pool

import android.content.Context
import android.widget.LinearLayout

class LinearLayoutObjectPoolFactory(private val context: Context) : ObjectPoolFactory<LinearLayout?> {
    override fun createObject(): LinearLayout {
        return LinearLayout(context, null)
    }
}