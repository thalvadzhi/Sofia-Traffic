package com.bearenterprises.sofiatraffic.routesExpandableRecyclerView;

import com.bearenterprises.sofiatraffic.restClient.second.Stop;
import com.bignerdranch.expandablerecyclerview.model.Parent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thalvadzhiev on 4/13/17.
 */

public class Direction implements Parent<Stop> {
    private Stop from, to;
    private ArrayList<Stop> stops;
    private String transportationType;

    public String getTransportationType() {
        return transportationType;
    }

    public Stop getFrom() {

        return from;
    }

    public Stop getTo() {
        return to;
    }

    public Direction(ArrayList<Stop> stops, String trType) {
        this.stops = stops;
        this.transportationType = trType;
        if(stops != null && stops.size() > 0){
            this.from = this.stops.get(0);
            this.to = this.stops.get(this.stops.size() - 1);
        }
    }

    @Override
    public List<Stop> getChildList() {
        return stops;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return false;
    }

    public ArrayList<Stop> getStops() {
        return stops;
    }
}
