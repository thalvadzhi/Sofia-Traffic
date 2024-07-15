package com.bearenterprises.sofiatraffic.restClient;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ScheduleRouteNewApi {
    @SerializedName("ext_id")
    public String extId;
    public int id;
    @SerializedName("line_id")
    public int lineId;
    public String name;
    @SerializedName("route_ref")
    public int routeRef;
    public int type;
    public ScheduleRouteDetails details;
    public List<Segment> segments;
}
