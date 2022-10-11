package com.example.main.grid.impl

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.LinearLayout
import com.example.main.grid.core.BaseGridView
import com.example.main.grid.pool.LinearLayoutObjectPoolFactory
import com.example.main.grid.pool.ObjectPoolFactory

class GridRecyclerView(context: Context, attrs: AttributeSet?) : RecyclerView(context, attrs), BaseGridView<GridRecyclerViewAdapter> {
    private var adapter: GridRecyclerViewAdapter? = null

    override fun setAdapter(adapter: GridRecyclerViewAdapter) {
        this.adapter = adapter
        super.setAdapter(adapter)
        val isHorizontal = adapter.requestGirdViewConfig().isHorizontal
        layoutManager = LinearLayoutManager(context, if (isHorizontal) LinearLayoutManager.HORIZONTAL else LinearLayoutManager.VERTICAL, false)
        addItemDecoration(SpacesItemDecoration(adapter.requestGirdViewConfig().spaceWidth.toInt()))
        this.adapter?.setPoolFactory(LinearLayoutObjectPoolFactory(context) as ObjectPoolFactory<LinearLayout>)
        this.adapter?.recalculateData()
    }

    init {
        viewTreeObserver?.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                viewTreeObserver.removeGlobalOnLayoutListener(this)
                adapter?.recalculateData()
            }
        })
    }
}