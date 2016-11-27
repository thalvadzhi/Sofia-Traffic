package com.bearenterprises.sofiatraffic.restClient;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by thalv on 27-Nov-16.
 */

public class LineRoute {
    private Line line;
    private ArrayList<Route> routes;

    public LineRoute(Line line, ArrayList<Route> routes) {
        this.line = line;
        this.routes = routes;
    }

    public Line getLine() {
        return line;
    }

    public ArrayList<Route> getRoutes() {
        return routes;
    }
}
