package com.per.epx.easytrain.models.req;

import com.per.epx.easytrain.models.sln.Segment;

import java.io.Serializable;
import java.util.List;

public class DepotTimetableReply implements Serializable {
    private long depotId;
    private String depotCode;//The depot code
    private String depotName;//The depot name
    private Long timeOfYearMonthDay;//The day we search
    private List<Segment> timetable;


    public DepotTimetableReply() {
    }

    public DepotTimetableReply(long depotId, String depotCode, String depotName, Long timeOfYearMonthDay, List<Segment> timetable) {
        this.depotId = depotId;
        this.depotCode = depotCode;
        this.timeOfYearMonthDay = timeOfYearMonthDay;
        this.timetable = timetable;
        this.depotName = depotName;
    }

    public long getDepotId() {
        return depotId;
    }

    public String getDepotCode() {
        return depotCode;
    }

    public void setDepotCode(String depotCode) {
        this.depotCode = depotCode;
    }

    public String getDepotName() {
        return depotName;
    }

    public void setDepotName(String depotName) {
        this.depotName = depotName;
    }

    public Long getTimeOfYearMonthDay() {
        return timeOfYearMonthDay;
    }

    public void setTimeOfYearMonthDay(Long timeOfYearMonthDay) {
        this.timeOfYearMonthDay = timeOfYearMonthDay;
    }

    public List<Segment> getTimetable() {
        return timetable;
    }

    public void setTimetable(List<Segment> timetable) {
        this.timetable = timetable;
    }
}
