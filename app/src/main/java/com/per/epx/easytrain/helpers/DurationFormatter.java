package com.per.epx.easytrain.helpers;

public class DurationFormatter {

    public static String totalMillsToHourMinute(long timeMs){
        if(timeMs <= 0){
            return "00:00";
        }
        long hours = timeMs / 3600000;
        long minutes = timeMs % 3600000 / 60000;
        return addZeroToTwo(hours) + ":"+ addZeroToTwo(minutes);
    }

    public static String totalMillsToHourMinute(long timeMs, String hourLabel, String minuteLabel){
        return totalMillsToHourMinute(timeMs, hourLabel, minuteLabel, ",");
    }

    public static String totalMillsToHourMinute(long timeMs, String hourLabel, String minuteLabel, String separator){
        if(timeMs <= 0){
            return "0" + hourLabel+ separator+"0"+minuteLabel;
        }
        long hours = timeMs / 3600000;
        long minutes = timeMs % 3600000 / 60000;
        if(hours<=0){
            return minutes + minuteLabel;
        }
        if(minutes <= 0){
            return hours + hourLabel;
        }
        return hours + hourLabel+ separator+minutes+minuteLabel;
    }

    private static String addZeroToTwo(long number){
        if(number < 10 && number >= 0){
            return "0"+number;
        }
        return String.valueOf(number);
    }
}
