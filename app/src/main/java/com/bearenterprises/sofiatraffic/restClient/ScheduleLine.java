package com.bearenterprises.sofiatraffic.restClient;

import com.google.gson.annotations.SerializedName;

public class ScheduleLine {
    @SerializedName("ext_id")
    public String extId;
    @SerializedName("has_single_direction")
    public int hasSingleDirection;
    public int id;
    @SerializedName("is_active")
    public int isActive;
    public String name;
    @SerializedName("tr_color")
    public String transportationColor;
    @SerializedName("tr_icon")
    public String transportationIcon;
    @SerializedName("tr_name")
    public String transportationName;
    public int type;
}
