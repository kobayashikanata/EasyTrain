package com.per.epx.easytrain.models;

import com.per.epx.easytrain.models.history.HistoryItem;
import com.per.epx.easytrain.models.sln.LineRoute;

public class FavoriteItem extends HistoryItem {
    private LineRoute route;

    public FavoriteItem(LineRoute route) {
        this.route = route;
    }

    public FavoriteItem(LineRoute route, int weight) {
        super(weight);
        this.route = route;
    }

    public LineRoute getRoute() {
        return route;
    }

    public void setRoute(LineRoute route) {
        this.route = route;
    }
}