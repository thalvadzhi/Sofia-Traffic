package com.bearenterprises.sofiatraffic.restClient;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by thalvadzhiev on 11/3/17.
 */

public abstract class IStop {
    protected Integer favouriteIndex;
    protected String alias;
    protected Integer id;
    protected String name;
    @SerializedName(value="longitude", alternate={"longtitude"})
    protected String longitude;
    protected String latitude;

    public IStop(){

    }

    public IStop(String name, String longitude, String latitude) {
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public Integer getFavouriteIndex() {
        return favouriteIndex;
    }

    public void setFavouriteIndex(int favouriteIndex) {
        this.favouriteIndex = favouriteIndex;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    abstract List<String> getCoordinates();
    abstract String getName();
    abstract List<Integer> getCodes();
    //type could be NORMAL for trams, buses, trolleys and SUBWAY for subway stops
    abstract Integer getStopType();
}
