package com.example.main.grid.bean

import android.os.Parcel
import android.os.Parcelable
import com.example.main.grid.bean.GridItemInfo

class GridLineInfo() : Parcelable {

    private lateinit var items: MutableList<GridItemInfo>
    private var height = 0
    private var width = 0

    constructor(width: Int, height: Int, items: MutableList<GridItemInfo>) : this() {
        this.width = width
        this.height = height
        this.items = items
    }

    constructor(parcel: Parcel) : this() {
        height = parcel.readInt()
        width = parcel.readInt()
        val totalItems = parcel.readInt()
        items = mutableListOf()
        val classLoader = GridItemSize::class.java.classLoader
        for (i in 0 until totalItems) {
            items.add(GridItemInfo(parcel.readInt(), parcel.readParcelable<Parcelable>(classLoader) as GridItemSize))
        }
    }

    fun getItems(): List<GridItemInfo> {
        return items
    }

    fun getHeight(): Int {
        return height
    }

    fun getWidth(): Int {
        return width
    }

    fun getArea(): Int {
        return width * height
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(height)
        dest.writeInt(width)
        dest.writeInt(items.size)
        for (item in items) {
            dest.writeInt(item.getIndex())
            dest.writeParcelable(item.getSize(), 0)
        }
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<GridLineInfo> {
        override fun createFromParcel(parcel: Parcel): GridLineInfo {
            return GridLineInfo(parcel)
        }

        override fun newArray(size: Int): Array<GridLineInfo?> {
            return arrayOfNulls(size)
        }
    }
}