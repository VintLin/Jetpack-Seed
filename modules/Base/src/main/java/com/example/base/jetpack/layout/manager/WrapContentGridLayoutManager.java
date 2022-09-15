package com.example.base.jetpack.layout.manager;

import android.content.Context;
import android.util.AttributeSet;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class WrapContentGridLayoutManager extends GridLayoutManager {
    public WrapContentGridLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
    }

    public WrapContentGridLayoutManager(Context context, int spanCount, int orientation, boolean reverseLayout) {
        super(context, spanCount, orientation, reverseLayout);
    }

    public WrapContentGridLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        try {
            super.onLayoutChildren(recycler, state);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }

    public static class OneVertical extends WrapContentGridLayoutManager {
        public OneVertical(Context context, int spanCount) {
            super(context, spanCount);
        }

        public OneVertical(Context context, int spanCount, int orientation, boolean reverseLayout) {
            super(context, spanCount, orientation, reverseLayout);
        }

        public OneVertical(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
            setOrientation(LinearLayoutManager.HORIZONTAL);
        }
    }

    public static class TwoHorizontal extends WrapContentGridLayoutManager {
        public TwoHorizontal(Context context, int spanCount) {
            super(context, spanCount);
        }

        public TwoHorizontal(Context context, int spanCount, int orientation, boolean reverseLayout) {
            super(context, spanCount, orientation, reverseLayout);
        }

        public TwoHorizontal(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
            setSpanCount(2);
            setOrientation(LinearLayoutManager.HORIZONTAL);
        }
    }

    public static class Four extends WrapContentGridLayoutManager {
        public Four(Context context, int spanCount) {
            super(context, spanCount);
        }

        public Four(Context context, int spanCount, int orientation, boolean reverseLayout) {
            super(context, spanCount, orientation, reverseLayout);
        }

        public Four(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
            setSpanCount(4);
        }
    }

    public static class Three extends WrapContentGridLayoutManager {

        public Three(Context context, int spanCount) {
            super(context, spanCount);
        }

        public Three(Context context, int spanCount, int orientation, boolean reverseLayout) {
            super(context, spanCount, orientation, reverseLayout);
        }

        public Three(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
            setSpanCount(3);
        }
    }

}
