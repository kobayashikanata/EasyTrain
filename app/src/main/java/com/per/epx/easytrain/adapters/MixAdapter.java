package com.per.epx.easytrain.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import static android.support.v7.widget.RecyclerView.NO_ID;

public class MixAdapter<T extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<T> {
    private List<RecyclerView.Adapter<T>> adapters;
    private static final int VIEW_TYPE_OFFSET = 10000;

    public MixAdapter(){
        this.adapters = new ArrayList<>();
    }

    public MixAdapter(List<RecyclerView.Adapter<T>> adapters){
        this.adapters = adapters;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        for(RecyclerView.Adapter<T> adapter : adapters){
            adapter.onAttachedToRecyclerView(recyclerView);
        }
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        for(RecyclerView.Adapter<T> adapter : adapters){
            adapter.onDetachedFromRecyclerView(recyclerView);
        }
    }

    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(hasStableIds);
        for(RecyclerView.Adapter<T> adapter : adapters){
            adapter.setHasStableIds(hasStableIds);
        }
    }

    @Override
    public void registerAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
        super.registerAdapterDataObserver(observer);
        for(RecyclerView.Adapter<T> adapter : adapters){
            adapter.registerAdapterDataObserver(observer);
        }
    }

    @Override
    public void unregisterAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
        super.unregisterAdapterDataObserver(observer);
        for(RecyclerView.Adapter<T> adapter : adapters){
            adapter.unregisterAdapterDataObserver(observer);
        }
    }

    @Override
    public int getItemViewType(int position) {
        int offset = 0;
        int index = position;
        for(RecyclerView.Adapter<T> adapter : adapters){
            if (index < adapter.getItemCount()) {
                return offset + adapter.getItemViewType(index);
            } else {
                offset += VIEW_TYPE_OFFSET;
                index -= adapter.getItemCount();
            }
        }
        throw new IllegalArgumentException("not found view type in adapters");
    }

    @Override
    public T onCreateViewHolder(ViewGroup parent, int viewType) {
        int index = viewType / VIEW_TYPE_OFFSET;
        int position = viewType % VIEW_TYPE_OFFSET;
        RecyclerView.Adapter<T> adapter = adapters.get(index);
        return adapter.onCreateViewHolder(parent, position);
    }

    @Override
    public void onBindViewHolder(T holder, int position) {
        int offset = 0;
        for(RecyclerView.Adapter<T> adapter : adapters){
            if (position - offset < adapter.getItemCount()) {
                adapter.onBindViewHolder(holder, position - offset);
                return;
            } else {
                offset += adapter.getItemCount();
            }
        }
    }

    @Override
    public int getItemCount() {
        int count = 0;
        for(RecyclerView.Adapter<T> adapter : adapters){
            count += adapter.getItemCount();
        }
        return count;
    }

    @Override
    public long getItemId(int position) {
        int offset = 0;
        for(RecyclerView.Adapter<T> adapter : adapters){
            if (position - offset < adapter.getItemCount()) {
                return adapter.getItemId(position - offset);
            } else {
                offset += adapter.getItemCount();
            }
        }
        return NO_ID;
    }

    /**
     * Add adapter into MixAdapter
     */
    public void addAdapter(RecyclerView.Adapter<T> adapter) {
        adapters.add(adapter);
        notifyDataSetChanged();
    }

    /**
     * Get start position of given adapter in MixAdapter
     */
    public int getAdapterOffset(RecyclerView.Adapter<T> target){
        int offset = 0;
        for(RecyclerView.Adapter<T> adapter : adapters){
            if (adapter == target) {
                return offset;
            }
            offset += adapter.getItemCount();
        }
        return offset;
    }
}
