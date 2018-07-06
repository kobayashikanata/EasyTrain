package com.per.epx.easytrain.helpers;

import android.content.Context;

public class ContextHolder {
    private static Context globalContext;

    public static void initial(Context context) {
        globalContext = context;
    }

    public static void unTouch(){
        globalContext = null;
    }

    public static Context getContext() {
        return globalContext;
    }
}
