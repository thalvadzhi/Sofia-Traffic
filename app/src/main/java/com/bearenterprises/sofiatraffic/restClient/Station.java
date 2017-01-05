package com.bearenterprises.sofiatraffic.restClient;

import java.util.ArrayList;

/**
 * Created by thalv on 29-Aug-16.
 */
public class Station {
    private int id;
    private int code;
    private String name;
    private String description;
    private ArrayList<com.bearenterprises.sofiatraffic.restClient.second.Line> lines;
    private String alias;

    public Station(int id, int code, String name, String description, ArrayList<com.bearenterprises.sofiatraffic.restClient.second.Line> lines) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.description = description;
        this.lines = lines;
    }

    public Station(int id, int code, String name){
        this.id = id;
        this.code = code;
        this.name = name;
        this.description = null;
        this.lines = null;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
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

    public ArrayList<com.bearenterprises.sofiatraffic.restClient.second.Line> getLines() {
        return lines;
    }
}
