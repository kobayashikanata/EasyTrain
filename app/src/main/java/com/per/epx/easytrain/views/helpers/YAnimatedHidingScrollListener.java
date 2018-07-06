package com.per.epx.easytrain.views.helpers;

import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

public class YAnimatedHidingScrollListener extends HidingScrollListener {
    public YAnimatedHidingScrollListener(View view) {
        super(view);
    }

    @Override
    public void onHide(View defaultAnimated) {
        Resources resources = defaultAnimated.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        int height = dm.heightPixels;
        defaultAnimated.animate()
                .translationY(height - defaultAnimated.getHeight())
                .setInterpolator(new AccelerateInterpolator(2))
                .setDuration(800)
                .start();
    }

    @Override
    public void onShow(View defaultAnimated) {
        defaultAnimated.animate()
                .translationY(0)
                .setInterpolator(new DecelerateInterpolator(2))
                .setDuration(800)
                .start();
    }
}
