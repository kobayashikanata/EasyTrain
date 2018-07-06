package com.per.epx.easytrain.helpers;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Parcelable;
import android.util.TypedValue;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

public class CommonHelper {
    public static int dpToPx(Resources res, int dp){
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, res.getDisplayMetrics()));
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isSameDay(long timeMs1, long timeMs2) {
        if(timeMs1 >= 0 && timeMs2 >= 0) {
            Calendar cal1 = Calendar.getInstance();
            cal1.setTimeInMillis(timeMs1);
            Calendar cal2 = Calendar.getInstance();
            cal2.setTimeInMillis(timeMs2);
            return isSameDay(cal1, cal2);
        } else {
            throw new IllegalArgumentException("The time must large than zero");
        }
    }

    public static boolean isTodayOrAfter(long timeMs1, long timeCompareTo){
        if(timeMs1 >= 0 && timeCompareTo >= 0) {
            Calendar c1 = Calendar.getInstance();
            c1.setTimeInMillis(timeMs1);
            Calendar compareTo = Calendar.getInstance();
            compareTo.setTimeInMillis(timeCompareTo);
            return c1.get(Calendar.ERA) == compareTo.get(Calendar.ERA)
                    && c1.get(Calendar.YEAR) == compareTo.get(Calendar.YEAR)
                    && c1.get(Calendar.DAY_OF_YEAR) >= compareTo.get(Calendar.DAY_OF_YEAR);
        } else {
            throw new IllegalArgumentException("The time must large than zero");
        }
    }

    public static boolean isSameDay(Date date1, Date date2) {
        if(date1 != null && date2 != null) {
            Calendar cal1 = Calendar.getInstance();
            cal1.setTime(date1);
            Calendar cal2 = Calendar.getInstance();
            cal2.setTime(date2);
            return isSameDay(cal1, cal2);
        } else {
            throw new IllegalArgumentException("The date must not be null");
        }
    }

    public static boolean isSameDay(Calendar cal1, Calendar cal2) {
        if(cal1 != null && cal2 != null) {
            return cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA)
                    && cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                    && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
        } else {
            throw new IllegalArgumentException("The date must not be null");
        }
    }


    public static <T extends Serializable> T safeCastSerializable(Intent intent, String key, Class<T> clazz) {
        if(intent == null) return null;
        return CommonHelper.safeCast(intent.getSerializableExtra(key), clazz);
    }

    public static <T extends Parcelable> T safeCastParcelable(Intent intent, String key, Class<T> clazz) {
        if(intent == null) return null;
        return CommonHelper.safeCast(intent.getParcelableExtra(key), clazz);
    }

    public static <T> T safeCast(Object o, Class<T> clazz) {
        return clazz != null && clazz.isInstance(o) ? clazz.cast(o) : null;
    }
}
