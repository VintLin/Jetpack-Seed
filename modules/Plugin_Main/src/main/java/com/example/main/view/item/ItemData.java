package com.example.main.view.item;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.example.main.grid.bean.GridItemSize;

public class ItemData implements GridItemSize {
    private int width;
    private int height;
    private int position;

    public ItemData() {
        this(1, 1, 0);
    }

    public ItemData(int width, int height, int position) {
        this.width = width;
        this.height = height;
        this.position = position;
    }

    public ItemData(Parcel in) {
        readFromParcel(in);
    }


    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public int getWidth() {
        return width;
    }

    public int getPosition() {
        return position;
    }

    @Override
    public String toString() {
        return String.format("%s: %sx%s", position, height, width);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    private void readFromParcel(Parcel in) {
        width = in.readInt();
        height = in.readInt();
        position = in.readInt();
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(width);
        dest.writeInt(height);
        dest.writeInt(position);
    }

    public static final Parcelable.Creator<ItemData> CREATOR = new Parcelable.Creator<ItemData>() {
        @Override
        public ItemData createFromParcel(@NonNull Parcel in) {
            return new ItemData(in);
        }

        @Override
        @NonNull
        public ItemData[] newArray(int size) {
            return new ItemData[size];
        }
    };

}
