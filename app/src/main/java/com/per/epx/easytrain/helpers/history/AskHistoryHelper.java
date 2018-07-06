package com.per.epx.easytrain.helpers.history;

import com.google.gson.reflect.TypeToken;
import com.per.epx.easytrain.models.history.SolutionAskHistory;

import java.util.List;

public class AskHistoryHelper extends HistoryHelperBase<SolutionAskHistory> {

    private static AskHistoryHelper instance = null;
    public static AskHistoryHelper getInstance(){
        synchronized (AskHistoryHelper.class){
            if(instance == null){
                instance = new AskHistoryHelper();
            }
            return instance;
        }
    }

    @Override
    protected TypeToken getTypeTokenForTList() {
        return new TypeToken<List<SolutionAskHistory>>(){};
    }

    @Override
    protected String getFileName() {
        return "history";
    }

    @Override
    protected String getKeyName() {
        return "ask_history";
    }

    @Override
    protected int getMaxStoreSize() {
        return 10;
    }

    @Override
    protected boolean isTheSameHistory(SolutionAskHistory t1, SolutionAskHistory t2) {
        if(t1.getFrom() == null){
            return t2.getFrom() == null;
        }else{
            return t1.getFrom().getCode().equals(t2.getFrom().getCode())
                    && t1.getTo().getCode().equals(t2.getTo().getCode());
        }
    }
}
