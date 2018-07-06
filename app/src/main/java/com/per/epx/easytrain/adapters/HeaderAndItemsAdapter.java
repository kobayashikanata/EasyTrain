package com.per.epx.easytrain.adapters;

import android.support.v7.widget.RecyclerView;

import com.oushangfeng.pinnedsectionitemdecoration.utils.FullSpanUtil;
import com.per.epx.easytrain.adapters.base.RcvAdapterBase;
import com.per.epx.easytrain.adapters.base.RcvListAdapterBase;

import java.util.List;

public class HeaderAndItemsAdapter<T> extends RcvListAdapterBase<T> {
    public static final int typeOfTopView = 2;
    public static final int typeOfHeader = 1;

    public HeaderAndItemsAdapter(RcvAdapterBase.IViewHolderFactory factory) {
        super(factory);
    }

    public HeaderAndItemsAdapter(RcvAdapterBase.IViewHolderFactory factory, List<T> dataList) {
        super(factory, dataList);
    }

    @Override
    public int getItemViewType(int position) {
        switch (position){
            case 0:return typeOfTopView;
            case 1:return typeOfHeader;
            default:return 0;
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        FullSpanUtil.onAttachedToRecyclerView(recyclerView, this, typeOfHeader);
    }

    @Override
    public void onViewAttachedToWindow(VHBase holder) {
        super.onViewAttachedToWindow(holder);
        FullSpanUtil.onViewAttachedToWindow(holder, this, typeOfHeader);
    }

}
