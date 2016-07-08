package com.bearenterprises.sofiatraffic.stations;

/**
 * Created by thalv on 02-Jul-16.
 */
public class Line {
    private String type, name, id;

    public Line(String type, String name, String id) {
        this.type = type;
        this.name = name;
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }
}
