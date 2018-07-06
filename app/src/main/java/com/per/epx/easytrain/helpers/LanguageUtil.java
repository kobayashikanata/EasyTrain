package com.per.epx.easytrain.helpers;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class LanguageUtil {

    public static void applyLanguage(Context context, String newLanguage) {
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        Locale locale = SupportLanguageUtil.getSupportLanguage(newLanguage);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // apply locale
            configuration.setLocale(locale);

        } else {
            // updateConfiguration
            configuration.locale = locale;
            DisplayMetrics dm = resources.getDisplayMetrics();
            resources.updateConfiguration(configuration, dm);
        }
    }

    public static Context attachBaseContext(Context context, String language) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return createConfigurationResources(context, language);
        } else {
            applyLanguage(context, language);
            return context;
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    private static Context createConfigurationResources(Context context, String language) {
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        Locale locale;
        if (TextUtils.isEmpty(language)) {//If the language is not special, use system language
            locale = SupportLanguageUtil.getSystemPreferredLanguage();
        } else {//Use the special language
            locale = SupportLanguageUtil.getSupportLanguage(language);
        }
        configuration.setLocale(locale);
        return context.createConfigurationContext(configuration);
    }

    public class LanguageConstants {
        public static final String SIMPLIFIED_CHINESE = "zh";
        public static final String ENGLISH = "en";
        public static final String AUTO = "";
        //public static final String TRADITIONAL_CHINESE = "zh-hant";
    }

    public static class SupportLanguageUtil {
        private static Map<String, Locale> mSupportLanguages = new HashMap<String, Locale>(7) {{
            put(LanguageConstants.ENGLISH, Locale.ENGLISH);
            put(LanguageConstants.SIMPLIFIED_CHINESE, Locale.SIMPLIFIED_CHINESE);
            //put(LanguageConstants.TRADITIONAL_CHINESE, Locale.TRADITIONAL_CHINESE);
        }};
        private static LocaleList mLocaleList;
        static {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mLocaleList = LocaleList.getDefault();
            }
        }

        public static void setSystemLocaleList(LocaleList localeList) {
            mLocaleList = localeList;
        }


        /**
         * Check if the language is supported.
         *
         * @param language language
         * @return true:Supported false:Not supported.
         */
        public static boolean isSupportLanguage(String language) {
            return (language != null && language.length() > 0) && mSupportLanguages.containsKey(language);
        }

        /**
         * Get the supported language
         *
         * @param language language
         * @return return the supported language, otherwise return system preferred language
         */
        @TargetApi(Build.VERSION_CODES.N)
        public static Locale getSupportLanguage(String language) {
            if (isSupportLanguage(language)) {
                return mSupportLanguages.get(language);
            }
            return getSystemPreferredLanguage();
        }

        /**
         * Get system preferred language
         *
         * @return Locale
         */
        @RequiresApi(api = Build.VERSION_CODES.N)
        public static Locale getSystemPreferredLanguage() {
            Locale locale;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                locale = mLocaleList.get(0);
            } else {
                locale = Locale.getDefault();
            }
            return locale;
        }
    }
}