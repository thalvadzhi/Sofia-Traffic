package com.bearenterprises.sofiatraffic.restClient.schedules;

import com.bearenterprises.sofiatraffic.restClient.Time;

import java.util.ArrayList;

/**
 * Created by thalvadzhiev on 10/9/17.
 */

public class ScheduleTimes {
    private int scheduleId, routeId;
    private String routeName;
    private ArrayList<String> scheduleDayTypes;
    private ArrayList<Time> times;

    public int getScheduleId() {
        return scheduleId;
    }

    public int getRouteId() {
        return routeId;
    }

    public String getRouteName() {
        return routeName;
    }

    public ArrayList<String> getScheduleDayTypes() {
        return scheduleDayTypes;
    }

    public ArrayList<Time> getTimes() {
        return times;
    }
}
