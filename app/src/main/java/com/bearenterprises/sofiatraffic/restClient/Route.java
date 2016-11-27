package com.bearenterprises.sofiatraffic.restClient;

import java.util.ArrayList;

/**
 * Created by thalv on 27-Nov-16.
 */

public class Route {
    private int id;
    private ArrayList<Station> route;

    public Route(int id, ArrayList<Station> route) {
        this.id = id;
        this.route = route;
    }

    public int getId() {
        return id;
    }

    public ArrayList<Station> getRoute() {
        return route;
    }
}
