package com.per.epx.easytrain.models.req;

import com.per.epx.easytrain.models.DepotLite;
import com.per.epx.easytrain.models.sln.LineRoute;

import java.io.Serializable;
import java.util.List;

//Create by dai-jian 20180218
//Class for the recommend result got from service-desk
public class SolutionReply implements Serializable {
    private int replyCode;//Reply code for the ask route.
    private String message;//Reply message, it will be "ok" on success, or the error message on fail.
    private DepotLite from;//The station code of begin station
    private DepotLite to;//The station code of destination
    private long tripDate;//The early time to begin this trip
    private List<LineRoute> plans;//Result of solutions for trip to the ask
    private String version;//Protocol version

    public int getReplyCode() {
        return replyCode;
    }

    public void setReplyCode(int replyCode) {
        this.replyCode = replyCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public DepotLite getFrom() {
        return from;
    }

    public void setFrom(DepotLite from) {
        this.from = from;
    }

    public DepotLite getTo() {
        return to;
    }

    public void setTo(DepotLite to) {
        this.to = to;
    }

    public long getTripDate() {
        return tripDate;
    }

    public void setTripDate(long tripDate) {
        this.tripDate = tripDate;
    }

    public List<LineRoute> getPlans() {
        return plans;
    }

    public void setPlans(List<LineRoute> routeList) {
        this.plans = routeList;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
