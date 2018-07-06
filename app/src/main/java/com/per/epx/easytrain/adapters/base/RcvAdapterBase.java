package com.per.epx.easytrain.adapters.base;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

//Data-Holed adapter base
//Class save the factory to create view holder and hole data.
@SuppressWarnings("WeakerAccess,unused")
public abstract class RcvAdapterBase<DataType> extends RecyclerView.Adapter<RcvAdapterBase.VHBase> {
    protected IViewHolderFactory  vmFactory;

    public RcvAdapterBase(@NonNull IViewHolderFactory vmFactory) {
        this.vmFactory = vmFactory;
    }

    @Override
    public final VHBase onCreateViewHolder(ViewGroup parent, int viewType) {
        return vmFactory.createViewHolder(parent, viewType);
    }

    @Override
    @SuppressWarnings("unchecked")
    public final void onBindViewHolder(VHBase holder, int position) {
        holder.bindData(getItem(position), new Payload(position, getItemCount()));
    }

    protected abstract DataType getItem(int position);

    public interface IViewHolderFactory {
         VHBase createViewHolder(ViewGroup parent, int viewType);
    }

    public static class Payload{
        public final int position;
        public final int itemCount;
        public Payload(int position, int itemCount){
            this.position = position;
            this.itemCount = itemCount;
        }
    }

    public static abstract class VHBase<D> extends RecyclerView.ViewHolder{
        public final IHolderApi holderApi;

        public VHBase(View itemView) {
            super(itemView);
            holderApi = new ViewHolderHelper(itemView) {
                @Override
                protected int getLayoutPosition() {
                    return VHBase.this.getLayoutPosition();
                }

                @Override
                protected int getAdapterPosition() {
                    return VHBase.this.getAdapterPosition();
                }
            };
        }

        protected final void bindData(D data, Payload payload){
            onBindData(holderApi, data,payload);
        }

        protected abstract void onBindData(IHolderApi holderApi, D data, Payload payload);

    }

    public static abstract class VHLayoutBase<D> extends VHBase<D>{
        public VHLayoutBase(@NonNull Context context, @LayoutRes int layoutId) {
            super(LayoutInflater.from(context).inflate(layoutId, null, false));
        }
    }

    public static abstract class VHViewParentBase<D> extends VHBase<D>{
        public VHViewParentBase(@NonNull ViewGroup parent, @LayoutRes int layoutId) {
            super(LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false));
        }
    }
}
