package com.per.epx.easytrain.models.req;

import java.io.Serializable;

public class LineBaseReply implements Serializable {
    private String lineCode;
    private long lineId;
    private String name;

    public LineBaseReply() {
    }

    public LineBaseReply(String lineCode, long lineId, String name) {
        this.lineCode = lineCode;
        this.lineId = lineId;
        this.name = name;
    }

    public String getLineCode() {
        return lineCode;
    }

    public void setLineCode(String lineCode) {
        this.lineCode = lineCode;
    }

    public long getLineId() {
        return lineId;
    }

    public void setLineId(long lineId) {
        this.lineId = lineId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
