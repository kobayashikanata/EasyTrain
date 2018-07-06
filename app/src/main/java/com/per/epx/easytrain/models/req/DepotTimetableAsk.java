package com.per.epx.easytrain.models.req;

import java.io.Serializable;

public class DepotTimetableAsk implements Serializable {
    private long depotId;
    private String depotCode;//The depot code
    private long timeOfYearMonthDay;//The day we search

    public DepotTimetableAsk() {
    }

    public DepotTimetableAsk(long depotId, String depotCode, long timeOfYearMonthDay) {
        this.depotId = depotId;
        this.depotCode = depotCode;
        this.timeOfYearMonthDay = timeOfYearMonthDay;
    }

    public long getDepotId() {
        return depotId;
    }

    public void setDepotId(long depotId) {
        this.depotId = depotId;
    }

    public void setDepotCode(String depotCode) {
        this.depotCode = depotCode;
    }

    public String getDepotCode() {
        return depotCode;
    }

    public void setTimeOfYearMonthDay(long timeOfYearMonthDay) {
        this.timeOfYearMonthDay = timeOfYearMonthDay;
    }

    public long getTimeOfYearMonthDay() {
        return timeOfYearMonthDay;
    }
}
