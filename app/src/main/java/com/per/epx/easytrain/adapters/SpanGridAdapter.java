package com.per.epx.easytrain.adapters;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.per.epx.easytrain.adapters.base.RcvListAdapterBase;
import com.per.epx.easytrain.models.getset.GetSetter0;
import com.per.epx.easytrain.models.getset.IGetter0;

import java.util.List;

public class SpanGridAdapter<T extends SpanGridAdapter.Item> extends RcvListAdapterBase<T> {
    private final GetSetter0<RecyclerView> mRecycler = new GetSetter0<>();

    public SpanGridAdapter(@NonNull IViewHolderFactory vhFactory, List<T> data) {
        super(vhFactory, data);
        if(VHFactory.class.isInstance(vhFactory)){
            ((VHFactory)vhFactory).setRvGetter(mRecycler);
        }
    }

    public void removeTypeOf(int viewType){
        for(int i = data().size() - 1; i >= 0; i--){
            if(data().get(i).getViewType() == viewType){
                data().remove(i);
                notifyItemRemoved(i);
            }
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecycler.set(recyclerView);
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager instanceof GridLayoutManager) {
            final GridLayoutManager gridLayoutManager = (GridLayoutManager) manager;
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return getItem(position).getSpanSize();
                }
            });
        }
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        mRecycler.set(null);
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getViewType();
    }

    public interface Item {
        int getViewType();
        int getSpanSize();
    }

    public static class ItemDecoration extends RecyclerView.ItemDecoration{
        private final boolean isEdgeNeed;
        private final int columnSize;
        private final int space;

        public ItemDecoration(int columnSize, int space){
            this(true, columnSize, space);
        }

        public ItemDecoration(boolean isEdgeNeed, int columnSize, int space){
            this.isEdgeNeed = isEdgeNeed;
            this.columnSize = columnSize;
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            addSpaceToView(outRect, parent.getChildAdapterPosition(view), parent);
        }

        private void addSpaceToView(Rect outRect, int position, RecyclerView parent) {
            if (position == RecyclerView.NO_POSITION || parent == null || outRect == null)
                return;
            if(parent.getLayoutManager() instanceof GridLayoutManager){
                GridLayoutManager layoutMgr = (GridLayoutManager)parent.getLayoutManager();
                int itemSpan = layoutMgr.getSpanSizeLookup().getSpanSize(position);

                if (itemSpan == columnSize) {
                    //Skip the header which span whole row
                } else {
                    int spanCount = layoutMgr.getSpanCount();
                    int column = layoutMgr.getSpanSizeLookup().getSpanIndex(position, spanCount);
                    setupHorizonOffset(outRect, column, spanCount, space);
                    setupVerticalOffset(layoutMgr, outRect, position, itemSpan);
                }
            }
        }

        private void setupVerticalOffset(GridLayoutManager mgr, Rect outRect, int position, int itemSpan){
            int rowIndex = 0;
            for(int i = 0, spanLeft = 0; i <= position; i++){
                int spanCurrent = (i == position) ? itemSpan : mgr.getSpanSizeLookup().getSpanSize(i);
                if(spanCurrent == columnSize) {
                    rowIndex = 0;
                    spanLeft = 0;
                }else{
                    spanLeft += spanCurrent;
                    if(spanLeft > columnSize){
                        spanLeft -= columnSize;
                        rowIndex++;
                    }
                }
            }
            outRect.bottom = space;
            if(rowIndex == 0){
                outRect.top = space;
            }
        }

        private void setupHorizonOffset(Rect outRect, int column, int spanCount, int space) {
            if (isEdgeNeed) {
                outRect.left = space * (spanCount - column) / spanCount;
                outRect.right = space * (column + 1) / spanCount;
            } else {
                outRect.left = space * column / spanCount;
                outRect.right = space * (spanCount - 1 - column) / spanCount;
            }
        }
    }

    public static abstract class VHFactory implements IViewHolderFactory{
        private IGetter0<RecyclerView> rvGetter;

        private void setRvGetter(IGetter0<RecyclerView> rvGetter) {
            this.rvGetter = rvGetter;
        }

        @Override
        public final VHBase createViewHolder(ViewGroup parent, int viewType) {
            return createViewHolder(parent.getContext(), rvGetter, parent, viewType);
        }

        protected abstract VHBase createViewHolder(Context context, IGetter0<RecyclerView> recyclerViewGetter, ViewGroup parent, int viewType);
    }
}
