package com.bearenterprises.sofiatraffic.restClient;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by thalv on 29-Aug-16.
 */
public class Station {
    private int id;
    private int code;
    private String name;
    private String description;
    private ArrayList<Line> lines;

    public Station(int id, int code, String name, String description, ArrayList<Line> lines) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.description = description;
        this.lines = lines;
    }

    public int getId() {
        return id;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public ArrayList<Line> getLines() {
        return lines;
    }
}
