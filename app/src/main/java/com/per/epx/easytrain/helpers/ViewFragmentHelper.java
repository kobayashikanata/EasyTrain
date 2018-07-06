package com.per.epx.easytrain.helpers;

import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.per.epx.easytrain.interfaces.IFunc1;

public class ViewFragmentHelper {
    private final Fragment fragment;
    private final FragmentManager fragmentManager;

    public ViewFragmentHelper(FragmentManager fragmentManager, Fragment fragment){
        this.fragmentManager = fragmentManager;
        this.fragment = fragment;
    }

    public static void ensureFragmentExist(FragmentManager fragmentManager, @IdRes int id, IFunc1<Fragment> generateNew){
        //.findFragmentByTag(tag), way one
        Fragment frag = fragmentManager.findFragmentById(id);
        if(frag == null){
            frag = generateNew.invoke();
            fragmentManager.beginTransaction().add(id, frag).commit();
                    //.add(fragment, tag), way one
        }
    }

    public Fragment getFragment() {
        return fragment;
    }

    public void setVisible(boolean visible){
        if(visible){
            show();
        }else{
            hide();
        }
    }

    public void show(){
        if(fragment.isHidden()){
            fragmentManager.beginTransaction().show(fragment).commit();
        }
    }

    public void hide(){
        if(!fragment.isHidden()){
            fragmentManager.beginTransaction().hide(fragment).commit();
        }
    }
}
