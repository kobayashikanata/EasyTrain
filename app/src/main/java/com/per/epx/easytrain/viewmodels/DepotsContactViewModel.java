package com.per.epx.easytrain.viewmodels;

import android.content.Context;
import android.support.annotation.NonNull;

import com.per.epx.easytrain.helpers.history.AskHistoryHelper;
import com.per.epx.easytrain.helpers.search.ISearchable;
import com.per.epx.easytrain.helpers.search.PinyinUtils;
import com.per.epx.easytrain.helpers.search.SearchToken;
import com.per.epx.easytrain.models.Depot;
import com.per.epx.easytrain.models.DepotsModel;
import com.per.epx.easytrain.models.history.SolutionAskHistory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DepotsContactViewModel {
    private DepotsModel model;
    private final List<String> indexes = new ArrayList<>();
    private final Map<String, List<Depot>> indexesMap = new HashMap<>();
    private final List<DepotWrapper> searchableWrapper = new ArrayList<>();

    public DepotsContactViewModel(@NonNull DepotsModel model){
        this.model = model;
        List<Depot> depots = model.getAll();
        for(Depot depot : model.getHot()){
            depot.idToCodeIfNull();
        }
        for(Depot depot : depots){
            depot.idToCodeIfNull();
            searchableWrapper.add(new DepotWrapper(depot));
            //Get indexes
            String pinyin = depot.getPinyin();
            String english = depot.getEnglish();
            String indexText = (pinyin != null && pinyin.length() > 0) ?
                    pinyin : english;
            String index = String.valueOf(indexText.charAt(0)).toUpperCase();
            List<Depot> current = indexesMap.get(index);
            if(current == null){
                current = new ArrayList<>();
            }
            current.add(depot);
            indexesMap.put(index, current);
        }
        indexes.addAll(indexesMap.keySet());
        Collections.sort(indexes);
    }

    public List<Depot> getHotDepots(){
        return this.model.getHot();
    }

    public List<Depot> getMostDepots(Context context){
        List<Depot> most = new ArrayList<>();
        List<SolutionAskHistory> askList = AskHistoryHelper.getInstance().get(context);
        if(askList != null && askList.size() > 0){
            Map<String, Integer> nameMap = new HashMap<>();
            for(SolutionAskHistory history : askList){
                if(!nameMap.containsKey(history.getFrom().getName())){
                    most.add(history.getFrom());
                    nameMap.put(history.getFrom().getName(), 1);
                }
                if(!nameMap.containsKey(history.getTo().getName())){
                    most.add(history.getTo());
                    nameMap.put(history.getTo().getName(), 1);
                }
            }
        }
        return most;
    }

    public List<Depot> getAll(){
        return this.model.getAll();
    }

    public List<String> getIndexes(){
        return indexes;
    }

    public List<Depot> findDepotsOfKeyword(String keyword){
        List<Depot> after = new ArrayList<>();
        List<DepotWrapper> searchResult = PinyinUtils.searchKeyword(keyword, searchableWrapper);
        for (DepotWrapper depotWrapper : searchResult){
            after.add(depotWrapper.getDepot());
        }
        return after;
    }

    public List<Depot> findDepotsOfIndex(String indexLetter){
        return indexesMap.get(indexLetter);
    }


    private static class DepotWrapper implements ISearchable{
        private Depot depot;
        private SearchToken token;
        public DepotWrapper(Depot depot){
            this.depot = depot;
            this.token = PinyinUtils.convertToPolyphoneSupportSearchToken(this.getKeyword());
        }

        public Depot getDepot() {
            return depot;
        }

        @Override
        public String getKeyword() {
            return depot.getName();
        }

        @Override
        public SearchToken getSearchToken() {
            return token;
        }
    }
}
