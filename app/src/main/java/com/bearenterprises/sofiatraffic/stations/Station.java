package com.bearenterprises.sofiatraffic.stations;

/**
 * Created by thalv on 01-Jul-16.
 */
public class Station {
    private String name, code, latitude, longtitute;

    public Station(String name, String code, String latitude, String longtitute) {
        this.name = name;
        this.code = code;
        this.latitude = latitude;
        this.longtitute = longtitute;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongtitute() {
        return longtitute;
    }
}
