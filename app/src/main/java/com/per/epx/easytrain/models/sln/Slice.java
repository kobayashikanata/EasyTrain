package com.per.epx.easytrain.models.sln;

import java.io.Serializable;

//A location that have train/bus/plane to stop
public class Slice implements Serializable{
    private final int order;
    private final String name;//The simplify station name         10086
    private String code;//The stop-point that arrived       10010
    private final long id;
    private final long arrivalTime;                            //     20:00
    private final long stayTime;                               //
    private final long driveTime;
    //private String code;//The station code
    //private String cityCode;//The city code that current station belong to
    //private String cityName;//The simplify name of current city

    public Slice(int order, long id, String name, String code, long arrivalTime, long driveTime, long stayTime) {
        this.id = id;
        this.order = order;
        this.name = name;
        this.code = code;
        this.arrivalTime = arrivalTime;
        this.stayTime = stayTime;
        this.driveTime = driveTime;
    }

    public String getName() {
        return name;
    }

    public int getOrder() {
        return order;
    }

    public long getArrivalTime() {
        return arrivalTime;
    }

    public long getDriveTime() {
        return driveTime;
    }

    public long getStayTime() {
        return stayTime;
    }

    public long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public void idToCodeIfNull(){
        if(getCode() == null || getCode().length() == 0){
            this.code = String.valueOf(getId());
        }
    }
}
