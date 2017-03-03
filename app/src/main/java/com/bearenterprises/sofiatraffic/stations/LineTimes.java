package com.bearenterprises.sofiatraffic.stations;

import com.bearenterprises.sofiatraffic.restClient.Time;
import com.bearenterprises.sofiatraffic.restClient.second.Line;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by thalv on 02-Jul-16.
 */
public class LineTimes implements Serializable{
    private com.bearenterprises.sofiatraffic.restClient.second.Line line;
    private String type;
    private String times;
    private ArrayList<Time> vehicleTimes;

    public LineTimes(Line line, String type, String times, ArrayList<Time> vehicleTimes) {
        this.line = line;
        this.type = type;
        this.times = times;
        this.vehicleTimes = vehicleTimes;
    }

    public LineTimes(com.bearenterprises.sofiatraffic.restClient.second.Line line, String type) {
        this.line = line;
        this.type = type;
    }

    public com.bearenterprises.sofiatraffic.restClient.second.Line getLine() {
        return line;
    }

    public String getType() {
        return type;
    }

    public String getTimes() {
        if (vehicleTimes == null){
            return null;
        }
//        if(times == null){
//            times = generateTimes();
//        }
        return generateTimes();
    }

    public void setLine(com.bearenterprises.sofiatraffic.restClient.second.Line line) {
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
