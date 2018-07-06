package com.per.epx.easytrain.models.sln;

import java.io.Serializable;

//Create by dai-jian 20180218
//A route for trip
public class Segment implements Serializable {
    private long lineId;//The id of line
    private String lineCode;//The code of line
    private String name;//The name of this route
    private Slice beginning;//The start-point of this route
    private Slice terminal;//The terminal of this route
    private int seatType;
    private int crossSize;//The depot size will cross
    private double payment;//Money may need to pay for route
    private String paymentUnit;//The unit ot payment

    public Segment() {}

    public Segment(String lineCode,long lineId, String name, Slice beginning, Slice terminal, int seatType, int crossSize, int payment) {
        this.lineCode = lineCode;
        this.lineId = lineId;
        this.name = name;
        this.beginning = beginning;
        this.terminal = terminal;
        this.seatType = seatType;
        this.crossSize = crossSize;
        this.payment = payment;
    }

    public Segment(String lineCode, long lineId, String name, Slice beginning, Slice terminal, int seatType, int crossSize, int payment, String paymentUnit) {
        this.lineCode = lineCode;
        this.lineId = lineId;
        this.name = name;
        this.beginning = beginning;
        this.terminal = terminal;
        this.seatType = seatType;
        this.crossSize = crossSize;
        this.payment = payment;
        this.paymentUnit = paymentUnit;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Slice getBeginning() {
        return beginning;
    }

    public void setBeginning(Slice beginning) {
        this.beginning = beginning;
    }

    public Slice getTerminal() {
        return terminal;
    }

    public void setTerminal(Slice terminal) {
        this.terminal = terminal;
    }

    public int getCrossSize() {
        return crossSize;
    }

    public void setCrossSize(int crossSize) {
        this.crossSize = crossSize;
    }

    public int getSeatType() {
        return seatType;
    }

    public void setSeatType(int seatType) {
        this.seatType = seatType;
    }

    public double getPayment() {
        return payment;
    }

    public void setPayment(double payment) {
        this.payment = payment;
    }

    public String getPaymentUnit() {
        return paymentUnit;
    }

    public void setPaymentUnit(String paymentUnit) {
        this.paymentUnit = paymentUnit;
    }
}
