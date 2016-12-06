package com.bearenterprises.sofiatraffic.stations;

import com.bearenterprises.sofiatraffic.restClient.Time;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by thalv on 02-Jul-16.
 */
public class VehicleTimes implements Serializable{
    private String line;
    private String type;
    private String times;
    private ArrayList<Time> vehicleTimes;

    public VehicleTimes(String line, String type, String times, ArrayList<Time> vehicleTimes) {
        this.line = line;
        this.type = type;
        this.times = times;
        this.vehicleTimes = vehicleTimes;
    }

    public VehicleTimes(String line, String type) {
        this.line = line;
        this.type = type;
    }

    public String getLine() {
        return line;
    }

    public String getType() {
        return type;
    }

    public String getTimes() {
        if (vehicleTimes == null){
            return null;
        }
        if(times == null){
            times = generateTimes();
        }
        return times;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ArrayList<Time> getVehicleTimes() {
        return vehicleTimes;
    }

    private String generateTimes(){
        if (vehicleTimes == null){
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for(Time t : vehicleTimes){
            builder.append(t.getTime());
            builder.append(" ");
        }
        return builder.toString();
    }

    public void setVehicleTimes(ArrayList<Time> vehicleTimes) {
        this.vehicleTimes = vehicleTimes;
    }
}
