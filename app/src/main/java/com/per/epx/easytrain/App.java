package com.per.epx.easytrain;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.LocaleList;
import android.util.Log;

import com.per.epx.easytrain.helpers.CommonHelper;
import com.per.epx.easytrain.helpers.ContextHolder;
import com.per.epx.easytrain.helpers.CrashAssistant;
import com.per.epx.easytrain.helpers.DateFormatter;
import com.per.epx.easytrain.helpers.LanguageUtil;
import com.per.epx.easytrain.helpers.PrefSettings;
import com.per.epx.easytrain.helpers.SharedPreferencesUtils;
import com.per.epx.easytrain.testing.ApiRedirect;
import com.per.epx.easytrain.views.activities.ErrorActivity;


import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;
import java.util.Date;

public class App extends Application {
    private static String mLanguage = LanguageUtil.LanguageConstants.ENGLISH;
    private static String mServerUrl;
    private static boolean debugging;
    private static boolean drawerKeepOpen;
    private static boolean wideTemplate;
    private static WeakReference<Context> weakCtx;

    @Override
    public void onCreate() {
        super.onCreate();
        weakCtx = new WeakReference<Context>(this);
        CrashAssistant.CrashConfig config = new CrashAssistant.CrashConfig();
        config.setErrorActivityClass(ErrorActivity.class);
        CrashAssistant.install(getBaseContext(), config);
        ContextHolder.initial(this);
        try{
            mLanguage = CommonHelper.safeCast(SharedPreferencesUtils.get(this,
                    "LangConfig", "Language", ""),
                    String.class);
        } catch (Exception e){
            e.printStackTrace();
        }
        updateServerUrl(this);
        setDebugging(PrefSettings.isDebugging(this));
        setDrawerKeepOpen(PrefSettings.isDrawerKeepOpen(this));
        setWideTemplate(PrefSettings.isWideTemplateUsed(this));
        ApiRedirect.usingLocal(PrefSettings.isApiRedirect(this));
        LanguageUtil.applyLanguage(this, mLanguage);
        registerActivityLifecycleCallbacks(callbacks);
        ApiRedirect.getDepotsContact(100002, null);
        this.registerReceiver(localeChangedReceiver, new IntentFilter(Intent.ACTION_LOCALE_CHANGED));
    }

    private BroadcastReceiver localeChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_LOCALE_CHANGED.equals(intent.getAction())) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    LanguageUtil.SupportLanguageUtil.setSystemLocaleList(LocaleList.getDefault());
                }
            }
        }
    };

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LanguageUtil.attachBaseContext(base, App.getLanguage()));
        //super.attachBaseContext(base);
    }

    public static Context getContext(){
        if(weakCtx == null){
            return null;
        }
        return weakCtx.get();
    }

    public static void setLanguage(Context context, String language){
        App.mLanguage = language;
        try{
            SharedPreferencesUtils.put(context,
                    "LangConfig", "Language", language);
        }catch (Exception e){
            e.printStackTrace();
        }
        EventBus.getDefault().post(new LanguageChangeEvent());
    }

    public static class LanguageChangeEvent{

    }

    public static String getLanguage(){
        return App.mLanguage;
    }

    public static boolean isWideTemplate(){
        return wideTemplate;
    }

    public static void setWideTemplate(boolean wideTemplate) {
        App.wideTemplate = wideTemplate;
    }

    public static void updateServerUrl(Context context){
        App.mServerUrl = PrefSettings.getServerAddress(context);
        if(App.mServerUrl == null || App.mServerUrl.length() == 0){
            String defaultUrl = "http://192.168.43.242:63319";
            PrefSettings.putServerAddress(context, defaultUrl);
            PrefSettings.putApiRedirect(context, false);
            App.mServerUrl = defaultUrl;
        }
        ApiRedirect.resetDepotsContact();
    }

    public static void setDebugging(boolean debugging) {
        App.debugging = debugging;
    }

    public static void setDrawerKeepOpen(boolean drawerKeepOpen) {
        App.drawerKeepOpen = drawerKeepOpen;
    }

    public static boolean isDrawerKeepOpen() {
        return drawerKeepOpen;
    }

    public static boolean isDebugging() {
        return debugging;
    }

    public static String getServerUrl() {
        return mServerUrl;
    }

    public static String getVersionName(Activity activity){
        PackageInfo pkg = null;
        try {
            pkg = activity.getPackageManager().getPackageInfo(activity.getApplication().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String appName = pkg.applicationInfo.loadLabel(activity.getPackageManager()).toString();
        String versionName = pkg.versionName;
        return versionName;
    }

    ActivityLifecycleCallbacks callbacks = new ActivityLifecycleCallbacks() {
        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        }

        @Override
        public void onActivityStarted(Activity activity) { }
        @Override
        public void onActivityResumed(Activity activity) { }
        @Override
        public void onActivityPaused(Activity activity) { }
        @Override
        public void onActivityStopped(Activity activity) { }
        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) { }
        @Override
        public void onActivityDestroyed(Activity activity) { }
    };
}
