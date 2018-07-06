package com.per.epx.easytrain.adapters.base;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("WeakerAccess,unused")
public class RcvListAdapterBase<DataType> extends RcvAdapterBase<DataType> {
    protected List<DataType> dataList;

    public RcvListAdapterBase(@NonNull IViewHolderFactory vmFactory) {
        this(vmFactory, new ArrayList<DataType>());
    }

    public RcvListAdapterBase(@NonNull IViewHolderFactory vmFactory, List<DataType> dataList) {
        super(vmFactory);
        this.dataList = dataList;
    }

    public List<DataType> data(){
        return this.dataList;
    }

    //Rebind data to source
    //Data controlling outside
    public void rebind(List<DataType> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    //Re-Add data to content
    //Data controlling by internal
    public void reAdd(List<DataType> dataList){
        if(this.dataList == null){
            this.dataList = new ArrayList<>();
        }else{
            this.dataList.clear();
        }
        this.dataList.addAll(dataList);
    }

    @Override
    public final int getItemCount() {
        return dataList == null ? 0 : dataList.size();
    }

    @Override
    protected final DataType getItem(int position) {
        return dataList.get(position);
    }


    public static class ItemMarginDecoration extends RecyclerView.ItemDecoration{
        private final int left;
        private final int right;
        private final int top;
        private final int bottom;

        public ItemMarginDecoration(int space){
            this(space, space, space, space);
        }

        public ItemMarginDecoration(int horizontal, int vertical){
            this(horizontal, horizontal, vertical, vertical);
        }

        public ItemMarginDecoration(int left, int right, int top, int bottom){
            this.left = left;
            this.right = right;
            this.top = top;
            this.bottom = bottom;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            if (parent == null || outRect == null)
                return;
            outRect.left = left;
            outRect.right = right;
            outRect.top = top;
            outRect.bottom = bottom;
        }
    }

}

