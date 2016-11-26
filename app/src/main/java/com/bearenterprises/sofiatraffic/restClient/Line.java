package com.bearenterprises.sofiatraffic.restClient;

/**
 * Created by thalv on 29-Aug-16.
 */
public class Line {
    private int id;
    private int type;
    private String name;

    public Line(int id, int type, String name) {
        this.id = id;
        this.type = type;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}
