package com.bearenterprises.sofiatraffic.restClient;

/**
 * Created by thalv on 29-Aug-16.
 */
public class Time {
    private String time;
    private boolean hasAc;
    private boolean hasPlatform;

    public Time(String time, boolean hasAc, boolean hasPlatform) {
        this.time = time;
        this.hasAc = hasAc;
        this.hasPlatform = hasPlatform;
    }

    public String getTime() {
        return time;
    }

    public boolean isHasAc() {
        return hasAc;
    }

    public boolean isHasPlatform() {
        return hasPlatform;
    }
}
