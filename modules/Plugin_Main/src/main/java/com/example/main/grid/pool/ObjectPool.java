package com.example.main.grid.pool;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.example.main.grid.pool.ObjectPoolFactory;

import java.util.Stack;

public class ObjectPool<T> implements Parcelable {
    Stack<T> stack = new Stack<>();
    ObjectPoolFactory<T> factory;
    PoolStats stats;

    public ObjectPool(Parcel in) {
    }

    public ObjectPool() {
        stats = new PoolStats();
    }

    public ObjectPool(ObjectPoolFactory<T> factory) {
        this.factory = factory;
    }

    static class PoolStats {
        int size = 0;
        int hits = 0;
        int misses = 0;
        int created = 0;

        @SuppressLint("DefaultLocale")
        String getStats(String name) {
            return String.format("%s: size %d, hits %d, misses %d, created %d", name, size, hits, misses, created);
        }
    }

    public T get() {
        if (!stack.isEmpty()) {
            stats.hits++;
            stats.size--;
            return stack.pop();
        }

        stats.misses++;

        T object = factory != null ? factory.createObject() : null;

        if (object != null) {
            stats.created++;
        }

        return object;
    }

    public void put(T object) {
        stack.push(object);
        stats.size++;
    }

    public void clear() {
        stats = new PoolStats();
        stack.clear();
    }

    public String getStats(String name) {
        return stats.getStats(name);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, final int flags) {
    }

    public static final Creator<ObjectPool> CREATOR = new Creator<ObjectPool>() {

        @Override
        public ObjectPool createFromParcel(@NonNull Parcel in) {
            return new ObjectPool(in);
        }

        @Override
        @NonNull
        public ObjectPool[] newArray(int size) {
            return new ObjectPool[size];
        }
    };
}
