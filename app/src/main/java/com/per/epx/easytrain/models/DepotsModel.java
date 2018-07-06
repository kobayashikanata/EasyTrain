package com.per.epx.easytrain.models;

import java.util.List;

public class DepotsModel {
    private List<Depot> all;
    private List<Depot> hot;

    public DepotsModel() {
    }

    public DepotsModel(List<Depot> all, List<Depot> hot) {
        this.all = all;
        this.hot = hot;
    }

    public List<Depot> getHot() {
        return hot;
    }

    public void setHot(List<Depot> hot) {
        this.hot = hot;
    }

    public List<Depot> getAll() {
        return all;
    }

    public void setAll(List<Depot> all) {
        this.all = all;
    }
}
