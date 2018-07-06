package com.per.epx.easytrain.helpers;

import android.content.Context;

import com.per.epx.easytrain.R;

public class ExceptionMessageHelper {
    public static <T extends Exception> String getMessage(Context context, T e){
        if(e instanceof java.net.ConnectException){
            return context.getString(R.string.tips_ex_connect);
        }
        return e.getMessage();
    }
}
