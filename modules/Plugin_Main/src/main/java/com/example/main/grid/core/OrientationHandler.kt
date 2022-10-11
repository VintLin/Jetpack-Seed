package com.example.main.grid.core

import android.util.Log
import com.example.main.grid.bean.ItemInfo
import com.example.main.grid.bean.LineInfo

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

            override fun calculateLines(cache: MutableList<ItemInfo>, splitCount: Int): List<LineInfo> {
                val lines: MutableList<LineInfo> = mutableListOf()
                while (cache.isNotEmpty()) {
                    val line: LineInfo = calculateLine(cache, splitCount)
                    val items: List<ItemInfo> = line.getItems()
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

            override fun calculateLine(items: List<ItemInfo>, maxExtent: Int): LineInfo {
                val fitItems: MutableList<ItemInfo> = mutableListOf()
                var currentIndex = 0
                var maxWidth = 1
                var maxArea = maxExtent * maxWidth
                while (0 < maxArea && currentIndex < items.size) {
                    val item = items[currentIndex]
                    val itemArea = item.getSize().height * item.getSize().width
                    if (maxWidth < item.getSize().width) {
                        // restart with double height
                        fitItems.clear()
                        maxWidth = item.getSize().width
                        currentIndex = 0
                        maxArea = maxExtent * item.getSize().width
                    } else if (maxArea >= itemArea) {
                        val info = calculateWidth(currentIndex, items, maxWidth)
                        maxArea -= info.getArea()
                        currentIndex += info.getItems().size
                        fitItems.addAll(info.getItems())
                    } else if (!isReordering()) {
                        break
                    }
                }
                Log.e("TAGTAG", "size: (${fitItems.size}) width: $maxExtent")
                return LineInfo(maxWidth, maxExtent, fitItems)
            }

            private fun calculateWidth(index: Int, items: List<ItemInfo>, maxExtent: Int): LineInfo {
                val fitItems: MutableList<ItemInfo> = mutableListOf()
                var currentIndex = index
                val maxHeight = items[index].getSize().height
                var maxArea = maxHeight * maxExtent
                var currentWidth = 0
                while (0 < maxArea && currentIndex < items.size) {
                    val item = items[currentIndex++]
                    val itemArea = item.getSize().height * item.getSize().width
                    currentWidth += item.getSize().width
                    Log.e("TAGTAG", "item: (${item.getSize()}) width: $currentWidth height: $maxHeight ${item.getSize().height}")
                    if (maxHeight < item.getSize().height || maxExtent < currentWidth) {
                        break;
                    } else if (maxArea >= itemArea) {
                        maxArea -= itemArea
                        Log.e("TAGTAG", "add item: (${item.getSize()})")
                        fitItems.add(item)
                    } else if (!isReordering()) {
                        break
                    }
                }
                Log.e("TAGTAG", "=====")
                return LineInfo(maxExtent, maxHeight, fitItems)
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

            override fun calculateLines(cache: MutableList<ItemInfo>, splitCount: Int): List<LineInfo> {
                val lines: MutableList<LineInfo> = mutableListOf()
                while (cache.isNotEmpty()) {
                    val line: LineInfo = calculateLine(cache, splitCount)
                    val items: List<ItemInfo> = line.getItems()
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

            override fun calculateLine(items: List<ItemInfo>, maxExtent: Int): LineInfo {
                val fitItems: MutableList<ItemInfo> = mutableListOf()
                var currentIndex = 0
                var maxHeight = 1
                var maxArea = maxExtent * maxHeight
                while (0 < maxArea && currentIndex < items.size) {
                    val item = items[currentIndex]
                    val itemArea = item.getSize().height * item.getSize().width
                    if (maxHeight < item.getSize().height) {
                        // restart with double height
                        fitItems.clear()
                        maxHeight = item.getSize().height
                        currentIndex = 0
                        maxArea = maxExtent * item.getSize().height
                    } else if (maxArea >= itemArea) {
                        val info = calculateHeight(currentIndex, items, maxHeight)
                        maxArea -= info.getArea()
                        currentIndex += info.getItems().size
                        fitItems.addAll(info.getItems())
                    } else if (!isReordering()) {
                        break
                    }
                }
                return LineInfo(maxExtent, maxHeight, fitItems)
            }

            private fun calculateHeight(index: Int, items: List<ItemInfo>, maxHeight: Int): LineInfo {
                val fitItems: MutableList<ItemInfo> = mutableListOf()
                var currentIndex = index
                val maxWidth = items[index].getSize().width
                var maxArea = maxWidth * maxHeight
                var currentHeight = 0
                while (0 < maxArea && currentIndex < items.size) {
                    val item = items[currentIndex++]
                    val itemArea = item.getSize().height * item.getSize().width
                    currentHeight += item.getSize().height
                    if (maxWidth < item.getSize().width || maxHeight < currentHeight) {
                        break;
                    } else if (maxArea >= itemArea) {
                        maxArea -= itemArea
                        fitItems.add(item)
                    } else if (!isReordering()) {
                        break
                    }
                }
                return LineInfo(maxWidth, maxHeight, fitItems)
            }
        }
    }

    fun setReordering(state: Boolean)

    fun isReordering(): Boolean

    fun calculateLines(cache: MutableList<ItemInfo>, splitCount: Int): List<LineInfo>

    fun calculateLine(items: List<ItemInfo>, maxExtent: Int): LineInfo
}