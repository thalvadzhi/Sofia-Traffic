package com.bearenterprises.sofiatraffic.restClient;

import com.google.gson.annotations.SerializedName;

public class VirtualTableTime {
    @SerializedName("t")
    public Integer time;
    public boolean ac;
    public boolean wheelchairs;
    public boolean bikes;
}
