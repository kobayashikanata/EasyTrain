package com.per.epx.easytrain.models.history;

import java.io.Serializable;

public class HistoryItem implements Serializable{
    private int weight;

    public HistoryItem() {
    }

    public HistoryItem(int weight) {
        this.weight = weight;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

}
