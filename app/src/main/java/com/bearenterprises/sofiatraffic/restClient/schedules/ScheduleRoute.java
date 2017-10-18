package com.bearenterprises.sofiatraffic.restClient.schedules;

import com.bearenterprises.sofiatraffic.restClient.Stop;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thalvadzhiev on 10/9/17.
 */

public class ScheduleRoute {
    private int scheduleId, routeId;
    private String routeName;
    private ArrayList<String> scheduleDayTypes;
    private List<Stop> stops;

    public int getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(int scheduleId) {
        this.scheduleId = scheduleId;
    }

    public int getRouteId() {
        return routeId;
    }

    public void setRouteId(int routeId) {
        this.routeId = routeId;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public ArrayList<String> getScheduleDayTypes() {
        return scheduleDayTypes;
    }

    public void setScheduleDayTypes(ArrayList<String> scheduleDayTypes) {
        this.scheduleDayTypes = scheduleDayTypes;
    }

    public List<Stop> getStops() {
        return stops;
    }

    public void setStops(List<Stop> stops) {
        this.stops = stops;
    }
}
