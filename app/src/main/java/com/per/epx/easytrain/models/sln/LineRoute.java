package com.per.epx.easytrain.models.sln;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

//Create by dai-jian 20180218
public class LineRoute implements Serializable {
    private Slice beginning;//The start-point of this route
    private Slice terminal;//The terminal of this route
    private List<Segment> runLines;//The stations will route pass
    private double payment;//Money may need to pay for route
    private String paymentUnit;//The unit ot payment

    public LineRoute() {
        this.runLines = new ArrayList<>();
    }

    public LineRoute(Slice beginning, Slice terminal, List<Segment> segments, int payment, String paymentUnit) {
        this.beginning = beginning;
        this.terminal = terminal;
        this.runLines = segments != null ? segments : new ArrayList<Segment>();
        this.payment = payment;
        this.paymentUnit = paymentUnit;
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

    public List<Segment> getRunLines() {
        return runLines;
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
