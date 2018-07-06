package com.per.epx.easytrain.helpers.history;

import com.google.gson.reflect.TypeToken;
import com.per.epx.easytrain.models.history.TextHistory;

import java.util.List;

@SuppressWarnings("WeakerAccess")
public class TextHistoryHelper extends HistoryHelperBase<TextHistory>{

    private static TextHistoryHelper instance = null;
    public static TextHistoryHelper getInstance(){
        synchronized (TextHistoryHelper.class){
            if(instance == null){
                instance = new TextHistoryHelper();
            }
            return instance;
        }
    }

    @Override
    protected TypeToken getTypeTokenForTList() {
        return new TypeToken<List<TextHistory>>(){};
    }

    @Override
    protected String getFileName() {
        return "history";
    }

    @Override
    protected String getKeyName() {
        return "text_history";
    }

    @Override
    protected int getMaxStoreSize() {
        return 10;
    }

    @Override
    protected boolean isTheSameHistory(TextHistory t1, TextHistory t2) {
        if(t1.getText() == null){
            return t2.getText() == null;
        }else{
            return t1.getText().equals(t2.getText());
        }
    }
}
