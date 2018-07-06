package com.per.epx.easytrain.helpers;

import android.content.Context;

import java.net.ConnectException;

public class ErrorMessageHelper {
    public static String messageOfException(Context context, Exception e){
        if(e instanceof ConnectException){
            return "Network error";
        }
        return "";
    }
}
