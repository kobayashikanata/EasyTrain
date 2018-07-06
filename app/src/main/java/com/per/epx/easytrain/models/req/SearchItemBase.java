package com.per.epx.easytrain.models.req;

import com.per.epx.easytrain.models.DepotLite;

import java.io.Serializable;

public class SearchItemBase implements Serializable{
    private DepotLite from;//The station code of begin station
    private DepotLite to;//The station code of destination

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
}
