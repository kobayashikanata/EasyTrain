package com.per.epx.easytrain.adapters.base;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.view.View;

@SuppressWarnings("unused")
public interface IHolderApi {
    Context getContext();
    void removeFromCache(@IdRes int id);
    void removeAll();
    void initView();
    <T extends View> T getView(@IdRes int id);

    IHolderApi setText(@IdRes int id, CharSequence text);
    CharSequence getText(@IdRes int id);

    IHolderApi setOnItemClickListener(ItemClickListener listener);
    IHolderApi setOnItemLongClickListener(ItemLongClickListener listener);
    IHolderApi setOnClickListener(@IdRes int id, View.OnClickListener onClickListener);
    IHolderApi setOnLongClickListener(@IdRes int id, View.OnLongClickListener onLongClickListener);

    IHolderApi setVisibility(@IdRes int id, int visibility);

    IHolderApi setImageSrc(@IdRes int id, @DrawableRes int res);
    IHolderApi setImageBitmap(@IdRes int id, Bitmap bitmap);
    IHolderApi setImageDrawable(@IdRes int id, Drawable drawable);

    IHolderApi setTextColor(@IdRes int id, @ColorInt int color);
    IHolderApi setBackgroundResource(@IdRes int id, @DrawableRes int bgRes);
    IHolderApi setBackgroundColor(@IdRes int id, @ColorInt int color);

    public interface ItemClickListener {
        void itemClicked(View view, int layoutPosition, int adapterPosition);
    }

    public interface ItemLongClickListener{
        boolean itemClicked(View view, int layoutPosition, int adapterPosition);
    }
}
