package com.per.epx.easytrain.views.helpers;

import android.app.Activity;
import android.support.annotation.IdRes;
import android.widget.Checkable;

public class CheckPair{
    private @IdRes
    int id1;
    private @IdRes int id2;
    private Checkable c1;
    private Checkable c2;
    public CheckPair(Activity activity, @IdRes int id1, @IdRes int id2){
        this.id1 = id1;
        this.id2 = id2;
        this.c1 = activity.findViewById(id1);
        this.c2 = activity.findViewById(id2);
    }

    @SuppressWarnings("unused")
    public CheckPair(@IdRes int id1, Checkable c1, @IdRes int id2, Checkable c2){
        this.id1 = id1;
        this.id2 = id2;
        this.c1 = c1;
        this.c2 = c2;
    }

    public @IdRes int uncheckedOrDefaultId(){
        if(!c1.isChecked()){
            return id1;
        }
        return id2;
    }

    @SuppressWarnings("unused")
    public Checkable uncheckedOrDefault(){
        if(!c1.isChecked()){
            return c1;
        }
        return c2;
    }

    public void check(@IdRes int checkedId){
        c1.setChecked(id1 == checkedId);
        c2.setChecked(id2 == checkedId);
    }

    public void uncheckedAll(){
        c1.setChecked(false);
        c2.setChecked(false);
    }
}
