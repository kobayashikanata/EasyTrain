package com.per.epx.easytrain.models;

import java.io.Serializable;

public class Pass implements Serializable{
    private int order;
    private String name;
    private long arrivalTime;
    private long driveTime;
    private long stayTime;

    public Pass() {
    }

    public Pass(int order, String name, long arrivalTime, long driveTime, long stayTime) {
        this.order = order;
        this.name = name;
        this.arrivalTime = arrivalTime;
        this.driveTime = driveTime;
        this.stayTime = stayTime;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(long arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public long getDriveTime() {
        return driveTime;
    }

    public void setDriveTime(long driveTime) {
        this.driveTime = driveTime;
    }

    public long getStayTime() {
        return stayTime;
    }

    public void setStayTime(long stayTime) {
        this.stayTime = stayTime;
    }
}
