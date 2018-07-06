package com.per.epx.easytrain.models.wrapper;

import android.content.Context;

import com.per.epx.easytrain.R;
import com.per.epx.easytrain.helpers.DateFormatter;
import com.per.epx.easytrain.helpers.DurationFormatter;
import com.per.epx.easytrain.models.sln.LineRoute;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;

public class RouteWrapper implements Serializable{
    private static NumberFormat formatNumber = new DecimalFormat("#####.##");
    private LineRoute raw;
    private long timeUsage;
    private int dayDiffer;
    private String tripMonthDayHourMinute;
    private String beginHourMinuteText;
    private String terminalHourMinuteText;
    private String timeUsageText;
    private String transferText;

    public RouteWrapper(LineRoute raw, Context context) {
        setup(raw, context);
        this.timeUsageText = DurationFormatter.totalMillsToHourMinute(timeUsage,
                context.getString(R.string.label_hour),
                context.getString(R.string.label_minutes));
    }
    private void setup(LineRoute raw, Context context){
        this.raw = raw;
        long firstDriveTime = raw.getBeginning().getDriveTime();
        long lastArriveTime = raw.getTerminal().getArrivalTime();
        this.timeUsage = lastArriveTime - firstDriveTime;
        this.tripMonthDayHourMinute = DateFormatter.formatDefault(firstDriveTime, "MM/dd\nHH:mm");
        this.beginHourMinuteText = DateFormatter.formatDefault(firstDriveTime, "HH:mm");
        this.terminalHourMinuteText = DateFormatter.formatDefault(lastArriveTime, "HH:mm");
        this.dayDiffer = DateFormatter.differ(lastArriveTime, firstDriveTime, Calendar.DAY_OF_YEAR);
        if(raw.getRunLines().size() <= 1){
            this.transferText = context.getString(R.string.label_nonstop);
        }else{
            this.transferText = String.format(context.getString(R.string.format_transfer_times), raw.getRunLines().size() - 1);
        }
    }

    public LineRoute raw(){
        return this.raw;
    }

    public String getTransferText(){
        return transferText;
    }

    public String getPayment(){
        return formatNumber.format(raw().getPayment());
    }

    public String getCrossSize(){
        return String.valueOf(raw().getRunLines().size());
    }

    public String getBeginName(){
        return String.valueOf(raw().getBeginning().getName());
    }

    public String getTerminalName(){
        return String.valueOf(raw().getTerminal().getName());
    }

    public int getDayDiffer() {
        return dayDiffer;
    }

    public String getTimeUsageText() {
        return timeUsageText;
    }

    public String getTripMonthDayHourMinute() {
        return tripMonthDayHourMinute;
    }

    public String getBeginHourMinuteText() {
        return beginHourMinuteText;
    }

    public String getTerminalHourMinuteText() {
        return terminalHourMinuteText;
    }
}
