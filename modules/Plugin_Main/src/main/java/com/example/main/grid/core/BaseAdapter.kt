package com.example.main.grid.core

import android.annotation.SuppressLint
import android.database.CursorIndexOutOfBoundsException
import android.graphics.drawable.GradientDrawable
import android.os.AsyncTask
import android.util.ArrayMap
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.main.grid.bean.GirdViewAction
import com.example.main.grid.bean.*
import com.example.main.grid.pool.ObjectPool
import com.example.main.grid.pool.ObjectPoolFactory
import com.example.main.grid.bean.GridItemInfo

/**
 * 自适应网格布局适配器
 * 由外层的RecyclerView及内层的LinearLayout组成
 */
abstract class BaseAdapter : RecyclerView.Adapter<BaseAdapter.BaseViewHolder>(), View.OnClickListener, View.OnLongClickListener {
    companion object {
        private const val TAG = "BaseAdapter"
    }

    private val lines: MutableMap<Int, GridLineInfo> = HashMap()
    private val holders: MutableMap<Int, ObjectPool<ItemViewHolder>> = ArrayMap()
    private var pools: ObjectPool<LinearLayout>? = null
    private var task: ProcessTask? = null
    private var config: GirdViewConfig? = null
    private var action: GirdViewAction? = null

    fun setGirdViewConfig(config: GirdViewConfig) {
        this.config = config
    }

    fun requestGirdViewConfig(): GirdViewConfig {
        return config ?: GirdViewConfig()
    }

    fun setGirdViewAction(action: GirdViewAction) {
        this.action = action
    }

    fun setPoolFactory(factory: ObjectPoolFactory<LinearLayout>) {
        pools = ObjectPool(factory)
    }

    fun recalculateData() {
        if (task != null && task?.isCancelled == false) {
            task?.cancel(true);
        }
        pools?.clear()
        lines.clear()
        task = ProcessTask()
        task?.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR)
    }

    override fun onClick(view: View) {
        if (action == null) return
        val state: ViewState = view.tag as ViewState
        action?.onItemClick(state.item.getIndex(), view)
    }

    override fun onLongClick(view: View): Boolean {
        if (action == null) return false
        val state: ViewState = view.tag as ViewState
        return action?.onItemLongClick(state.item.getIndex(), view) ?: false
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        Log.d(TAG, "onBindViewHolder($position)")
        val line: GridLineInfo = lines[position] ?: return
        val items: ArrayList<GridItemInfo> = ArrayList(line.getItems())
        val layout: LinearLayout = initLinearLayout(holder.getItemView())
        var splitCount = 0
        var itemIndex = 0
        var lineWidth = line.getWidth()
        var lineHeight = line.getHeight()
        val isHorizontal = requestGirdViewConfig().isHorizontal
        while (items.isNotEmpty() && splitCount < requestGirdViewConfig().splitCount) {
            val currentItem: GridItemInfo = items[itemIndex]
            if (isHorizontal && lineWidth == 0 || !isHorizontal && lineHeight == 0) {
                splitCount++
                itemIndex = 0
                lineWidth = line.getWidth()
                lineHeight = line.getHeight()
                // No more space in this column. Move to next one
                continue
            }
            // Is there enough space in this column to accommodate currentItem?
            val isBind = if (isHorizontal) {
                lineWidth >= currentItem.getSize().width
            } else {
                lineHeight >= currentItem.getSize().height
            }
            if (!isBind && splitCount < requestGirdViewConfig().splitCount && items.size == 1) {
                splitCount++
                itemIndex = 0
                lineWidth = line.getWidth()
                lineHeight = line.getHeight()
                continue
            }
            if (isBind) {
                items.remove(currentItem)
                val actualIndex = currentItem.getIndex()
                val viewType: Int = getItemViewType(actualIndex)
                var pool: ObjectPool<ItemViewHolder>? = holders[viewType]
                if (pool == null) {
                    pool = ObjectPool()
                    holders[viewType] = pool
                }
                var viewHolder = pool.get()
                if (viewHolder == null) {
                    viewHolder = onCreateItemViewHolder(holder.getItemView(), viewType, actualIndex)
                }
                onBindItemViewHolder(holder.getItemView(), viewHolder, actualIndex)
                val view = viewHolder.itemView
                view.tag = ViewState(viewType, currentItem, viewHolder)
                view.setOnClickListener(this)
                view.setOnLongClickListener(this)
                if (isHorizontal) {
                    lineWidth -= currentItem.getSize().width
                } else {
                    lineHeight -= currentItem.getSize().height
                }
                itemIndex = 0
                view.layoutParams = LinearLayout.LayoutParams(
                    getWidth(currentItem.getSize()),
                    getHeight(currentItem.getSize())
                )
                val childLayout: LinearLayout = findLinearLayout(layout, splitCount)
                childLayout.addView(view)
            } else if (itemIndex < items.size - 1) {
                itemIndex++
            } else {
                break
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        Log.d(TAG, "onCreateViewHolder()")
        val context = parent.context
        val layout = LinearLayout(context, null)
        layout.showDividers = LinearLayout.SHOW_DIVIDER_MIDDLE
        layout.dividerDrawable = GradientDrawable().apply {
            setSize(requestGirdViewConfig().spaceWidth, ViewGroup.LayoutParams.MATCH_PARENT)
        }
        val layoutParams = AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT)
        layout.layoutParams = layoutParams
        return BaseViewHolder(layout)
    }

    private fun initLinearLayout(layout: LinearLayout): LinearLayout {
        // Clear all layout children before starting
        val childCount: Int = layout.childCount
        for (j in 0 until childCount) {
            val tempChild = layout.getChildAt(j) as LinearLayout
            pools?.put(tempChild)
            val innerChildCount = tempChild.childCount
            for (k in 0 until innerChildCount) {
                val innerView = tempChild.getChildAt(k)
                val viewState = innerView.tag as ViewState
                val pool: ObjectPool<ItemViewHolder>? = holders[viewState.viewType]
                pool?.put(viewState.viewHolder)
            }
            tempChild.removeAllViews()
        }
        layout.removeAllViews()
        layout.clipChildren = false
        layout.clipToPadding = false

        if (requestGirdViewConfig().isHorizontal) {
            layout.orientation = LinearLayout.VERTICAL
            layout.layoutParams = AbsListView.LayoutParams(
                AbsListView.LayoutParams.WRAP_CONTENT,
                AbsListView.LayoutParams.MATCH_PARENT
            )
            layout.dividerDrawable = GradientDrawable().apply {
                setSize(0, requestGirdViewConfig().spaceWidth)
            }
        } else {
            layout.orientation = LinearLayout.HORIZONTAL
            layout.layoutParams = AbsListView.LayoutParams(
                AbsListView.LayoutParams.MATCH_PARENT,
                AbsListView.LayoutParams.WRAP_CONTENT
            )
            layout.dividerDrawable = GradientDrawable().apply {
                setSize(requestGirdViewConfig().spaceWidth, 0)
            }
        }
        return layout
    }

    private fun findLinearLayout(parent: LinearLayout, childIndex: Int): LinearLayout {
        var childLayout: LinearLayout? = parent.getChildAt(childIndex) as LinearLayout?
        if (childLayout == null) {
            childLayout = pools?.get()!!
            childLayout.showDividers = LinearLayout.SHOW_DIVIDER_MIDDLE
            childLayout.clipChildren = false
            childLayout.clipToPadding = false
            if (requestGirdViewConfig().isHorizontal) {
                childLayout.orientation = LinearLayout.HORIZONTAL
                childLayout.layoutParams = AbsListView.LayoutParams(
                    AbsListView.LayoutParams.MATCH_PARENT,
                    AbsListView.LayoutParams.WRAP_CONTENT
                )
                childLayout.dividerDrawable = GradientDrawable().apply {
                    setSize(requestGirdViewConfig().spaceWidth, 0)
                }
            } else {
                childLayout.orientation = LinearLayout.VERTICAL
                childLayout.layoutParams = AbsListView.LayoutParams(
                    AbsListView.LayoutParams.WRAP_CONTENT,
                    AbsListView.LayoutParams.MATCH_PARENT
                )
                childLayout.dividerDrawable = GradientDrawable().apply {
                    setSize(0, requestGirdViewConfig().spaceWidth)
                }
            }
            parent.addView(childLayout)
        }
        return childLayout
    }

    private fun getHeight(item: GridItemSize): Int {
        val boxExtent: Int = requestGirdViewConfig().boxWidth * item.height
        val boxSpace: Int = requestGirdViewConfig().spaceWidth * (item.height - 1)
        // when the item spans multiple rows, we need to account for the vertical padding
        // and add that to the total final height
        return boxExtent + boxSpace
    }

    private fun getWidth(item: GridItemSize): Int {
        val boxExtent: Int = requestGirdViewConfig().boxWidth * item.width
        val boxSpace: Int = requestGirdViewConfig().spaceWidth * (item.width - 1)
        // when the item spans multiple columns, we need to account for the horizontal padding
        // and add that to the total final width
        return boxExtent + boxSpace
    }

    final override fun getItemCount(): Int {
        return lines.size
    }

    abstract fun getRealItemCount(): Int

    abstract fun getItem(position: Int): GridItemSize

    abstract fun onCreateItemViewHolder(parent: ViewGroup, viewType: Int, position: Int): ItemViewHolder

    abstract fun onBindItemViewHolder(parent: ViewGroup, holder: ItemViewHolder, position: Int)

    abstract class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    class BaseViewHolder(itemView: LinearLayout) : RecyclerView.ViewHolder(itemView) {
        fun getItemView(): LinearLayout = itemView as LinearLayout
    }

    private inner class ProcessTask : AsyncTask<Void, Void, List<GridLineInfo>>() {
        @SuppressLint("NotifyDataSetChanged")
        override fun onPostExecute(items: List<GridLineInfo>) {
            for (item in items) {
                lines[itemCount] = item
            }
            notifyDataSetChanged()
        }

        override fun doInBackground(vararg p0: Void?): List<GridLineInfo> {
            val cache: MutableList<GridItemInfo> = mutableListOf()
            for (i in 0 until getRealItemCount()) {
                try {
                    cache.add(GridItemInfo(i, getItem(i)))
                } catch (e: CursorIndexOutOfBoundsException) {
                    e.printStackTrace()
                }
            }
            return if (requestGirdViewConfig().isHorizontal) {
                OrientationHandler.HORIZONTAL.setReordering(requestGirdViewConfig().isReordering)
                OrientationHandler.HORIZONTAL.calculateLines(cache, requestGirdViewConfig().splitCount)
            } else {
                OrientationHandler.VERTICAL.setReordering(requestGirdViewConfig().isReordering)
                OrientationHandler.VERTICAL.calculateLines(cache, requestGirdViewConfig().splitCount)
            }
        }

    }

    private inner class ViewState constructor(
        val viewType: Int,
        val item: GridItemInfo,
        val viewHolder: ItemViewHolder
    )
}