package com.bearenterprises.sofiatraffic.restClient;

/**
 * Created by thalv on 29-Aug-16.
 */
public class Transport {
    private int type;
    private int id;
    private String name;

    public Transport(int type, int id, String name) {
        this.type = type;
        this.id = id;
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
