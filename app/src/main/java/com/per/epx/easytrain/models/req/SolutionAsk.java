package com.per.epx.easytrain.models.req;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

//Create by dai-jian 20180218
//Class for ask best route from service-desk
public class SolutionAsk extends SearchItemBase{
    private @AskType int type;//The type to filter best ways to go for self.
    private Long tripDate;//The early time to begin this trip
    private List<String> transitCodes;//Station code that wants to pass
    private String version;//Protocol version

    //Type of filter best choice for user.
    public static final int LEAST_TIME = 1;//Waste no more time
    public static final int CHEAPEST = 2;//Use less money
    public static final int LEAST_TRANSFER  = 3;//Wait without more transfer
    public static final int SHORTEST = 4;//Get shortest distance first
    @IntDef({SHORTEST, CHEAPEST, LEAST_TIME, LEAST_TRANSFER})
    @Retention(RetentionPolicy.SOURCE)
    public @interface AskType {}

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Long getTripDate() {
        return tripDate;
    }

    public void setTripDate(Long tripDate) {
        this.tripDate = tripDate;
    }

    public List<String> getTransitCodes() {
        return transitCodes;
    }

    public void setTransitCodes(List<String> transitCodes) {
        this.transitCodes = transitCodes;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
