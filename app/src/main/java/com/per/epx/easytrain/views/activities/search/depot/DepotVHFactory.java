package com.per.epx.easytrain.views.activities.search.depot;

import android.content.Context;
import android.support.annotation.IntDef;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.per.epx.easytrain.R;
import com.per.epx.easytrain.adapters.SpanGridAdapter;
import com.per.epx.easytrain.adapters.base.IHolderApi;
import com.per.epx.easytrain.adapters.base.RcvAdapterBase;
import com.per.epx.easytrain.helpers.SmoothHelper;
import com.per.epx.easytrain.interfaces.IAction1;
import com.per.epx.easytrain.models.Depot;
import com.per.epx.easytrain.models.getset.IGetter0;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@SuppressWarnings("WeakerAccess")
public class DepotVHFactory extends SpanGridAdapter.VHFactory {
    private IAction1<PlaceItem> onPlaceClicked;
    private IAction1<IndexItem> onIndexClicked;
    private int selectedPosition = -2;
    public DepotVHFactory(IAction1<PlaceItem> onPlaceClicked,
                          IAction1<IndexItem> onIndexClicked) {
        this.onIndexClicked = onIndexClicked;
        this.onPlaceClicked = onPlaceClicked;
    }

    private class PlaceActionListener implements IHolderApi.ItemClickListener{
        private final DepotVHFactory.PlaceItem item;
        public PlaceActionListener(DepotVHFactory.PlaceItem item){
            this.item = item;
        }

        @Override
        public void itemClicked(View view, int layoutPosition, int adapterPostion) {
            if(onPlaceClicked != null){
                onPlaceClicked.invoke(item);
            }
        }
    }

    @Override
    protected RcvAdapterBase.VHBase createViewHolder(final Context context, final IGetter0<RecyclerView> rcvGet, ViewGroup parent, int viewType) {
        switch (viewType){
            case SpanItemBase.TYPE_HEADER:{
                return new RcvAdapterBase.VHLayoutBase<HeaderItem>(context, R.layout.template_header_depot) {
                    @Override
                    protected void onBindData(IHolderApi holderApi, DepotVHFactory.HeaderItem data, RcvAdapterBase.Payload payload) {
                        holderApi.setText(R.id.tv_places_header, data.label);
                    }
                };
            }
            case SpanItemBase.TYPE_HOT:{
                return new RcvAdapterBase.VHLayoutBase<HotItem>(context, R.layout.template_item_depot_wrap) {
                    @Override
                    protected void onBindData(IHolderApi holderApi, final DepotVHFactory.HotItem data, RcvAdapterBase.Payload payload) {
                        holderApi.setText(R.id.tv_item_normal_label, data.label)
                                .setOnItemClickListener(new DepotVHFactory.PlaceActionListener(data));
                    }
                };
            }
            case SpanItemBase.TYPE_MOST:{
                return new RcvAdapterBase.VHLayoutBase<MostItem>(context, R.layout.template_item_depot_wrap) {
                    @Override
                    protected void onBindData(IHolderApi holderApi, DepotVHFactory.MostItem data, RcvAdapterBase.Payload payload) {
                        holderApi.setText(R.id.tv_item_normal_label, data.label)
                                .setOnItemClickListener(new DepotVHFactory.PlaceActionListener(data));
                    }
                };
            }
            case SpanItemBase.TYPE_INDEX:{
                return new RcvAdapterBase.VHLayoutBase<IndexItem>(context, R.layout.template_item_depot_wrap) {
                    @Override
                    protected void onBindData(final IHolderApi holderApi, final DepotVHFactory.IndexItem data, RcvAdapterBase.Payload payload) {
                        holderApi.setText(R.id.tv_item_normal_label, data.label);
                        if(selectedPosition != -2 && getAdapterPosition() == selectedPosition){
                            holderApi.setBackgroundResource(R.id.tv_item_normal_label, R.drawable.round_border_fill);
                        }else{
                            holderApi.setBackgroundResource(R.id.tv_item_normal_label, R.drawable.round_border);
                        }
                        holderApi.setOnItemClickListener(new IHolderApi.ItemClickListener() {
                            @Override
                            public void itemClicked(View view, int layoutPosition, int adapterPosition) {
                                selectedPosition = layoutPosition;
                                if(rcvGet != null && rcvGet.get() != null){
                                    //Get the first item of type which is current type
                                    RecyclerView rcvTemp = rcvGet.get();
                                    RecyclerView.Adapter adapter = rcvTemp.getAdapter();
                                    int firstOfType = 0, typeCount = 0;
                                    for(int m = 0; m < adapter.getItemCount(); m++){
                                        if(adapter.getItemViewType(m) == SpanItemBase.TYPE_INDEX){
                                            typeCount++;
                                            if(typeCount == 1){
                                                firstOfType = m;
                                            }
                                        }
                                    }
                                    adapter.notifyItemRangeChanged(firstOfType, typeCount);
                                    //First visible item
                                    int firstVisiblePos = rcvTemp.getChildLayoutPosition(rcvTemp.getChildAt(0));
                                    //Last visible item
                                    int lastVisiblePos = rcvTemp.getChildLayoutPosition(rcvTemp.getChildAt(rcvTemp.getChildCount() - 1));
                                    SmoothHelper.smoothMoveToPosition(rcvTemp, firstOfType, firstVisiblePos, lastVisiblePos);
                                }
                                if(onIndexClicked != null){
                                    onIndexClicked.invoke(data);
                                }
                            }
                        });
                    }
                };
            }
            case SpanItemBase.TYPE_NORMAL:{
                return new RcvAdapterBase.VHLayoutBase<NormalItem>(context, R.layout.template_item_depot_whole_line) {
                    @Override
                    protected void onBindData(IHolderApi holderApi, NormalItem data, RcvAdapterBase.Payload payload) {
                        holderApi.setText(R.id.tv_place_name, data.label)
                                .setOnItemClickListener(new DepotVHFactory.PlaceActionListener(data));
                    }
                };
            }
            default:
                throw new UnsupportedOperationException("Oops!Unsupported viewType of " + viewType);
        }
    }

    public static class SpanItemBase implements SpanGridAdapter.Item {
        public static final int TYPE_HEADER = 1;
        public static final int TYPE_MOST = 2;
        public static final int TYPE_HOT = 3;
        public static final int TYPE_INDEX = 4;
        public static final int TYPE_NORMAL = 5;

        @IntDef({TYPE_HEADER, TYPE_MOST, TYPE_HOT, TYPE_INDEX, TYPE_NORMAL})
        @Retention(RetentionPolicy.SOURCE)
        public @interface Type{}

        public final String label;
        public final int span;
        public final @SpanItemBase.Type
        int type;
        public SpanItemBase(String label, @SpanItemBase.Type int type, int span){
            this.label = label;
            this.type = type;
            this.span = span;
        }

        @Override
        public int getViewType() {
            return this.type;
        }

        @Override
        public int getSpanSize() {
            return this.span;
        }

    }

    public static class HeaderItem extends SpanItemBase {
        public HeaderItem(String label) {
            super(label,TYPE_HEADER, 6);
        }
    }

    public static class IndexItem extends SpanItemBase {
        public IndexItem(String label) {
            super(label,TYPE_INDEX,1);
        }
    }

    public static abstract class PlaceItem extends SpanItemBase {
        public final Depot entity;

        public PlaceItem(Depot entity, String label, int type, int span) {
            super(label, type, span);
            this.entity = entity;
        }
    }

    public static class HotItem extends PlaceItem {
        public HotItem(Depot entity, String label) {
            super(entity, label, TYPE_HOT, 2);
        }
    }

    public static class MostItem extends PlaceItem {
        public MostItem(Depot entity, String label) {
            super(entity, label, TYPE_MOST, 2);
        }
    }

    public static class NormalItem extends PlaceItem {
        public NormalItem(Depot entity, String label){
            super(entity, label, TYPE_NORMAL, 6);
        }
    }

}
