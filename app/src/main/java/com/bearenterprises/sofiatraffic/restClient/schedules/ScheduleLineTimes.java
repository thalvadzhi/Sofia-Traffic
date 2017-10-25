package com.bearenterprises.sofiatraffic.restClient.schedules;

import com.bearenterprises.sofiatraffic.restClient.Line;

import java.util.List;

/**
 * Created by thalvadzhiev on 10/10/17.
 */

public class ScheduleLineTimes {
    private int type;
    private String name;
    private List<ScheduleTimes> schedule;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ScheduleTimes> getSchedule() {
        return schedule;
    }

    public void setSchedule(List<ScheduleTimes> schedule) {
        this.schedule = schedule;
    }

}
