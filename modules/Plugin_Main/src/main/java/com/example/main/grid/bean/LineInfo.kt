package com.example.main.grid.bean

import android.os.Parcel
import android.os.Parcelable

class LineInfo() : Parcelable {

    private lateinit var items: MutableList<ItemInfo>
    private var height = 0
    private var width = 0

    constructor(width: Int, height: Int, items: MutableList<ItemInfo>) : this() {
        this.width = width
        this.height = height
        this.items = items
    }

    constructor(parcel: Parcel) : this() {
        height = parcel.readInt()
        width = parcel.readInt()
        val totalItems = parcel.readInt()
        items = mutableListOf()
        val classLoader = ItemSize::class.java.classLoader
        for (i in 0 until totalItems) {
            items.add(ItemInfo(parcel.readInt(), parcel.readParcelable<Parcelable>(classLoader) as ItemSize))
        }
    }

    fun getItems(): List<ItemInfo> {
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

    companion object CREATOR : Parcelable.Creator<LineInfo> {
        override fun createFromParcel(parcel: Parcel): LineInfo {
            return LineInfo(parcel)
        }

        override fun newArray(size: Int): Array<LineInfo?> {
            return arrayOfNulls(size)
        }
    }
}