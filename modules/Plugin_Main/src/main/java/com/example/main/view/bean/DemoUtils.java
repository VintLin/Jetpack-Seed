package com.example.main.view.bean;

import java.util.ArrayList;
import java.util.List;

import kotlin.random.Random;

public final class DemoUtils {
    int currentOffset;

    public DemoUtils() {
    }

    public List<DemoItem> standItems() {
        List<DemoItem> items = new ArrayList<>();
        items.add(new DemoItem(2, 2, 0));
        items.add(new DemoItem(1, 1, 1));
        items.add(new DemoItem(1, 1, 2));
        items.add(new DemoItem(1, 1, 3));
        items.add(new DemoItem(2, 1, 4));
        items.add(new DemoItem(1, 2, 5));
        items.add(new DemoItem(1, 1, 6));
        items.add(new DemoItem(1, 1, 7));
        items.add(new DemoItem(1, 1, 8));
        items.add(new DemoItem(1, 1, 9));
        return items;
    }

    public List<DemoItem> standOneItems() {
        List<DemoItem> items = new ArrayList<>();
        items.add(new DemoItem(1, 1, 0));
        items.add(new DemoItem(1, 1, 1));
        items.add(new DemoItem(1, 1, 2));
        items.add(new DemoItem(1, 1, 3));
        items.add(new DemoItem(4, 2, 4));
        return items;
    }

    public List<DemoItem> standTwoItems() {
        List<DemoItem> items = new ArrayList<>();
        items.add(new DemoItem(1, 1, 0));
        items.add(new DemoItem(2, 1, 1));
        items.add(new DemoItem(1, 1, 2));

        items.add(new DemoItem(1, 1, 3));
        items.add(new DemoItem(4, 1, 4));
        items.add(new DemoItem(1, 1, 5));
        items.add(new DemoItem(2, 1, 6));

        items.add(new DemoItem(2, 1, 7));
        items.add(new DemoItem(1, 1, 8));
        items.add(new DemoItem(1, 1, 9));

        items.add(new DemoItem(4, 1, 10));
        items.add(new DemoItem(1, 1, 11));
        items.add(new DemoItem(1, 1, 12));
        items.add(new DemoItem(2, 1, 13));

        items.add(new DemoItem(1, 1, 14));
        items.add(new DemoItem(1, 1, 15));
        items.add(new DemoItem(1, 2, 16));
        items.add(new DemoItem(1, 1, 17));
        items.add(new DemoItem(1, 1, 18));

        items.add(new DemoItem(2, 2, 19));

        items.add(new DemoItem(2, 1, 20));
        items.add(new DemoItem(1, 2, 21));

        items.add(new DemoItem(1, 1, 22));
        items.add(new DemoItem(2, 1, 23));
        items.add(new DemoItem(1, 2, 24));
        items.add(new DemoItem(1, 1, 25));
        return items;
    }

    public List<DemoItem> randomItems(int qty) {
        List<DemoItem> items = new ArrayList<>();

        for (int i = 0; i < qty; i++) {
            int width = Random.Default.nextInt(1, 3);
            int height = Random.Default.nextInt(1, 3);
            DemoItem item = new DemoItem(width, height, currentOffset + i);
            items.add(item);
        }

        currentOffset += qty;

        return items;
    }
}
