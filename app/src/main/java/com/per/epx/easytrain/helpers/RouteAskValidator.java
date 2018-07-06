package com.per.epx.easytrain.helpers;

import android.content.Context;
import android.support.annotation.IntDef;

import com.per.epx.easytrain.R;
import com.per.epx.easytrain.models.req.SolutionAsk;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@SuppressWarnings("WeakerAccess")
public class RouteAskValidator {
    public static final int PASS = 0;
    public static final int INVALID_FROM = 1;
    public static final int INVALID_TO = 2;
    public static final int INVALID_TIME = 3;
    public static final int INVALID_TRANSIT = 4;
    public static final int IS_FINDING = 5;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({PASS, INVALID_FROM, INVALID_TO, INVALID_TIME, INVALID_TRANSIT, IS_FINDING})
    public @interface Type{}

    public static final RouteAskValidator DEFAULT = new RouteAskValidator();

    public boolean isLocationValid(String location){
        return location != null && location.length() > 1;
    }

    public static String getMessage(Context context, @Type int validType){
        switch (validType) {
            case PASS:
                break;
            case INVALID_FROM:
                return context.getString(R.string.invalid_from_msg);
            case INVALID_TIME:
                return context.getString(R.string.invalid_time_msg);
            case INVALID_TO:
                return context.getString(R.string.invalid_to_msg);
            case INVALID_TRANSIT:
                return context.getString(R.string.invalid_transit_msg);
            case IS_FINDING:
                return context.getString(R.string.still_finding);
        }
        return "";
    }

    public @Type int isAskValid(SolutionAsk askPkg){
        if(askPkg.getFrom() == null || !isLocationValid(askPkg.getFrom().getCode())){
            return INVALID_FROM;
        }else if(askPkg.getTo() == null || !isLocationValid(askPkg.getTo().getCode())){
            return INVALID_TO;
        }else {
            for(String transit : askPkg.getTransitCodes()){
                if(!isLocationValid(transit)){
                    return INVALID_TRANSIT;
                }
            }
        }
        return PASS;
    }
}
