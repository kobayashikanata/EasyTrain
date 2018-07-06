package com.per.epx.easytrain.models.history;

import com.per.epx.easytrain.models.Depot;

public class SolutionAskHistory extends HistoryItem {
    private Depot from;
    private Depot to;

    public SolutionAskHistory(Depot from, Depot to) {
        this.from = from;
        this.to = to;
    }

    public SolutionAskHistory(Depot from, Depot to, int weight) {
        super(weight);
        this.from = from;
        this.to = to;
    }

    public Depot getFrom() {
        return from;
    }

    public void setFrom(Depot from) {
        this.from = from;
    }

    public Depot getTo() {
        return to;
    }

    public void setTo(Depot to) {
        this.to = to;
    }
}
