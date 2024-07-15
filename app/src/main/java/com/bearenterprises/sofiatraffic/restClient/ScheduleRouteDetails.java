package com.bearenterprises.sofiatraffic.restClient;

import com.google.gson.annotations.SerializedName;

public class ScheduleRouteDetails {
    public String description;
    @SerializedName("continuous_drop_off")
    public int continuousDropOff;
    @SerializedName("continuous_pickup")
    public int continousPickup;
    public int id;
    @SerializedName("is_active")
    public int isActive;
    public String polyline;
    @SerializedName("route_id")
    public int routeId;
    public int type;
}
