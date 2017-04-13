package com.bearenterprises.sofiatraffic.utilities.parsing;

/**
 * Created by thalvadzhiev on 3/9/17.
 */

public class Description {
    private String transportationType, lineName, stopCode, direction;

    public Description(String transportationType, String lineName, String stopCode, String direction) {
        this.transportationType = transportationType;
        this.lineName = lineName;
        this.stopCode = stopCode;
        this.direction = direction;
    }

    public void setTransportationType(String transportationType) {
        this.transportationType = transportationType;
    }

    public void setLineName(String lineName) {
        this.lineName = lineName;
    }

    public void setStopCode(String stopCode) {
        this.stopCode = stopCode;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getTransportationType() {

        return transportationType;
    }

    public String getLineName() {
        return lineName;
    }

    public String getStopCode() {
        return stopCode;
    }

    public String getDirection() {
        return direction;
    }
}
