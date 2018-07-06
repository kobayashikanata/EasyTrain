package com.per.epx.easytrain.models.req;

import com.per.epx.easytrain.models.DepotLite;
import com.per.epx.easytrain.models.Pass;

import java.util.List;

public class LineDetailReply extends LineBaseReply{
    private DepotLite beginning;
    private DepotLite terminal;
    private long timeOfYearMonthDay;//The day we search
    private List<Pass> passes;

    public LineDetailReply() {
    }

    public LineDetailReply(String lineCode, long lineId, String name, long timeOfYearMonthDay, List<Pass> passes) {
        super(lineCode, lineId, name);
        this.timeOfYearMonthDay = timeOfYearMonthDay;
        this.passes = passes;
    }

    public LineDetailReply(String lineCode, long lineId, String name, DepotLite beginning, DepotLite terminal, long timeOfYearMonthDay, List<Pass> passes) {
        super(lineCode, lineId, name);
        this.beginning = beginning;
        this.terminal = terminal;
        this.timeOfYearMonthDay = timeOfYearMonthDay;
        this.passes = passes;
    }

    public DepotLite getBeginning() {
        return beginning;
    }

    public void setBeginning(DepotLite beginning) {
        this.beginning = beginning;
    }

    public DepotLite getTerminal() {
        return terminal;
    }

    public void setTerminal(DepotLite terminal) {
        this.terminal = terminal;
    }

    public long getTimeOfYearMonthDay() {
        return timeOfYearMonthDay;
    }

    public void setTimeOfYearMonthDay(long timeOfYearMonthDay) {
        this.timeOfYearMonthDay = timeOfYearMonthDay;
    }

    public List<Pass> getPasses() {
        return passes;
    }

    public void setPasses(List<Pass> passes) {
        this.passes = passes;
    }
}
