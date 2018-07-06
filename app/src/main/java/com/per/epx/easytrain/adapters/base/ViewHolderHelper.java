package com.per.epx.easytrain.adapters.base;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

@SuppressWarnings("WeakerAccess,unused")
public class ViewHolderHelper implements IHolderApi {
    protected SparseArray<View> views = new SparseArray<>();
    protected View itemView;

    public ViewHolderHelper(View itemView) {
        this.itemView = itemView;
    }

    protected int getLayoutPosition(){
        return -1;
    }


    protected int getAdapterPosition(){
        return -1;
    }

    @Override
    public Context getContext() {
        return itemView.getContext();
    }

    @Override
    public void removeFromCache(int id) {
        views.remove(id);
    }

    @Override
    public void removeAll() {
        views.clear();
    }

    @Override
    public void initView() { }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends View> T getView(int id) {
        T view = null;
        try {
            view = (T) views.get(id);
            if(view == null){
                view = (T) itemView.findViewById(id);
                if(view != null){
                    //add to cache
                    views.put(id, View.class.cast(view));
                }
            }
        } catch (ClassCastException e) {
            e.printStackTrace();
            return view;
        }
        return view;
    }

    @Override
    public IHolderApi setText(@IdRes int id, CharSequence text) {
        TextView tvTarget = getView(id);
        if (tvTarget != null)
            tvTarget.setText(text);
        return this;
    }

    @Override
    public CharSequence getText(@IdRes int id) {
        TextView targetTxt = getView(id);
        if (targetTxt != null)
            return targetTxt.getText();
        return "";
    }

    @Override
    public IHolderApi setOnItemClickListener(final ItemClickListener listener) {
        if(listener == null){
            this.itemView.setOnClickListener(null);
        }else{
            this.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.itemClicked(v, getLayoutPosition(), getAdapterPosition());
                }
            });
        }
        return this;
    }

    @Override
    public IHolderApi setOnItemLongClickListener(final ItemLongClickListener listener) {
        if(listener == null){
            this.itemView.setOnLongClickListener(null);
        }else{
            this.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return listener.itemClicked(v, getLayoutPosition(), getAdapterPosition());
                }
            });
        }
        return this;
    }

    @Override
    public IHolderApi setTextColor(@IdRes int id, @ColorInt int color) {
        TextView targetTxt = getView(id);
        if (targetTxt != null)
            targetTxt.setTextColor(color);
        return this;
    }

    @Override
    public IHolderApi setBackgroundResource(@IdRes int id, @DrawableRes int bgRes) {
        View targetView = getView(id);
        if (targetView != null)
            targetView.setBackgroundResource(bgRes);
        return this;
    }

    @Override
    public IHolderApi setBackgroundColor(@IdRes int id, @ColorInt int color) {
        View targetView = getView(id);
        if (targetView != null)
            targetView.setBackgroundColor(color);
        return this;
    }

    @Override
    public IHolderApi setOnClickListener(@IdRes int id, View.OnClickListener onClickListener ) {
        View targetView = getView(id);
        if (targetView != null) {
            targetView.setOnClickListener(onClickListener);
        }
        return this;
    }

    @Override
    public IHolderApi setOnLongClickListener(@IdRes int id, View.OnLongClickListener onLongClickListener ) {
        View targetView = getView(id);
        if (targetView != null) {
            targetView.setOnLongClickListener(onLongClickListener);
        }
        return this;
    }

    @Override
    public IHolderApi setVisibility(@IdRes int id, int visibility ) {
        View targetView = getView(id);
        if (targetView != null) {
            targetView.setVisibility(visibility);
        }
        return this;
    }

    @Override
    public IHolderApi setImageSrc(@IdRes int id, @DrawableRes int res ) {
        ImageView targetImageView = getView(id);
        if (targetImageView != null)
            targetImageView.setImageResource(res);
        return this;
    }

    @Override
    public IHolderApi setImageBitmap(@IdRes int id, Bitmap bitmap ) {
        ImageView targetImageView = getView(id);
        if (targetImageView != null)
            targetImageView.setImageBitmap(bitmap);
        return this;
    }

    @Override
    public IHolderApi setImageDrawable(@IdRes int id, Drawable drawable ) {
        ImageView targetImageView = getView(id);
        if (targetImageView != null)
            targetImageView.setImageDrawable(drawable);
        return this;
    }
}