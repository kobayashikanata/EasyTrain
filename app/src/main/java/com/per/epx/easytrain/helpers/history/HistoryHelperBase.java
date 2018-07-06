package com.per.epx.easytrain.helpers.history;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.per.epx.easytrain.helpers.SharedPreferencesUtils;
import com.per.epx.easytrain.models.history.HistoryItem;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public abstract class HistoryHelperBase<T extends HistoryItem> {

    public List<T> updateWeight(List<T> histories){
        Collections.sort(histories, new Comparator<T>() {
            @Override
            public int compare(T o1, T o2) {
                return o2.getWeight() - o1.getWeight();
            }
        });
        int overSize = histories.size() - getMaxStoreSize();
        if(overSize > 0){
            for(int i = 0; i < overSize; i++){
                histories.remove(0);
            }
        }
        return histories;
    }

    public List<T> get(Context context){
        Object text = SharedPreferencesUtils.get(context, getFileName(), getKeyName(), "");
        if(text != null && text instanceof String){
            String jsonText = ((String) text);
            if(jsonText.length() > 0){
                try{
                    Type type = getTypeTokenForTList().getType();
                    List<T> histories = new Gson().fromJson(jsonText, type);
                    return histories == null ? new ArrayList<T>() : histories;
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        return new ArrayList<>();
    }

    public boolean remove(Context context, List<T> histories, T item) {
        boolean removed = false;
        if(histories != null){
            removed = histories.remove(item);
        }
        if(removed){
            save(context, histories);
        }
        return removed;
    }

    public void save(Context context, List<T> histories){
        updateWeight(histories);
        SharedPreferencesUtils.put(context, getFileName(), getKeyName(), new Gson().toJson(histories));
    }

    public void append(Context context, T newItem){
        append(context, get(context), newItem);
    }

    public void append(Context context, List<T> histories, T newItem){
        if(histories == null){
            histories = new ArrayList<>();
        }
        T sameOne = null;
        for(int i = 0; i < histories.size(); i++){
            T history = histories.get(i);
            if(isTheSameHistory(history, newItem)){
                sameOne = history;
                break;
            }
        }
        if(sameOne != null){
            sameOne.setWeight(sameOne.getWeight()+1);
        }else{
            histories.add(newItem);
        }
        updateWeight(histories);
        save(context, histories);
    }

    public void clear(Context context){
        SharedPreferencesUtils.put(context, getFileName(), getKeyName(), "");
    }

    protected abstract TypeToken getTypeTokenForTList();
    protected abstract String getFileName();
    protected abstract String getKeyName();
    protected abstract int getMaxStoreSize();
    protected abstract boolean isTheSameHistory(T t1, T t2);
}
