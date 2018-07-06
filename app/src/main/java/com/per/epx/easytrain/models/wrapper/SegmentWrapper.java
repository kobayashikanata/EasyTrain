package com.per.epx.easytrain.models.wrapper;

import android.content.Context;

import com.per.epx.easytrain.R;
import com.per.epx.easytrain.helpers.DateFormatter;
import com.per.epx.easytrain.helpers.DurationFormatter;
import com.per.epx.easytrain.models.sln.Segment;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SegmentWrapper {
    private static NumberFormat formatNumber = new DecimalFormat("#####.##");
    private Segment raw;
    private long timeUsage;
    private long timeToLastMs;
    private int dayDiffer;
    private String beginMonthDayText;
    private String beginHourMinuteText;
    private String terminalHourMinuteText;
    private String timeUsageText;
    private String timeToLastText;
    private int order;

    private SegmentWrapper(Segment raw, long timeToLastMs, int order, String hourLabel, String minuteLabel) {
        this.raw = raw;
        this.order = order;
        this.timeToLastMs = timeToLastMs;
        long firstDriveTime = raw.getBeginning().getDriveTime();
        long lastArriveTime = raw.getTerminal().getArrivalTime();
        this.timeUsage = lastArriveTime - firstDriveTime;
        this.beginHourMinuteText = DateFormatter.formatDefault(firstDriveTime, "HH:mm");
        this.beginMonthDayText = DateFormatter.formatDefault(firstDriveTime, "MM-dd");
        this.terminalHourMinuteText = DateFormatter.formatDefault(lastArriveTime, "HH:mm");
        this.dayDiffer = DateFormatter.differ(lastArriveTime, firstDriveTime, Calendar.DAY_OF_YEAR);
        this.timeUsageText = DurationFormatter.totalMillsToHourMinute(timeUsage,hourLabel, minuteLabel);
        this.timeToLastText = DurationFormatter.totalMillsToHourMinute(timeToLastMs,hourLabel, minuteLabel);
    }

    public static List<SegmentWrapper> fromSegments(List<Segment> segments, Context context){
        String hourLabel = context.getString(R.string.label_hour);
        String minuteLabel = context.getString(R.string.label_minutes);
        List<SegmentWrapper> wrappers = new ArrayList<>();
        for (Segment current : segments){
            wrappers.add(new SegmentWrapper(current, -1L, -1,hourLabel,minuteLabel));
        }
        return wrappers;
    }

    public static List<SegmentWrapper> fromContinuableSegments(List<Segment> segments, Context context){
        String hourLabel = context.getString(R.string.label_hour);
        String minuteLabel = context.getString(R.string.label_minutes);
        List<SegmentWrapper> wrappers = new ArrayList<>();
        Segment last = null;
        for (int i = 0; i <segments.size(); i++){
            Segment current = segments.get(i);
            if(last == null){
                wrappers.add(new SegmentWrapper(current, -1L, i+1, hourLabel, minuteLabel));
            }else{
                long waitingTime = current.getBeginning().getDriveTime() - last.getTerminal().getArrivalTime();
                wrappers.add(new SegmentWrapper(current, waitingTime, i+1, hourLabel, minuteLabel));
            }
            last = current;
        }
        return wrappers;
    }

    public Segment raw(){
        return this.raw;
    }

    public int getOrder() {
        return order;
    }

    public String getTimeToLastText() {
        return timeToLastText;
    }

    public long getTimeToLastMs() {
        return timeToLastMs;
    }

    public String getLineNo(){
        return raw().getLineCode();
    }

    public String getPayment(){
        return formatNumber.format(raw().getPayment());
    }

    public String getCrossSize(){
        return String.valueOf(raw().getCrossSize());
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

    public String getBeginMonthDayText() {
        return beginMonthDayText;
    }

    public String getBeginHourMinuteText() {
        return beginHourMinuteText;
    }

    public String getTerminalHourMinuteText() {
        return terminalHourMinuteText;
    }
}
