package com.per.epx.easytrain.models;

import java.util.ArrayList;
import java.util.List;

public class ListAcceptable<T> {
    public interface IAcceptable<T>{
        boolean accept(T value);
    }

    private final List<IAcceptable<T>> acceptors = new ArrayList<>();
    private final List<T> accepted = new ArrayList<>();
    private List<T> source;

    public ListAcceptable(List<T> source){
        this.source = source;
    }

    public List<T> getAccepted() {
        return accepted;
    }

    public void replaceSource(List<T> source){
        this.source = source;
        notifyAcceptorsChanged();
    }

    public boolean addAcceptor(IAcceptable<T> filterable){
        if(!acceptors.contains(filterable)){
            acceptors.add(filterable);
            notifyAcceptorAdded(filterable);
            return true;
        }
        return false;
    }

    public boolean removeAcceptor(IAcceptable<T> filterable){
        if(acceptors.remove(filterable)){
            notifyAcceptorsChanged();
            return true;
        }
        return false;
    }

    public void clear(){
        acceptors.clear();
        notifyAcceptorsChanged();
    }

    public void notifyAcceptorsChanged(){
        accepted.clear();
        if(acceptors.size() < 1){
            accepted.addAll(source);
        }else{
            for(int i = 0;i  < source.size(); i++){
                T value = source.get(i);
                if(accept(acceptors, value)){
                    accepted.add(value);
                }
            }
        }
    }

    private void notifyAcceptorAdded(IAcceptable<T> filterable){
        for(int i = accepted.size() - 1; i >= 0; i--){
            if(!filterable.accept(accepted.get(i))){
                accepted.remove(i);
            }
        }
    }

    private boolean accept(List<IAcceptable<T>> filters, T value){
        if(filters == null || filters.size() == 0)
            return true;
        for(IAcceptable<T> filter : filters){
            if(!filter.accept(value)){
                return false;
            }
        }
        return true;
    }
}
