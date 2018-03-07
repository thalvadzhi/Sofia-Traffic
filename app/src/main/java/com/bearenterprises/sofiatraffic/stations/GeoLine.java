package com.bearenterprises.sofiatraffic.stations;

/**
 * Created by thalv on 07-Mar-18.
 */

public class GeoLine {
    private int type;
    private String firstStop, lastStop, geo, name;

    public GeoLine(int type, String firstStop, String lastStop, String geo, String name) {
        this.type = type;
        this.firstStop = firstStop;
        this.lastStop = lastStop;
        this.geo = geo;
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getFirstStop() {
        return firstStop;
    }

    public void setFirstStop(String firstStop) {
        this.firstStop = firstStop;
    }

    public String getLastStop() {
        return lastStop;
    }

    public void setLastStop(String lastStop) {
        this.lastStop = lastStop;
    }

    public String getGeo() {
        return geo;
    }

    public void setGeo(String geo) {
        this.geo = geo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
