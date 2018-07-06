package com.per.epx.easytrain.views.fragments.base;

import android.util.SparseArray;

public abstract class BaseFragmentFactory {
    private SparseArray<BaseFragment> mBaseFragments = new SparseArray<BaseFragment>();

    public BaseFragment findFragment(int pos) {
        BaseFragment baseFragment = mBaseFragments.get(pos);

        if (baseFragment == null) {
            baseFragment = createNewFragment(pos);
            mBaseFragments.put(pos, baseFragment);
        }
        return baseFragment;
    }

    protected abstract BaseFragment createNewFragment(int position);
}
