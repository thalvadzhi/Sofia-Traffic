package com.bearenterprises.sofiatraffic.stations;

import java.io.Serializable;

/**
 * Created by thalv on 02-Jul-16.
 */
public class VehicleTimes implements Serializable{
    private String line;
    private String type;
    private String times;

    public VehicleTimes(String line, String type, String times) {
        this.line = line;
        this.type = type;
        this.times = times;
    }

    public String getLine() {
        return line;
    }

    public String getType() {
        return type;
    }

    public String getTimes() {
        return times;
    }
}
