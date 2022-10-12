package com.example.main.view.bean;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.example.main.grid.bean.ItemSize;

public class DemoItem implements ItemSize {
    private int width;
    private int height;
    private int position;

    public DemoItem() {
        this(1, 1, 0);
    }

    public DemoItem(int width, int height, int position) {
        this.width = width;
        this.height = height;
        this.position = position;
    }

    public DemoItem(Parcel in) {
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

    /* Parcelable interface implementation */
    public static final Parcelable.Creator<DemoItem> CREATOR = new Parcelable.Creator<DemoItem>() {
        @Override
        public DemoItem createFromParcel(@NonNull Parcel in) {
            return new DemoItem(in);
        }

        @Override
        @NonNull
        public DemoItem[] newArray(int size) {
            return new DemoItem[size];
        }
    };

}
