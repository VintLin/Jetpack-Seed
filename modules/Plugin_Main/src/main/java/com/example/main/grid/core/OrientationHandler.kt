package com.example.main.grid.core

import com.example.main.grid.bean.GridItemInfo
import com.example.main.grid.bean.GridLineInfo

interface OrientationHandler {

    companion object {
        val HORIZONTAL = object : OrientationHandler {
            private var isReordering: Boolean = false

            override fun setReordering(state: Boolean) {
                isReordering = state
            }

            override fun isReordering(): Boolean {
                return isReordering
            }

            override fun calculateLines(cache: MutableList<GridItemInfo>, splitCount: Int): List<GridLineInfo> {
                val lines: MutableList<GridLineInfo> = mutableListOf()
                while (cache.isNotEmpty()) {
                    val line: GridLineInfo = calculateLine(cache, splitCount)
                    val items: List<GridItemInfo> = line.getItems()
                    if (items.isEmpty()) {
                        break;
                    }
                    for (item in items) {
                        cache.remove(item)
                    }
                    lines.add(line)
                }
                return lines
            }

            override fun calculateLine(items: List<GridItemInfo>, maxExtent: Int): GridLineInfo {
                val fitItems: MutableList<GridItemInfo> = mutableListOf()
                var currentIndex = 0
                var maxWidth = 1
                var maxArea = maxExtent * maxWidth
                while (0 < maxArea && currentIndex < items.size) {
                    val item = items[currentIndex++]
                    val itemArea = item.getSize().height * item.getSize().width
                    if (maxWidth < item.getSize().width) {
                        // restart with double height
                        fitItems.clear()
                        maxWidth = item.getSize().width
                        currentIndex = 0
                        maxArea = maxExtent * item.getSize().width
                    } else if (maxArea >= itemArea && isAdd(item, fitItems, maxWidth, maxExtent)) {
                        maxArea -= itemArea
                        fitItems.add(item)
                    } else if (!isReordering()) {
                        break
                    }
                }
                return GridLineInfo(maxWidth, maxExtent, fitItems)
            }

            override fun isAdd(addItem: GridItemInfo, items: List<GridItemInfo>, maxWidth: Int, maxHeight: Int): Boolean {
                var currentWidth = 0
                var currentHeight = 0
                var maxItemHeight = 0
                for (index in 0 until items.size + 1) {
                    var itemWidth: Int
                    var itemHeight: Int
                    if (index == items.size) {
                        itemWidth = addItem.getSize().width
                        itemHeight = addItem.getSize().height
                    } else {
                        itemWidth = items[index].getSize().width
                        itemHeight = items[index].getSize().height
                    }
                    if (maxItemHeight == 0) maxItemHeight = itemHeight
                    if (currentWidth + itemWidth < maxWidth && index != items.size) {
                        currentWidth += itemWidth
                        if (maxItemHeight < itemHeight) {
                            maxItemHeight = itemHeight
                        }
                    } else {
                        currentHeight += maxItemHeight
                        maxItemHeight = 0
                        currentWidth = 0
                    }
                }
                return currentHeight <= maxHeight;
            }
        }

        val VERTICAL = object : OrientationHandler {
            private var isReordering: Boolean = false

            override fun setReordering(state: Boolean) {
                isReordering = state
            }

            override fun isReordering(): Boolean {
                return isReordering
            }

            override fun calculateLines(cache: MutableList<GridItemInfo>, splitCount: Int): List<GridLineInfo> {
                val lines: MutableList<GridLineInfo> = mutableListOf()
                while (cache.isNotEmpty()) {
                    val line: GridLineInfo = calculateLine(cache, splitCount)
                    val items: List<GridItemInfo> = line.getItems()
                    if (items.isEmpty()) {
                        break;
                    }
                    for (item in items) {
                        cache.remove(item)
                    }
                    lines.add(line)
                }
                return lines
            }

            override fun calculateLine(items: List<GridItemInfo>, maxExtent: Int): GridLineInfo {
                val fitItems: MutableList<GridItemInfo> = mutableListOf()
                var currentIndex = 0
                var maxHeight = 1
                var maxArea = maxExtent * maxHeight
                while (0 < maxArea && currentIndex < items.size) {
                    val item = items[currentIndex++]
                    val itemArea = item.getSize().height * item.getSize().width
                    if (maxHeight < item.getSize().height) {
                        // restart with double height
                        fitItems.clear()
                        maxHeight = item.getSize().height
                        currentIndex = 0
                        maxArea = maxExtent * item.getSize().height
                    } else if (maxArea >= itemArea && isAdd(item, fitItems, maxExtent, maxHeight)) {
                        maxArea -= itemArea
                        fitItems.add(item)
                    } else if (!isReordering()) {
                        break
                    }
                }
                return GridLineInfo(maxExtent, maxHeight, fitItems)
            }

            override fun isAdd(addItem: GridItemInfo, items: List<GridItemInfo>, maxWidth: Int, maxHeight: Int): Boolean {
                var currentWidth = 0
                var currentHeight = 0
                var maxItemWidth = 0
                for (index in 0 until items.size + 1) {
                    var itemWidth: Int
                    var itemHeight: Int
                    if (index == items.size) {
                        itemWidth = addItem.getSize().width
                        itemHeight = addItem.getSize().height
                    } else {
                        itemWidth = items[index].getSize().width
                        itemHeight = items[index].getSize().height
                    }
                    if (maxItemWidth == 0) maxItemWidth = itemWidth
                    if (currentHeight + itemHeight < maxHeight && index != items.size) {
                        currentHeight += itemHeight
                        if (maxItemWidth < itemWidth) {
                            maxItemWidth = itemWidth
                        }
                    } else {
                        currentWidth += maxItemWidth
                        maxItemWidth = 0
                        currentHeight = 0
                    }
                }
                return currentWidth <= maxWidth;
            }
        }
    }

    fun setReordering(state: Boolean)

    fun isReordering(): Boolean

    fun isAdd(addItem: GridItemInfo, items: List<GridItemInfo>, maxWidth: Int, maxHeight: Int): Boolean

    fun calculateLines(cache: MutableList<GridItemInfo>, splitCount: Int): List<GridLineInfo>

    fun calculateLine(items: List<GridItemInfo>, maxExtent: Int): GridLineInfo
}