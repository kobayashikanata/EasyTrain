package com.per.epx.easytrain.helpers;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateFormatter {

    public SimpleDateFormat fromDefault(String format){
        return new SimpleDateFormat(format, Locale.getDefault());
    }

    public static String formatYMD(long dateMs){
        return formatYMD(new Date(dateMs));
    }

    public static String formatYMD(Date date){
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date);
    }

    public static String formatMD(long dateMs){
        return formatMD(new Date(dateMs));
    }

    public static String formatMD(Date date){
        return new SimpleDateFormat("MM/dd", Locale.getDefault()).format(date);
    }

    public static int differ(long dataMs, long dataMs2, int field){
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(dataMs);
        int value =  c.get(field);
        c.setTimeInMillis(dataMs2);
        return value - c.get(field);
    }


    @SuppressWarnings("SameParameterValue")
    public static String formatDefault(long timeMs, String format){
        return new SimpleDateFormat(format, Locale.getDefault()).format(timeMs);
    }

    @SuppressWarnings("SameParameterValue")
    public static String formatDefault(Date date, String format){
        return new SimpleDateFormat(format, Locale.getDefault()).format(date);
    }

    public static long getCurrentYearMonthDayInMs(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime().getTime();
    }
}
