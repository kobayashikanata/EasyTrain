package com.per.epx.easytrain.helpers;

import android.support.v7.widget.RecyclerView;

public class SmoothHelper {

    public static void smoothMoveToPosition(RecyclerView rv, final int position, int firstItem, int lastItem) {
        if (position < firstItem) {
            //Jump position is before the fist visible item
            rv.smoothScrollToPosition(position);
        } else if (position <= lastItem) {
            //Jump position is after the fist visible item
            int movePosition = position - firstItem;
            if (movePosition >= 0 && movePosition < rv.getChildCount()) {
                int top = rv.getChildAt(movePosition).getTop();
                rv.smoothScrollBy(0, top);
            }
        } else {
            //Jump position is after the last visible item
            rv.smoothScrollToPosition(position);
        }
    }
}
