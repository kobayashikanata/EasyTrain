package com.per.epx.easytrain.adapters.base;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

//None-Data-Hole version of adapter
//Class save the factory to create view holder
//But not hole data or view.
@SuppressWarnings("WeakerAccess,unused")
public abstract class RcvAdapterPrimitive extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    protected final IViewHolderFactoryPrimitive vmFactory;

    public RcvAdapterPrimitive(@NonNull IViewHolderFactoryPrimitive vmFactory) {
        this.vmFactory = vmFactory;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return vmFactory.createViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        onBinding(holder, getItem(position), position);
    }

    protected abstract void onBinding(RecyclerView.ViewHolder holder, Object data, int position);

    protected abstract Object getItem(int position);

    public interface IViewHolderFactoryPrimitive {
        RecyclerView.ViewHolder createViewHolder(ViewGroup parent, int viewType);
    }
}
