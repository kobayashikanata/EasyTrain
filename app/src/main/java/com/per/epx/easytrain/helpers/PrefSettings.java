package com.per.epx.easytrain.helpers;

import android.content.Context;

import com.per.epx.easytrain.testing.ApiRedirect;

public class PrefSettings {
    public static final String PREFERENCES_FILE_NAME = "m-preference";
    public static final String PREFERENCES_KEY_SERVER_ADDRESS = "SERVER_ADDRESS";
    public static final String PREFERENCES_KEY_API_REDIRECT = "API_REDIRECT";
    public static final String PREFERENCES_KEY_IS_DEBUGGING = "IS_DEBUGGING";
    public static final String PREFERENCES_KEY_KEEP_DRAWER_OPEN = "KEEP_DRAWER_OPEN";
    public static final String PREFERENCES_KEY_CLEAR = "CLEAR-ON-EXIT";
    public static final String PREFERENCES_KEY_USE_WIDE_TEMPLATE = "USE_WIDE_TEMPLATE";

    public static String getServerAddress(Context context){
        return safelyGet(context, PREFERENCES_KEY_SERVER_ADDRESS, "", String.class);
    }

    public static Boolean isClearWhenExit(Context context) {
        return safelyGet(context, PREFERENCES_KEY_CLEAR, false, Boolean.class);
    }

    public static Boolean isApiRedirect(Context context) {
        return safelyGet(context, PREFERENCES_KEY_API_REDIRECT, true, Boolean.class);
    }

    public static Boolean isDebugging(Context context) {
        return safelyGet(context, PREFERENCES_KEY_IS_DEBUGGING, false, Boolean.class);
    }

    public static Boolean isDrawerKeepOpen(Context context) {
        return safelyGet(context, PREFERENCES_KEY_KEEP_DRAWER_OPEN, false, Boolean.class);
    }

    public static Boolean isWideTemplateUsed(Context context) {
        return safelyGet(context, PREFERENCES_KEY_USE_WIDE_TEMPLATE, false, Boolean.class);
    }


    public static void putServerAddress(Context context, String  serverAddress){
        SharedPreferencesUtils.put(context, PREFERENCES_FILE_NAME, PREFERENCES_KEY_SERVER_ADDRESS, serverAddress);
    }


    public static void putClearWhenExit(Context context, boolean isClearWhenExit){
        SharedPreferencesUtils.put(context, PREFERENCES_FILE_NAME, PREFERENCES_KEY_CLEAR, isClearWhenExit);
    }

    public static void putIsDebugging(Context context, boolean isDebugging) {
        SharedPreferencesUtils.put(context, PREFERENCES_FILE_NAME, PREFERENCES_KEY_IS_DEBUGGING, isDebugging);
    }

    public static void putKeepDrawerOpen(Context context, boolean deepDrawerOpen) {
        SharedPreferencesUtils.put(context, PREFERENCES_FILE_NAME, PREFERENCES_KEY_KEEP_DRAWER_OPEN, deepDrawerOpen);
    }

    public static void putWideTemplateUse(Context context, boolean isWideTemplateUsed) {
        SharedPreferencesUtils.put(context, PREFERENCES_FILE_NAME, PREFERENCES_KEY_USE_WIDE_TEMPLATE, isWideTemplateUsed);
    }


    public static void putApiRedirect(Context context, boolean isApiRedirect){
        SharedPreferencesUtils.put(context, PREFERENCES_FILE_NAME, PREFERENCES_KEY_API_REDIRECT, isApiRedirect);
        ApiRedirect.usingLocal(isApiRedirect);
    }

    public static <T> T safelyGet(Context context, String key, T defaultValue, Class<T> clazz){
        try{
            return CommonHelper.safeCast(SharedPreferencesUtils.
                    get(context, PREFERENCES_FILE_NAME, key, defaultValue), clazz);
        }catch (Exception e){
            e.printStackTrace();
        }
        return defaultValue;
    }
}
