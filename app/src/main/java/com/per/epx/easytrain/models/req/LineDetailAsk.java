package com.per.epx.easytrain.models.req;

import java.io.Serializable;

public class LineDetailAsk implements Serializable{
    private long lineId;
    private String lineCode;
    private long timeOfYearMonthDay;//The day we search

    public LineDetailAsk() {
    }

    public LineDetailAsk(String lineCode, long lineId, long timeOfYearMonthDay) {
        this.lineCode = lineCode;
        this.lineId = lineId;
        this.timeOfYearMonthDay = timeOfYearMonthDay;
    }

    public long getLineId() {
        return lineId;
    }

    public void setLineId(long lineId) {
        this.lineId = lineId;
    }

    public String getLineCode() {
        return lineCode;
    }

    public void setLineCode(String lineCode) {
        this.lineCode = lineCode;
    }

    public long getTimeOfYearMonthDay() {
        return timeOfYearMonthDay;
    }

    public void setTimeOfYearMonthDay(long timeOfYearMonthDay) {
        this.timeOfYearMonthDay = timeOfYearMonthDay;
    }
}
