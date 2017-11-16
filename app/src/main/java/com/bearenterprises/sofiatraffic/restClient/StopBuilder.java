package com.bearenterprises.sofiatraffic.restClient;

import java.util.ArrayList;

/**
 * Created by thalvadzhiev on 5/3/17.
 */

public class StopBuilder {
    private Stop stop;

    public StopBuilder(){
        stop = new Stop();
    }

    public StopBuilder setFavouriteIndex(Integer index){
        stop.setFavouriteIndex(index);
        return this;
    }

    public StopBuilder setId(Integer id){
        stop.setId(id);
        return this;
    }

    public StopBuilder setCode(Integer code){
        stop.setCode(code);
        return this;
    }

    public StopBuilder setName(String name){
        stop.setName(name);
        return this;
    }

    public StopBuilder setLongtitude(String longtitude){
        stop.setLongitude(longtitude);
        return this;
    }

    public StopBuilder setLatitude(String latitude){
        stop.setLatitude(latitude);
        return this;
    }

    public StopBuilder setDescription(String description){
        stop.setDescription(description);
        return this;
    }

    public StopBuilder setAlias(String alias){
        stop.setAlias(alias);
        return this;
    }

    public StopBuilder setLines(ArrayList<Line> lines){
        stop.setLines(lines);
        return this;
    }

    public Stop build(){
        return stop;
    }
}
