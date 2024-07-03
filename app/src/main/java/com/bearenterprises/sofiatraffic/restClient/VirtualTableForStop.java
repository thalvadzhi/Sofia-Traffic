package com.bearenterprises.sofiatraffic.restClient;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VirtualTableForStop {
    @SerializedName("ext_id")
    public String externalId;
    @SerializedName("st_name")
    public String stopName;
    public Integer type;
    @SerializedName("route_name")
    public String routeName;
    @SerializedName("route_id")
    public Integer routeId;
    public String name;
    public Integer id;
    @SerializedName("details")
    public List<VirtualTableTime> times;
}
