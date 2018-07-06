package com.per.epx.easytrain.helpers.history;

import com.google.gson.reflect.TypeToken;
import com.per.epx.easytrain.models.FavoriteItem;

import java.util.List;

public class FavoriteHistoryHelper extends HistoryHelperBase<FavoriteItem>{

    private static FavoriteHistoryHelper instance = null;
    public static FavoriteHistoryHelper getInstance(){
        synchronized (FavoriteHistoryHelper.class){
            if(instance == null){
                instance = new FavoriteHistoryHelper();
            }
            return instance;
        }
    }

    @Override
    protected TypeToken getTypeTokenForTList() {
        return new TypeToken<List<FavoriteItem>>(){};
    }

    @Override
    protected String getFileName() {
        return "favorite";
    }

    @Override
    protected String getKeyName() {
        return "route_favorite";
    }

    @Override
    protected int getMaxStoreSize() {
        return 20;
    }

    @Override
    protected boolean isTheSameHistory(FavoriteItem t1, FavoriteItem t2) {
        return false;
    }
}