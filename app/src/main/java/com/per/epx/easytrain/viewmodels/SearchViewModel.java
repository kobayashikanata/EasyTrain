package com.per.epx.easytrain.viewmodels;

import android.databinding.ObservableField;
import android.support.annotation.NonNull;

import com.per.epx.easytrain.helpers.RouteAskValidator;
import com.per.epx.easytrain.interfaces.IAction1;
import com.per.epx.easytrain.interfaces.IViewModel;
import com.per.epx.easytrain.interfaces.Interceptor;
import com.per.epx.easytrain.models.Interval;
import com.per.epx.easytrain.models.SearchModel;
import com.per.epx.easytrain.models.getset.GetSetter0;
import com.per.epx.easytrain.models.getset.IGetter0;
import com.per.epx.easytrain.models.req.SolutionAsk;
import com.per.epx.easytrain.models.req.SolutionReply;
import com.per.epx.easytrain.testing.ApiRedirect;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

public class SearchViewModel implements IViewModel, Interceptor, IAction1<SolutionReply> {
    private final GetSetter0<Boolean> mIsSearching = new GetSetter0<>(false);

    public final SearchModel model = new SearchModel();
    public final IGetter0<Boolean> isSearching = mIsSearching;
    public final ObservableField<SolutionReply> replied = new ObservableField<>();

    @SuppressWarnings("FieldCanBeLocal")
    private final String version = "v1.0";//Protocol version

    public SearchViewModel(){
    }

    public void copyBasicModel(SearchModel sm){
        this.model.from.set(sm.from.get());
        this.model.to.set(sm.to.get());
        this.model.type.set(sm.type.get());
        this.model.dateMs.set(sm.dateMs.get());
        this.model.transitCodes.addAll(sm.transitCodes);
    }

    public @RouteAskValidator.Type int check(SolutionAsk ask){
        return RouteAskValidator.DEFAULT.isAskValid(ask);
    }

    public @RouteAskValidator.Type int checkCurrent(){
        return RouteAskValidator.DEFAULT.isAskValid(wrapCurrentAsk());
    }

    public SolutionAsk wrapCurrentAsk(){
        SolutionAsk solutionAsk = new SolutionAsk();
        solutionAsk.setFrom(model.from.get());
        solutionAsk.setTo(model.to.get());
        solutionAsk.setTripDate(model.dateMs.get());
//        Interval<Integer> timeRange = model.interval.get();
//        if(timeRange == null){
//            timeRange = new Interval<>(0, 1440);
//        }
//        solutionAsk.setMinOutTime(date + timeRange.from.get() * 60 * 1000);
//        solutionAsk.setMaxOutTime(date + timeRange.to.get() * 60 * 1000);
        solutionAsk.setType(model.type.get());
        solutionAsk.setTransitCodes(model.transitCodes);
        solutionAsk.setVersion(version);
        return solutionAsk;
    }

    public @RouteAskValidator.Type int find(int requestKey){
        if(mIsSearching.get()){
            return RouteAskValidator.IS_FINDING;
        }else {
            mIsSearching.set(true);
            SolutionAsk ask = wrapCurrentAsk();
            int valid = check(ask);
            if(valid == RouteAskValidator.PASS){
                ApiRedirect.getApi().register(requestKey, this);
                ApiRedirect.getApi().findRoute(requestKey, ask,this);
            }else{
                mIsSearching.set(false);
            }
            return valid;
        }
    }

    //On search result back
    @Override
    public void invoke(SolutionReply replyData) {
        mIsSearching.set(false);
        replied.set(replyData);
    }

    @Override
    public void cleanUp() {
        mIsSearching.set(false);
        ApiRedirect.getApi().unregister(this);
    }

    @Override
    public void onExceptionOccupied(@NonNull Call call, @NonNull Response response, @NonNull Exception e) {
        mIsSearching.set(false);
        ApiRedirect.getApi().unregister(this);
    }

    @Override
    public void onFailure(@NonNull Call call, @NonNull IOException e) {
        mIsSearching.set(false);
        ApiRedirect.getApi().unregister(this);
    }
}
