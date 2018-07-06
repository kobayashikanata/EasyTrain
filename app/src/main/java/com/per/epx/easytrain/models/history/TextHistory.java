package com.per.epx.easytrain.models.history;

public class TextHistory extends HistoryItem {
    private String text;
    private int type;

    public TextHistory() {
    }

    public TextHistory(String text, int weight, int type) {
        super(weight);
        this.text = text;
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
